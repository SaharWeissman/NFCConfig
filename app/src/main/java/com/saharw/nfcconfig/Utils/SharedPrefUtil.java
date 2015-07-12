package com.saharw.nfcconfig.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sahar on 7/12/15.
 */
public class SharedPrefUtil {

    private static final String TAG = "SharedPrefUtil";


    public static final String KEY_CONFIG_LIST = "config_list";

    private final Context mContext;
    private final String mFileName;
    private final SharedPreferences mSharedPref;
    private final SharedPreferences.Editor mSharedPrefEditor;

    public SharedPrefUtil(Context context, String fileName){
        mContext = context;
        mFileName = fileName;
        mSharedPref = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        mSharedPrefEditor = mSharedPref.edit();
    }

    public boolean writeJSONArray(JSONArray jsonArray){
        boolean success = false;
        if(jsonArray != null && jsonArray.length() != 0){
            try{
                mSharedPrefEditor.putString(KEY_CONFIG_LIST, jsonArray.toString());
                mSharedPrefEditor.commit();
                success = true;
            }catch (Exception e){
                Log.d(TAG, "unable to write JSON");
                success = false;
            }
        }
        return success;
    }

    public JSONArray getJSONArray(String key){
        JSONArray res = null;
        if(!TextUtils.isEmpty(key)){
            String jsonStr = mSharedPref.getString(KEY_CONFIG_LIST, "");
            try {
                res = new JSONArray(jsonStr);
            } catch (JSONException e) {
                Log.d(TAG, "unable to getJSON");
                res = null;
            }
        }
        return res;
    }
}
