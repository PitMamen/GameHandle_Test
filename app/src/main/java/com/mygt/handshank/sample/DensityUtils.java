package com.mygt.handshank.sample;

import android.content.Context;

/**
 * Created by chengkai on 2017/1/11.
 */
public class DensityUtils {
    public DensityUtils() {
    }

    public static int dip2px(Context context, float dpValue) {
        return (int)(dpValue * getDensity(context) + 0.5F);
    }

    public static int px2dip(Context context, float pxValue) {
        return (int)(pxValue / getDensity(context) + 0.5F);
    }

    public static int sp2px(Context context, float spValue) {
        return (int)((double)(spValue * getFontDensity(context)) + 0.5D);
    }

    public static int px2sp(Context context, float pxValue) {
        return (int)((double)(pxValue / getFontDensity(context)) + 0.5D);
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static float getFontDensity(Context context) {
        return context.getResources().getDisplayMetrics().scaledDensity;
    }
}
