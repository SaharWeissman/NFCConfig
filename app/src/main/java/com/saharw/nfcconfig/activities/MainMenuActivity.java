package com.saharw.nfcconfig.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.saharw.nfcconfig.R;
import com.saharw.nfcconfig.Utils.FileExplorer;
import com.saharw.nfcconfig.Utils.ImageUtil;
import com.saharw.nfcconfig.Utils.SharedPrefUtil;
import com.saharw.nfcconfig.asyncTasks.DecodeBitmapAsync;
import com.saharw.nfcconfig.lists.ListItemConfig;
import com.saharw.nfcconfig.lists.adapters.ConfigAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class MainMenuActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener, DialogInterface.OnDismissListener {

    public static final String TAG = "MainMenuActivity";
    private static final String SHARED_PREF_FILENAME = "NfcConfigPref";
    private static final String JSON_KEY_CONFIG_NAME = "config_name";
    private static final String JSON_KEY_CONFIG_ICON_PATH = "config_icon_path";
    private static final int KEY_ICON_PATH = 0;

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
                            itemJson.getString(JSON_KEY_CONFIG_ICON_PATH));
                    listItems.add(itemConfig);
                } catch (JSONException e) {
                    Log.e(TAG, "unable to extract item #" + i);
                }
            }
        }
        return listItems;
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
        return (String) configIcon.getTag();
    }

    private void handleChooseIconClick() {
        Log.d(TAG, "handleChooseIconClick");
        Intent startFileExplorerActivity = new Intent(this, FileExplorer.class);
        startFileExplorerActivity.putExtra(FileExplorer.KEY_REQ_CODE, FileExplorer.ACTION_PICK_ICON);
        startActivityForResult(startFileExplorerActivity, FileExplorer.ACTION_PICK_ICON);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        populateList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "request code = " + requestCode + ", result code = " + resultCode);
        String path = data.getStringExtra(FileExplorer.KEY_CHOSEN_FILE_PATH);
        if(!TextUtils.isEmpty(path)){
            Bitmap imgBitmap = null;
            try {
                imgBitmap = new DecodeBitmapAsync().execute(path).get();
            } catch (InterruptedException e) {
                Log.e(TAG, "InterruptedException", e);
            } catch (ExecutionException e) {
                Log.e(TAG, "ExecutionException", e);
            }
            if(imgBitmap != null){
                mConfigIconImg.setImageBitmap(imgBitmap);
                mConfigIconImg.setTag(path);
            }
        }else{
            Log.d(TAG, "chosen file path is empty");
        }
    }
}
