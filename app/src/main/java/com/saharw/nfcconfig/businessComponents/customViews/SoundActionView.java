package com.saharw.nfcconfig.businessComponents.customViews;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.saharw.nfcconfig.R;
import com.saharw.nfcconfig.businessComponents.customViews.base.ActionViewBase;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sahar on 7/18/15.
 */
public class SoundActionView extends ActionViewBase{
    public static final String JSON_KEY_SET_SOUND_CHECKED = "setSoundChecked";
    private static final String TAG = "SoundActionView";
    private LayoutInflater mInflater;

    public SoundActionView(Context context, JSONObject params) {
        super(context, params);
    }

    @Override
    public RelativeLayout initFreeSection(Context context, JSONObject params) {
        RelativeLayout view = null;
        if(params != null){
            mInflater = LayoutInflater.from(context);
            view = (RelativeLayout) mInflater.inflate(R.layout.action_sound_layout, null);
            try {
                if(!params.isNull(params.getString(JSON_KEY_SET_SOUND_CHECKED))){
                    int checkedSetSound = params.getInt(JSON_KEY_SET_SOUND_CHECKED);
                    RadioButton setSoundRadioBtn = (RadioButton) view.findViewById(checkedSetSound);
                    if(setSoundRadioBtn != null && !setSoundRadioBtn.isChecked()){
                        setSoundRadioBtn.setChecked(true);
                    }

                }
            } catch (JSONException e) {
                Log.e(TAG, "unable to extract value from json", e);
            }
        }
        return view;
    }
}
