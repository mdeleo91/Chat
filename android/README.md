# PocketAI — Android build

Builds `releases/PocketAI.apk` **without the Android SDK** (the build environment
cannot reach `dl.google.com`, so `aapt2`/`d8`/`apksigner` are unavailable).
The pipeline substitutes each piece with tooling from Maven Central + the JDK:

| Step | Standard tool | Used here |
|---|---|---|
| Compile Java | javac + android.jar | `javac` + `com.google.android:android:4.1.1.4` (API stubs, Central) |
| Dex | d8 | `com.jakewharton.android.repackaged:dalvik-dx:9.0.0_r3` (Central) |
| Binary AndroidManifest.xml | aapt2 | [`axml.py`](axml.py) — minimal AXML encoder |
| Package | aapt2/zipflinger | `zip` |
| Sign | apksigner | `com.android.tools.build:apksig:2.3.0` (v2 scheme) via [`tools/Sign.java`](tools/Sign.java) |
| Verify | apksigner verify | apksig `ApkVerifier` via [`tools/Verify.java`](tools/Verify.java) + `pyaxmlparser` cross-check |

## Build

```bash
# one-time toolchain fetch (~14 MB)
mkdir -p /opt/android-build && cd /opt/android-build
curl -sSLO https://repo1.maven.org/maven2/com/jakewharton/android/repackaged/dalvik-dx/9.0.0_r3/dalvik-dx-9.0.0_r3.jar
curl -sSLO https://repo1.maven.org/maven2/com/google/android/android/4.1.1.4/android-4.1.1.4.jar
curl -sSLO https://repo1.maven.org/maven2/com/android/tools/build/apksig/2.3.0/apksig-2.3.0.jar
mv dalvik-dx-9.0.0_r3.jar dx.jar; mv android-4.1.1.4.jar android-stub.jar; mv apksig-2.3.0.jar apksig.jar

# build (bump both numbers for every release; update releases/version.json to match)
VERSION_CODE=2 VERSION_NAME=0.2.0 ./android/build.sh
```

## App shape

- `src/…/MainActivity.java` — full-screen WebView loading
  `assets/index.html?platform=apk&vc=<versionCode>&vn=<versionName>`.
  Non-`file:` URLs open in the system browser (that's how APK downloads work).
- `app/index.html` (repo root) is the **single source** for both the web app and
  the APK asset — the page reads the query string to decide whether to show
  "Download APK" (web) or "Check for updates" (APK).
- Update flow: the page fetches
  `https://raw.githubusercontent.com/mdeleo91/Chat/<branch>/releases/version.json`
  (tries `main`, then the dev branch until merged) and compares `versionCode`
  against its own. Newer ⇒ link to `apkUrl`.

## Release checklist

1. Edit the app / Java.
2. Bump `VERSION_CODE` (integer, always +1) and `VERSION_NAME`, rebuild.
3. Update `releases/version.json` with the same numbers + notes.
4. Commit `releases/PocketAI.apk` + `releases/version.json`, merge to `main`.
   Installed apps see the update on their next check.

## Constraints & caveats (prototype-grade, on purpose)

- **minSdk 24** (Android 7.0+): apksig 2.3.0's v1 signing uses JDK internals
  removed in modern JDKs, so the APK is v2-signed only — which Android accepts
  from 7.0 up. (`--add-exports` flags in `build.sh` keep the rest of apksig
  working on JDK 9+.)
- **`pocketai.keystore` is committed with a known password.** Deliberate for
  this prototype: Android only installs an update over an existing app when the
  signature matches, so the key must persist across builds and contributors.
  A real release key must never live in the repo.
- No launcher icon resource yet (Android shows the default glyph): icons need a
  `resources.arsc`, which is aapt2 territory — planned once real SDK access or
  a CI runner is available.
- The in-app "brain" is a scripted persona engine. The llama.cpp runtime from
  `docs/03-tech-stack.md` replaces it behind the same UI in a later milestone.
