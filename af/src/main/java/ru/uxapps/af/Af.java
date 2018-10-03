package ru.uxapps.af;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

public class Af {

    private static int sCode = 1;

    public static int code() {
        return sCode++;
    }

    public static <T> T ifNull(T value, T ifNull) {
        return value != null ? value : ifNull;
    }

    public static boolean isUi() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static void bg(Runnable r) {
        if (isUi()) AsyncTask.THREAD_POOL_EXECUTOR.execute(r);
        else r.run();
    }

    public static void ui(Runnable r) {
        ui(r, 0);
    }

    public static void ui(Runnable r, long delayMs) {
        new Handler(Looper.getMainLooper()).postDelayed(r, delayMs);
    }

}
