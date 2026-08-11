#!/usr/bin/env bash
# PocketAI APK build — no Android SDK required.
# Toolchain (all fetched from Maven Central; see android/README.md):
#   javac (JDK)                        -> Java bytecode
#   dalvik-dx (Jake Wharton repackage) -> classes.dex
#   android/axml.py                    -> binary AndroidManifest.xml
#   zip + apksig (v1+v2 signing)       -> signed APK
set -euo pipefail

VERSION_CODE="${VERSION_CODE:-1}"
VERSION_NAME="${VERSION_NAME:-0.1.0}"

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
AND="$ROOT/android"
TOOLS="${TOOLS:-/opt/android-build}"     # dx.jar, android-stub.jar, apksig.jar
OUT="$AND/out"
rm -rf "$OUT"; mkdir -p "$OUT/classes" "$OUT/stubs" "$OUT/apk/assets"

# 1) Version constants shared by Java and the manifest
mkdir -p "$OUT/gen/com/pocketai/app"
cat > "$OUT/gen/com/pocketai/app/BuildVersion.java" <<EOF
package com.pocketai.app;
public final class BuildVersion {
    public static final int CODE = $VERSION_CODE;
    public static final String NAME = "$VERSION_NAME";
    private BuildVersion() {}
}
EOF

# 2) Compile Java -> dex
#    android-stub.jar is API 16 (newest android.jar on Maven Central), so
#    framework classes added later are hand-stubbed in android/stubs. They go to
#    a separate output dir: on the compile classpath, never dexed, so the real
#    framework class wins at runtime.
javac -source 8 -target 8 -nowarn \
  -classpath "$TOOLS/android-stub.jar" \
  -d "$OUT/stubs" \
  $(find "$AND/stubs" -name '*.java')
# stubs first: WebChromeClient exists in the API 16 jar but without
# onShowFileChooser/FileChooserParams, so the stub has to shadow it here
javac -source 8 -target 8 -nowarn \
  -classpath "$OUT/stubs:$TOOLS/android-stub.jar" \
  -d "$OUT/classes" \
  $(find "$AND/src" -name '*.java') \
  "$OUT/gen/com/pocketai/app/BuildVersion.java"
java -cp "$TOOLS/dx.jar" com.android.dx.command.Main --dex \
  --min-sdk-version=24 --output="$OUT/apk/classes.dex" "$OUT/classes"

# 3) Binary manifest + assets
python3 "$AND/axml.py" "$OUT/apk/AndroidManifest.xml" "$VERSION_CODE" "$VERSION_NAME"
cp "$ROOT/app/index.html" "$OUT/apk/assets/index.html"

# 4) Zip -> sign (apksig does v1+v2; keystore committed for prototype
#    signature continuity so repo-downloaded updates install over old builds)
( cd "$OUT/apk" && zip -q -X -r ../unsigned.apk AndroidManifest.xml classes.dex assets )

KS="$AND/pocketai.keystore"
if [ ! -f "$KS" ]; then
  keytool -genkeypair -keystore "$KS" -alias pocketai -keyalg RSA -keysize 2048 \
    -validity 10950 -storepass pocketai -keypass pocketai \
    -dname "CN=PocketAI Prototype, OU=PocketAI, O=PocketAI, C=US"
fi

# v2-scheme signature (minSdk 24 => v1 not needed; apksig 2.3.0's v1 path
# is unusable on modern JDKs anyway)
javac -nowarn -cp "$TOOLS/apksig.jar" -d "$OUT" "$AND/tools/Sign.java" "$AND/tools/Verify.java"
java --add-exports java.base/sun.security.x509=ALL-UNNAMED --add-exports java.base/sun.security.pkcs=ALL-UNNAMED --add-exports java.base/sun.security.util=ALL-UNNAMED -cp "$TOOLS/apksig.jar:$OUT" Sign \
  "$KS" pocketai pocketai "$OUT/unsigned.apk" "$OUT/PocketAI.apk"
java --add-exports java.base/sun.security.x509=ALL-UNNAMED --add-exports java.base/sun.security.pkcs=ALL-UNNAMED --add-exports java.base/sun.security.util=ALL-UNNAMED -cp "$TOOLS/apksig.jar:$OUT" Verify "$OUT/PocketAI.apk"

cp "$OUT/PocketAI.apk" "$ROOT/releases/PocketAI.apk"
echo "Built releases/PocketAI.apk (versionCode=$VERSION_CODE, versionName=$VERSION_NAME)"
