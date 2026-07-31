import com.android.apksig.ApkSigner;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

/** Tiny driver around apksig: v1 + v2 signs an APK with a JKS keystore. */
public final class Sign {
    public static void main(String[] args) throws Exception {
        String ksPath = args[0], alias = args[1], pass = args[2];
        File in = new File(args[3]), out = new File(args[4]);

        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream fis = new FileInputStream(ksPath)) {
            ks.load(fis, pass.toCharArray());
        }
        PrivateKey key = (PrivateKey) ks.getKey(alias, pass.toCharArray());
        List<X509Certificate> certs =
            Collections.singletonList((X509Certificate) ks.getCertificate(alias));

        ApkSigner.SignerConfig signer =
            new ApkSigner.SignerConfig.Builder("POCKETAI", key, certs).build();
        new ApkSigner.Builder(Collections.singletonList(signer))
            .setInputApk(in)
            .setOutputApk(out)
            .setV1SigningEnabled(false)   // v1 done by jarsigner; JDK21 broke apksig 2.3.0 v1 path
            .setV2SigningEnabled(true)
            .build()
            .sign();
        System.out.println("signed " + out + " (" + out.length() + " bytes)");
    }
}
