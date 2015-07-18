package com.saharw.nfcconfig.asyncTasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.saharw.nfcconfig.R;
import com.saharw.nfcconfig.Utils.ImageUtil;

/**
 * Created by sahar on 7/17/15.
 */
public class DecodeBitmapAsync extends AsyncTask<String, Integer, Bitmap> {

    private final String TAG = "DecodeBitmapAsync";

    @Override
    protected void onPreExecute() { // on UI thread
        super.onPreExecute();
        Log.d(TAG, "onPreExecute");
    }

    @Override
    protected Bitmap doInBackground(String... params) { // on bkgd thread
        Log.d(TAG, "doInBackground: loading bitmap from path: " + params);
        Bitmap bitmap = ImageUtil.tryReadImgFromPath(params[0]);
        if(bitmap == null){
            Log.d(TAG, "unable to read bitmap from: " + params[0] + ", display error image instead");
            bitmap = ImageUtil.tryReadImgFromResource(R.drawable.error);
        }
        return bitmap;
    }

    @Override
    protected void onProgressUpdate(Integer... values) { // ui thread
        super.onProgressUpdate(values);
        Log.d(TAG, "onProgressUpdate");
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) { // ui thread
        super.onPostExecute(bitmap);
        Log.d(TAG, "onPostExecute: bitmap = " + bitmap);
    }
}
