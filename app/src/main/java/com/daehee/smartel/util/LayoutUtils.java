package com.daehee.smartel.util;

import android.content.Context;

/**
 * Created by daehee on 2017. 8. 9..
 */

public class LayoutUtils {
    Context context;

    public LayoutUtils(Context context) {
        this.context = context;
    } // Constructor

    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

} // end of class
