package com.pocketai.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.ValueCallback;
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

    /** Pending <input type="file"> callback, held while the picker is open. */
    private ValueCallback<Uri[]> filePicker;
    private static final int REQ_FILE = 7;

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
        // It also has to forward <input type="file"> to the system picker, or
        // choosing a contact photo silently does nothing.
        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView view,
                                             ValueCallback<Uri[]> callback,
                                             FileChooserParams params) {
                if (filePicker != null) filePicker.onReceiveValue(null);
                filePicker = callback;
                try {
                    startActivityForResult(params.createIntent(), REQ_FILE);
                    return true;
                } catch (Exception e) {
                    // no picker on the device — let the page know rather than
                    // leaving it waiting on a callback that never fires
                    filePicker = null;
                    return false;
                }
            }
        });
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

    /** Hands the chosen image back to the waiting page. */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_FILE) {
            if (filePicker != null) {
                filePicker.onReceiveValue(
                        WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                filePicker = null;
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (web != null && web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }
}
