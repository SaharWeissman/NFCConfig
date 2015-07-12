package com.saharw.nfcconfig;

import android.app.Application;
import android.util.Log;

/**
 * Created by sahar on 7/12/15.
 */
public class App extends Application {
    private static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }
}
