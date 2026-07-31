import com.android.apksig.ApkVerifier;

import java.io.File;

/** Verifies the signed APK the way an Android device would (v1 + v2). */
public final class Verify {
    public static void main(String[] args) throws Exception {
        ApkVerifier.Result r = new ApkVerifier.Builder(new File(args[0])).build().verify();
        System.out.println("verified=" + r.isVerified()
                + " v1=" + r.isVerifiedUsingV1Scheme()
                + " v2=" + r.isVerifiedUsingV2Scheme());
        for (ApkVerifier.IssueWithParams e : r.getErrors()) System.out.println("ERROR: " + e);
        for (ApkVerifier.IssueWithParams w : r.getWarnings()) System.out.println("warn: " + w);
        if (!r.isVerified()) System.exit(1);
    }
}
