package com.gpp.music_30;

import android.util.Log;

/**
 * Created by Administrator on 2017/5/10.
 */

public class Gpp {
    public static final String TAG = "gpp";
    private static boolean isShow=true;

    public static void v(String msg) {
        if (isShow) {
            Log.v(TAG, msg);
        }
    }
    public static void e(String msg) {
        if (isShow) {
            Log.e(TAG, msg);
        }
    }
}
