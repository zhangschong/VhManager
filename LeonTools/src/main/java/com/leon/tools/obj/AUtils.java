package com.leon.tools.obj;

import android.util.Log;

/**
 * Created by leon.zhang on 2016/6/25.
 */
public class AUtils {
    private final static String TAG = "AUtils";
    private final static boolean IsDebug = true;

    public static void debug(boolean isDebug, String tag, Object... msgs) {
        if (IsDebug && isDebug) {
            StringBuilder builder = new StringBuilder();
            for (Object obj : msgs) {
                builder.append(obj);
            }
            Log.d(TAG + " " + tag, builder.toString());
        }
    }
}
