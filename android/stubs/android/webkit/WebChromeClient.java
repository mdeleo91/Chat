package android.webkit;

import android.content.Intent;
import android.net.Uri;

/**
 * Compile-time stub. The real class ships with the framework; this exists only
 * because the API 16 android.jar on Maven Central predates
 * {@link #onShowFileChooser} (API 21) and its {@link FileChooserParams}
 * argument, which is a nested type and so cannot be stubbed on its own.
 *
 * Declares just enough for MainActivity to compile: the constructor it
 * subclasses and the file-chooser hook it overrides. Compiled to android/out/
 * stubs, which is on the compile classpath but never dexed, so the framework's
 * own WebChromeClient is what loads at runtime.
 */
public class WebChromeClient {

    public WebChromeClient() { }

    /** Added in API 21; called when the page opens an &lt;input type="file"&gt;. */
    public boolean onShowFileChooser(WebView webView,
                                     ValueCallback<Uri[]> filePathCallback,
                                     FileChooserParams fileChooserParams) {
        return false;
    }

    public static abstract class FileChooserParams {
        /** Intent that opens the system picker for the page's accept types. */
        public abstract Intent createIntent();

        /** True when the page's input carries the capture attribute (camera). */
        public abstract boolean isCaptureEnabled();

        /** Turns an onActivityResult back into the URI list the page expects. */
        public static Uri[] parseResult(int resultCode, Intent data) {
            return null;
        }
    }
}
