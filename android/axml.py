#!/usr/bin/env python3
"""Minimal Android binary XML (AXML) encoder — builds AndroidManifest.xml
without aapt/aapt2 (which need dl.google.com, unavailable in this build env).

Emits the chunk structure the Android framework's ResXMLTree expects:
  XML header -> string pool (UTF-16) -> resource map -> namespace/elements.
Attribute-name strings sit first in the pool, aligned 1:1 with the resource
map; android:-namespaced attributes are sorted by resource id within each
element (AttributeResolution::RetrieveAttributes assumes ascending order).
"""
import struct, sys

ANDROID_NS = "http://schemas.android.com/apk/res/android"

# Framework attr resource ids (frameworks/base public.xml, stable ABI)
ATTR_IDS = {
    "theme": 0x01010000, "label": 0x01010001, "icon": 0x01010002,
    "name": 0x01010003, "exported": 0x01010010, "authorities": 0x01010018,
    "grantUriPermissions": 0x0101001B, "launchMode": 0x0101001D,
    "configChanges": 0x0101001F, "minSdkVersion": 0x0101020C,
    "versionCode": 0x0101021B, "versionName": 0x0101021C,
    "windowSoftInputMode": 0x0101022B, "targetSdkVersion": 0x01010270,
    "hardwareAccelerated": 0x010102D3, "usesCleartextTraffic": 0x010104EC,
}

TYPE_STRING, TYPE_INT_DEC, TYPE_INT_HEX, TYPE_BOOL = 0x03, 0x10, 0x11, 0x12


class Attr:
    def __init__(self, name, value, ns=None):
        self.name, self.value, self.ns = name, value, ns
    @property
    def res_id(self):
        return ATTR_IDS.get(self.name) if self.ns == ANDROID_NS else None


class Elem:
    def __init__(self, tag, attrs=(), children=()):
        self.tag, self.children = tag, list(children)
        # android attrs sorted by resource id first, then plain attrs
        a = list(attrs)
        self.attrs = sorted([x for x in a if x.res_id], key=lambda x: x.res_id) + \
                     [x for x in a if not x.res_id]


