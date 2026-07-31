package android.webkit;

/**
 * Compile-time-only stub for the API 26 framework class.
 *
 * The build compiles against com.google.android:android:4.1.1.4 (API 16 stubs —
 * the newest android.jar reachable from Maven Central), which predates
 * WebViewClient.onRenderProcessGone. This file exists purely so javac can
 * resolve the parameter type; build.sh compiles it into a separate directory
 * that is placed on the classpath but never dexed, so the real framework class
 * is used at runtime.
 */
public abstract class RenderProcessGoneDetail {
    public abstract boolean didCrash();
    public abstract int rendererPriorityAtExit();
}
