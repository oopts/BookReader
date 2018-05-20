package com.example.texttest.View;

import android.util.Log;

public class ELogger {
    private static ITxtReaderLoggerListener l;
    public static void setLoggerListener(ITxtReaderLoggerListener l) {
        ELogger.l = l;
    }
    public static void log(String tag, String msg) {
        Log.e(tag, msg + "");
        if (l != null) {
            l.onLog(tag, msg + "");
        }
    }

}
