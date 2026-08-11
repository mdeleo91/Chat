package com.pocketai.app;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * The smallest possible camera-output provider: it serves exactly one path,
 * cache/cam/shot.jpg. The system camera app writes the full-size photo here
 * (EXTRA_OUTPUT must be a content:// URI on targetSdk 24+, and the usual
 * androidx FileProvider needs a compiled XML resource this build can't
 * produce — see android/README.md), then the WebView reads it back to hand
 * to the page's file input.
 */
public class PhotoProvider extends ContentProvider {

    static final String AUTHORITY = "com.pocketai.app.photos";

    static File shot(android.content.Context ctx) {
        File dir = new File(ctx.getCacheDir(), "cam");
        dir.mkdirs();
        return new File(dir, "shot.jpg");
    }

    @Override public boolean onCreate() { return true; }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        int m = "r".equals(mode)
                ? ParcelFileDescriptor.MODE_READ_ONLY
                : ParcelFileDescriptor.MODE_WRITE_ONLY
                  | ParcelFileDescriptor.MODE_CREATE
                  | ParcelFileDescriptor.MODE_TRUNCATE;
        return ParcelFileDescriptor.open(shot(getContext()), m);
    }

    @Override public String getType(Uri uri) { return "image/jpeg"; }

    // Nothing to query or mutate — the camera app only opens the stream.
    @Override public Cursor query(Uri u, String[] p, String s, String[] a, String o) { return null; }
    @Override public Uri insert(Uri u, ContentValues v) { return null; }
    @Override public int delete(Uri u, String s, String[] a) { return 0; }
    @Override public int update(Uri u, ContentValues v, String s, String[] a) { return 0; }
}
