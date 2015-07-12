package com.saharw.nfcconfig.Utils;

import android.app.Activity;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by sahar on 7/12/15.
 */
public class DisplayHelper {

    public static float getPxFromDP(Activity activity, int dp){
        Resources r = activity.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;
    }
}
