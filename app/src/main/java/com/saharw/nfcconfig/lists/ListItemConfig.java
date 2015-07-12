package com.saharw.nfcconfig.lists;

import android.graphics.Bitmap;

/**
 * Created by sahar on 7/12/15.
 */
public class ListItemConfig {
    public String mConfigName;
    public Bitmap mConfigIcon;

    public ListItemConfig(String configName, Bitmap configIcon){
        this.mConfigName = configName;
        this.mConfigIcon= configIcon;
    }
}
