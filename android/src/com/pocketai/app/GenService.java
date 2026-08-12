package com.pocketai.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;

/**
 * Foreground service held while the page has a reply or photo generating.
 * Android freezes backgrounded apps and severs their connections — this is
 * the sanctioned way to say "still working": with it, a slow generation
 * survives the screen locking or the user switching apps. Started/stopped
 * by the page through the PocketShell JS bridge; belt-and-braces timeouts
 * below make sure it can never outlive a wedged page by more than minutes.
 */
public class GenService extends Service {

    private static final String CHANNEL = "gen";
    private static final int NOTE_ID = 41;
    /** Nothing legitimate generates this long — self-stop backstop. */
    private static final long MAX_MS = 6 * 60 * 1000;

    private PowerManager.WakeLock lock;
    private final Handler handler = new Handler();
    private final Runnable expire = new Runnable() {
        @Override public void run() { stopSelf(); }
    };

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            startForeground(NOTE_ID, buildNote());
        } catch (Throwable ignored) {
            // no notification => no foreground exemption, but the service
            // itself still buys ordinary-priority time; don't crash over it
        }
        try {
            if (lock == null) {
                PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "pocketai:gen");
                lock.setReferenceCounted(false);
            }
            lock.acquire(MAX_MS);
        } catch (Throwable ignored) { }
        handler.removeCallbacks(expire);
        handler.postDelayed(expire, MAX_MS);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(expire);
        try { if (lock != null && lock.isHeld()) lock.release(); } catch (Throwable ignored) { }
        super.onDestroy();
    }

    /**
     * The channel and the channel-aware Builder constructor are API 26 —
     * newer than the API 16 stub jar this module compiles against — so both
     * go through reflection; 24/25 use the legacy builder directly.
     */
    private Notification buildNote() throws Exception {
        Notification.Builder b;
        if (Build.VERSION.SDK_INT >= 26) {
            Object ch = Class.forName("android.app.NotificationChannel")
                    .getConstructor(String.class, CharSequence.class, int.class)
                    .newInstance(CHANNEL, "Replies in progress", 2 /* IMPORTANCE_LOW */);
            NotificationManager nm =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.getClass().getMethod("createNotificationChannel", ch.getClass())
                    .invoke(nm, ch);
            b = (Notification.Builder) Notification.Builder.class
                    .getConstructor(Context.class, String.class)
                    .newInstance(this, CHANNEL);
        } else {
            b = new Notification.Builder(this);
        }
        // no resources.arsc in this build, so the icon is a system drawable
        return b.setSmallIcon(android.R.drawable.stat_notify_sync)
                .setContentTitle("PocketAI")
                .setContentText("Waiting on a reply — safe to switch away")
                .setOngoing(true)
                .build();
    }
}
