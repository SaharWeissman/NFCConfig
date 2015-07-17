package com.saharw.nfcconfig;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by sahar on 7/12/15.
 */
public class App extends Application {
    private static final String TAG = "App";
    public static Context sAppContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        sAppContext = this;
    }

    public static Context getAppContext(){
        return sAppContext;
    }
}
