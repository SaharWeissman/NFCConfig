package com.saharw.nfcconfig.businessComponents.customViews.base;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.saharw.nfcconfig.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sahar on 7/18/15.
 */
public abstract class ActionViewBase extends RelativeLayout {
    private static final String JSON_KEY_ACTION_NAME = "action_name";
    private static final String TAG = "ActionView";
    private LayoutInflater mInflater;

    public ActionViewBase(Context context, JSONObject params) {
        super(context);
        mInflater = LayoutInflater.from(context);
        init(context, params);
    }

    public ActionViewBase(Context context, AttributeSet attrs, int defStyle, JSONObject params){
        super(context, attrs, defStyle);
        mInflater = LayoutInflater.from(context);
        init(context, params);
    }

    public ActionViewBase(Context context, AttributeSet attrs, JSONObject params) {
        super(context, attrs);
        mInflater = LayoutInflater.from(context);
        init(context, params);
    }

    public void init(Context context, JSONObject params){
        View v =  mInflater.inflate(R.layout.action_view_base, null);

        // set up action name
        TextView txtHeader = (TextView) v.findViewById(R.id.action_header);
        if(txtHeader != null){
            if(!params.isNull(JSON_KEY_ACTION_NAME)){
                try {
                    txtHeader.setText(params.getString(JSON_KEY_ACTION_NAME));
                } catch (JSONException e) {
                    Log.e(TAG, "unable to set action name", e);
                }
            }
        }
        RelativeLayout freeSection = (RelativeLayout)v.findViewById(R.id.action_free_section);
        if(freeSection != null){
            freeSection = initFreeSection(context, params);
        }
        invalidate();
    }

    public abstract RelativeLayout initFreeSection(Context context, JSONObject params);
}