class Writer:
    def __init__(self, root):
        self.root = root
        self.strings = []           # pool
        self.index = {}
        # 1) attribute names with resource ids FIRST, pool order == map order
        self.res_map = []
        def collect_attr_names(e):
            for at in e.attrs:
                if at.res_id is not None and at.name not in self.index:
                    self.index[at.name] = len(self.strings)
                    self.strings.append(at.name)
                    self.res_map.append(at.res_id)
            for c in e.children:
                collect_attr_names(c)
        collect_attr_names(root)
        # 2) everything else
        def intern(s):
            if s not in self.index:
                self.index[s] = len(self.strings)
                self.strings.append(s)
            return self.index[s]
        self.intern = intern
        intern(ANDROID_NS); intern("android")
        def collect_rest(e):
            intern(e.tag)
            for at in e.attrs:
                intern(at.name)
                if isinstance(at.value, str):
                    intern(at.value)
            for c in e.children:
                collect_rest(c)
        collect_rest(root)

    # ---- string pool (UTF-16LE) ----
    def string_pool(self):
        offsets, data = [], b""
        for s in self.strings:
            offsets.append(len(data))
            enc = s.encode("utf-16-le")
            data += struct.pack("<H", len(s)) + enc + b"\x00\x00"
        while len(data) % 4:
            data += b"\x00"
        n = len(self.strings)
        header_size = 28
        strings_start = header_size + 4 * n
        total = strings_start + len(data)
        out = struct.pack("<HHIIIIII", 0x0001, header_size, total, n, 0, 0,
                          strings_start, 0)
        out += b"".join(struct.pack("<I", o) for o in offsets)
        return out + data

    def resource_map(self):
        body = b"".join(struct.pack("<I", rid) for rid in self.res_map)
        return struct.pack("<HHI", 0x0180, 8, 8 + len(body)) + body

    def typed_value(self, at):
        v = at.value
        if isinstance(v, bool):
            return TYPE_BOOL, (0xFFFFFFFF if v else 0), 0xFFFFFFFF
        if isinstance(v, int):
            return TYPE_INT_DEC, v & 0xFFFFFFFF, 0xFFFFFFFF
        if isinstance(v, tuple) and v[0] == "hex":
            return TYPE_INT_HEX, v[1] & 0xFFFFFFFF, 0xFFFFFFFF
        idx = self.index[v]
        return TYPE_STRING, idx, idx          # rawValue = same string

    def element(self, e):
        ns_none = 0xFFFFFFFF
        name_idx = self.index[e.tag]
        out = b""
        # start element
        attr_bytes = b""
        for at in e.attrs:
            dtype, data, raw = self.typed_value(at)
            ns_idx = self.index[at.ns] if at.ns else ns_none
            attr_bytes += struct.pack("<IIIHBBI", ns_idx, self.index[at.name],
                                      raw, 8, 0, dtype, data)
        size = 36 + len(attr_bytes)
        out += struct.pack("<HHIII", 0x0102, 16, size, 1, 0xFFFFFFFF)
        out += struct.pack("<IIHHHHHH", ns_none, name_idx, 0x14, 0x14,
                           len(e.attrs), 0, 0, 0)
        out += attr_bytes
        for c in e.children:
            out += self.element(c)
        out += struct.pack("<HHIIIII", 0x0103, 16, 24, 1, 0xFFFFFFFF,
                           ns_none, name_idx)
        return out

    def build(self):
        body = self.string_pool() + self.resource_map()
        pre = struct.pack("<HHIIIII", 0x0100, 16, 24, 1, 0xFFFFFFFF,
                          self.index["android"], self.index[ANDROID_NS])
        post = struct.pack("<HHIIIII", 0x0101, 16, 24, 1, 0xFFFFFFFF,
                           self.index["android"], self.index[ANDROID_NS])
        body += pre + self.element(self.root) + post
        return struct.pack("<HHI", 0x0003, 8, 8 + len(body)) + body


def A(name, value):
    return Attr(name, value, ANDROID_NS)


def build_manifest(package, version_code, version_name):
    CONFIG = ("hex", 0x80 | 0x20 | 0x100 | 0x400)   # orientation|keyboardHidden|screenLayout|screenSize
    root = Elem("manifest",
        [A("versionCode", version_code), A("versionName", version_name),
         Attr("package", package)],
        [
          Elem("uses-sdk", [A("minSdkVersion", 24), A("targetSdkVersion", 28)]),
          Elem("uses-permission", [A("name", "android.permission.INTERNET")]),
          Elem("application",
            # cleartext: reach a LAN model server at http://192.168.x.x — targetSdk 28
            # blocks plain HTTP otherwise
            [A("label", "PocketAI"), A("hardwareAccelerated", True),
             A("usesCleartextTraffic", True)],
            [
              Elem("activity",
                [A("name", package + ".MainActivity"), A("exported", True),
                 A("launchMode", 1),                 # singleTop
                 A("configChanges", CONFIG),
                 A("windowSoftInputMode", ("hex", 0x10))],  # adjustResize
                [Elem("intent-filter", [], [
                    Elem("action", [A("name", "android.intent.action.MAIN")]),
                    Elem("category", [A("name", "android.intent.category.LAUNCHER")]),
                ])]),
              # camera output target ("Take a photo"): the system camera writes
              # here via a per-package grant from MainActivity.startCamera()
              Elem("provider",
                [A("name", package + ".PhotoProvider"),
                 A("exported", False),
                 A("authorities", package + ".photos"),
                 A("grantUriPermissions", True)])
            ])
        ])
    return Writer(root).build()


if __name__ == "__main__":
    out, vc, vn = sys.argv[1], int(sys.argv[2]), sys.argv[3]
    blob = build_manifest("com.pocketai.app", vc, vn)
    with open(out, "wb") as f:
        f.write(blob)
    print("wrote %s (%d bytes)" % (out, len(blob)))
