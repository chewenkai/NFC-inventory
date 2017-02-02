package com.kevin.rfidmanager.Utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Kevin on 2017/2/2.
 */

public class ScreenUtil {
    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
