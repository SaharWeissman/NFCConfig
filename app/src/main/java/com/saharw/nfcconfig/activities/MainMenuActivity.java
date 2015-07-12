package com.saharw.nfcconfig.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.saharw.nfcconfig.R;
import com.saharw.nfcconfig.Utils.SharedPrefUtil;
import com.saharw.nfcconfig.lists.ListItemConfig;
import com.saharw.nfcconfig.lists.adapters.ConfigAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainMenuActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener, DialogInterface.OnDismissListener {

    public static final String TAG = "MainMenuActivity";
    private static final String SHARED_PREF_FILENAME = "NfcConfigPref";
    private static final String JSON_KEY_CONFIG_NAME = "config_name";
    private static final String JSON_KEY_CONFIG_ICON_PATH = "config_icon_path";

    // UI Components
    private Button mBtnAdd, mBtnEdit;
    private ListView mConfigList;
    private ConfigAdapter mListAdapter;
    private ArrayList<ListItemConfig> mListItems;
    private SharedPrefUtil mSharedPrefUtils;
    private AlertDialog mDialogAddConfig;
    private EditText mEditTxtConfigName;
    private Button mBtnSave;
    private Button mBtnChooseIcon;
    private ImageView mConfigIconImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPrefUtils = new SharedPrefUtil(this, SHARED_PREF_FILENAME);
        initUIComponents();
        populateList();
    }

    private void populateList() {
        mListItems = extractListData();
        mListAdapter = new ConfigAdapter(this, mListItems == null ? new ArrayList<ListItemConfig>(0) : mListItems);
        mConfigList.setAdapter(mListAdapter);
        refreshList();
    }

    private void refreshList() {
        mListAdapter.notifyDataSetChanged();
    }

    private ArrayList<ListItemConfig> extractListData() {
        //currently in SharedPref
        ArrayList<ListItemConfig> listItems = new ArrayList<ListItemConfig>();
        JSONArray data = mSharedPrefUtils.getJSONArray(SharedPrefUtil.KEY_CONFIG_LIST);
        if(data != null){
            for(int i = 0; i < data.length(); i++){
                try {
                    JSONObject itemJson = data.getJSONObject(i);

                    //try create item from json
                    ListItemConfig itemConfig = new ListItemConfig(itemJson.getString(JSON_KEY_CONFIG_NAME),
                            getBitmapFromPath(itemJson.getString(JSON_KEY_CONFIG_ICON_PATH)));
                    listItems.add(itemConfig);
                } catch (JSONException e) {
                    Log.e(TAG, "unable to extract item #" + i);
                }
            }
        }
        return listItems;
    }

    private Bitmap getBitmapFromPath(String string) {
        //TODO: read bitmap using path
        return null;
    }

    private void initUIComponents() {
        mBtnAdd = (Button) findViewById(R.id.btn_add_config);
        mBtnAdd.setOnClickListener(this);

        mBtnEdit = (Button)findViewById(R.id.btn_edit_list);
        mBtnEdit.setOnClickListener(this);

        mConfigList = (ListView)findViewById(R.id.lstV_config_list);
        mConfigList.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v != null){
            int id = v.getId();
            switch (id){
                case R.id.btn_add_config:{
                    Log.d(TAG, "case add config");
                    showAddConfigDialog();
                    break;
                }
                case R.id.btn_edit_list:{
                    Log.d(TAG, "case edit list");
                    break;
                }
                default:{
                    Log.d(TAG, "default case");
                    break;
                }
            }
        }
    }

    private void showAddConfigDialog() {
        final Dialog addConfigDialog = new Dialog(this);
        addConfigDialog.setContentView(R.layout.dialog_add_config);
        addConfigDialog.setOnDismissListener(this);

        mEditTxtConfigName = (EditText) addConfigDialog.findViewById(R.id.edTxt_config_name);
        mConfigIconImg = (ImageView) addConfigDialog.findViewById(R.id.imgV_config_icon);
        mBtnSave = (Button) addConfigDialog.findViewById(R.id.btn_save);
        mBtnChooseIcon = (Button) addConfigDialog.findViewById(R.id.btn_icon_choose);
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSaveClick(mEditTxtConfigName, mConfigIconImg);
                closeDialog(addConfigDialog);
            }
        });

        mBtnChooseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleChooseIconClick();
            }
        });
        addConfigDialog.show();
    }

    private void closeDialog(Dialog addConfigDialog) {
        if(addConfigDialog != null && addConfigDialog.isShowing()){
            addConfigDialog.dismiss();
        }
    }

    private void handleSaveClick(EditText mEditTxtConfigName, ImageView configIcon) {
        if(mEditTxtConfigName != null){
            String configName = mEditTxtConfigName.getText().toString();
            if(!TextUtils.isEmpty(configName)){
                String iconPath = getIconPath(configIcon);
                writeToSharedPref(configName, iconPath);
            }
        }
    }

    private void writeToSharedPref(String configName, String iconPath) {
        JSONObject json = null;
        // get current list & append
        try {
            json = new JSONObject();
            json.put(JSON_KEY_CONFIG_NAME, configName);
            json.put(JSON_KEY_CONFIG_ICON_PATH, iconPath == null ? "" : iconPath);
        } catch (JSONException e) {
            Log.e(TAG, "writeToSharedPref: unable to write");
            e.printStackTrace();
        }

        if(json != null){

            // append to current if exist
            JSONArray list = mSharedPrefUtils.getJSONArray(SharedPrefUtil.KEY_CONFIG_LIST);
            if(list == null){
                list = new JSONArray();
            }
            list.put(json);
            mSharedPrefUtils.writeJSONArray(list);
        }
    }

    private String getIconPath(ImageView configIcon) {
        return null;
    }

    private void handleChooseIconClick() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        populateList();
    }
}
