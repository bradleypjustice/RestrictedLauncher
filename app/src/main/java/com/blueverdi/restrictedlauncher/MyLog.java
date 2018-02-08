package com.blueverdi.restrictedlauncher;

import android.util.Log;

public class MyLog {
    private static final boolean LOGGING = true; //false to disable logging

    public static void d(String tag, String message) {
        if (LOGGING) {
            Log.d(tag, message);
        }
    }
    public static void w(String tag, String message) {
        if (LOGGING) {
            Log.w(tag, message);
        }
    }
}
