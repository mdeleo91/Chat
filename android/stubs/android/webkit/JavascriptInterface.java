package android.webkit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Compile-only stub: added in API 17, so absent from the API 16 stub jar.
 * Never dexed — at runtime the annotation resolves by name to the
 * framework's real one, which is what makes addJavascriptInterface expose
 * the annotated methods on targetSdk >= 17.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface JavascriptInterface {
}
