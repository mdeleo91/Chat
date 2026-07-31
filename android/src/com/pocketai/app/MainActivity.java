package com.pocketai.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * PocketAI Android shell: a full-screen WebView hosting the bundled app
 * (assets/index.html). The page detects the platform from its query string;
 * external links (APK downloads, GitHub) open in the system browser.
 */
public class MainActivity extends Activity {

    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_DeviceDefault_NoActionBar);
        buildWebView(null);
    }

    /**
     * Creates the WebView and loads the app. Called at startup and again after
     * the renderer process dies — a dead WebView can never be reused, so
     * recovery means throwing it away and building a fresh one.
     *
     * @param crashReason null on a normal start, otherwise passed to the page
     *                    as ?rcrash= so it can log the crash and stop
     *                    re-loading whatever model killed it.
     */
    private void buildWebView(String crashReason) {
        web = new WebView(this);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);           // localStorage for chats/memories
        s.setAllowFileAccess(true);
        // Allow the file:// page to fetch the update manifest from raw.githubusercontent.com
        s.setAllowUniversalAccessFromFileURLs(true);

        // Without a WebChromeClient, WebView suppresses JS dialogs entirely —
        // confirm() returns false, breaking the 18+ gate and delete/wipe flows.
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("file:")) return false;
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } catch (Exception ignored) { }
                return true;
            }

            /**
             * The renderer process died — almost always Android's low-memory
             * killer reclaiming it while WebLLM holds a multi-gigabyte model in
             * GPU memory. Without this override the framework tears down the
             * whole app process, which is what a user sees as "the app
             * crashed". Returning true keeps the app alive so we can rebuild
             * the WebView in place and tell the page what happened.
             *
             * No @Override: the API 16 stubs this module compiles against
             * predate the method (added in API 26). The signature still matches
             * the framework's, so it overrides at runtime on 8.0+; on 7.x it is
             * simply never called and the old behaviour applies.
             */
            public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
                // didCrash() false => the system killed it, i.e. out of memory.
                boolean didCrash = true;
                if (Build.VERSION.SDK_INT >= 26 && detail != null) didCrash = detail.didCrash();

                if (web != null) {
                    ViewGroup parent = (ViewGroup) web.getParent();
                    if (parent != null) parent.removeView(web);
                    web.destroy();
                    web = null;
                }
                buildWebView(didCrash ? "crash" : "oom");
                return true;
            }
        });

        setContentView(web);
        String url = "file:///android_asset/index.html?platform=apk&vc="
                + BuildVersion.CODE + "&vn=" + BuildVersion.NAME;
        if (crashReason != null) url += "&rcrash=" + crashReason;
        web.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (web != null && web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }
}
