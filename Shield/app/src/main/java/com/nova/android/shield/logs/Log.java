package com.nova.android.shield.logs;


import com.nova.android.shield.main.ShieldApp;

public class Log {
    public static void d(String str, String str2) {
        if (ShieldApp.debug) {
            android.util.Log.d(str, str2);
        }
    }

    public static void e(String str, String str2) {
        if (ShieldApp.debug) {
            android.util.Log.e(str, str2);
        }
    }

    public static void i(String str, String str2) {
        if (ShieldApp.debug) {
            android.util.Log.i(str, str2);
        }
    }

    public static void v(String str, String str2) {
        if (ShieldApp.debug) {
            android.util.Log.v(str, str2);
        }
    }

    public static void w(String str, String str2) {
        if (ShieldApp.debug) {
            android.util.Log.w(str, str2);
        }
    }

    public static void e(String str, String str2, Throwable th) {
        if (ShieldApp.debug) {
            android.util.Log.e(str, str2, th);
        }
    }
}
