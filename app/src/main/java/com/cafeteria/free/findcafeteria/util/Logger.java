package com.cafeteria.free.findcafeteria.util;

import android.util.Log;

public class Logger {
    private static final String LOG_TAG = "CAFETERIA";
    private static final String FORMAT = "[%s]: %s";

    public static void v(String msg) {
        Log.v(LOG_TAG, String.format(FORMAT, getCallerInfo(), msg));
    }

    public static void d(String msg) {
//        Log.d(LOG_TAG, String.format(FORMAT, getCallerInfo(), msg));
    }

    public static void l(String msg) {
        Log.i(LOG_TAG, String.format(FORMAT, getCallerInfo(), msg));
    }

    public static void w(String msg) {
        Log.d(LOG_TAG, String.format(FORMAT, getCallerInfo(), msg));
    }

    public static void e(String msg) {
        Log.e(LOG_TAG, String.format(FORMAT, getCallerInfo(), msg));
    }

    private static String getCallerInfo() {
        StackTraceElement[] elements = new Exception().getStackTrace();
        String className = elements[2].getClassName();
        return className.substring(className.lastIndexOf(".") + 1, className.length()) + "_" + elements[2].getLineNumber();
    }
}