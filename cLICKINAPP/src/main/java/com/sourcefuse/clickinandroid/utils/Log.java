package com.sourcefuse.clickinandroid.utils;

public class Log {

    private static final boolean enableLogging = true;

    public static void i(String tag, String msg) {
        if (enableLogging) {
            android.util.Log.i("" + tag, "" + msg);
        }
    }

    public static void e(String tag, String msg) {
        if (enableLogging) {
            Log.e("" + tag, "" + msg);
        }
    }

    public static void e(String tag, Throwable e) {
        if (enableLogging) {
            Log.e("" + tag, "" + e.getMessage());
            //e.printStackTrace();
        }
    }

    public static void e(String tag, String msg, Throwable e) {
        if (enableLogging) {
            Log.e("" + tag, "" + msg, e);
            //e.printStackTrace();
        }
    }

    public static void w(String tag, String msg) {
        if (enableLogging) {
            android.util.Log.w("" + tag, "" + msg);
        }
    }

    public static void w(String tag, Throwable e) {
        if (enableLogging) {
            android.util.Log.w("" + tag, "" + e.getMessage());
            e.printStackTrace();
        }
    }
}