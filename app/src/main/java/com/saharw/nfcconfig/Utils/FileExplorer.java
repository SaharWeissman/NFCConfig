package com.saharw.nfcconfig.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.saharw.nfcconfig.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by sahar on 7/12/15.
 */
public class FileExplorer extends Activity {

    private static final String TAG = "FileExplorer";
    public static final String ACTION = "action";
    public static final String KEY_CHOSEN_FILE_PATH = "chosen_file_path";

    public static final int ACTION_PICK_ICON = 1;
    public static final String KEY_REQ_CODE = "request_code";
    ArrayList<String> mStrings= new ArrayList<String>();
    private boolean mFirstLvl = true; // Check if the first
    private Item[] mFileList;// level of the directory structure is the one showing
    private File mPath;
    private String mChosenFile;
    private static final int DIALOG_LOAD_FILE = 1000;

    ListAdapter mAdapter;
    private int mReqCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPath = getExternalStorageDir();
        loadFileList();
        showDialog(DIALOG_LOAD_FILE);
        Intent startIntent = getIntent();
        if(startIntent != null){
            mReqCode = startIntent.getIntExtra(KEY_REQ_CODE, -1);
        }
        Log.d(TAG, mPath.getAbsolutePath());
    }

    private void loadFileList() {
        try {
            mPath.mkdirs();
        } catch (SecurityException e) {
            Log.e(TAG, "unable to write on the sd card ");
        }

        // Checks whether mPath exists
        if (mPath.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    // Filters based on whether the file is hidden or not
                    return (sel.isFile() || sel.isDirectory())
                            && !sel.isHidden();

                }
            };

            String[] fList = mPath.list(filter);
            mFileList = new Item[fList.length];
            for (int i = 0; i < fList.length; i++) {
                mFileList[i] = new Item(fList[i], R.drawable.file_icon);

                // Convert into file mPath
                File sel = new File(mPath, fList[i]);

                // Set drawables
                if (sel.isDirectory()) {
                    mFileList[i].icon = R.drawable.directory_icon;
                    Log.d("DIRECTORY", mFileList[i].file);
                } else {
                    Log.d("FILE", mFileList[i].file);
                }
            }

            if (!mFirstLvl) {
                Item temp[] = new Item[mFileList.length + 1];
                for (int i = 0; i < mFileList.length; i++) {
                    temp[i + 1] = mFileList[i];
                }
                temp[0] = new Item("Up", R.drawable.directory_up);
                mFileList = temp;
            }
        } else {
            Log.e(TAG, "mPath does not exist");
        }

        mAdapter = new ArrayAdapter<Item>(this,
                android.R.layout.select_dialog_item, android.R.id.text1,
                mFileList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // creates view
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view
                        .findViewById(android.R.id.text1);

                // put the image on the text view
                textView.setCompoundDrawablesWithIntrinsicBounds(
                        mFileList[position].icon, 0, 0, 0);

                // add margin between image and text (support various screen
                // densities)
                int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
                textView.setCompoundDrawablePadding(dp5);

                return view;
            }
        };

    }

    private File getExternalStorageDir() {
        String deviceModel = Build.MODEL;
        String path = Environment.getExternalStorageState();
        if(deviceModel.equals("LG-D855")){
            path = "/storage/sdcard0";
        }
        return new File(path);
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (mFileList == null) {
            Log.e(TAG, "No files loaded");
            dialog = builder.create();
            return dialog;
        }

        switch (id) {
            case DIALOG_LOAD_FILE:
                builder.setTitle("Choose your file");
                builder.setAdapter(mAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mChosenFile= mFileList[which].file;
                        File sel = new File(mPath + "/" + mChosenFile);
                        if (sel.isDirectory()) {
                            mFirstLvl = false;

                            // Adds chosen directory to list
                            mStrings.add(mChosenFile);
                            mFileList = null;
                            mPath = new File(sel + "");

                            loadFileList();

                            removeDialog(DIALOG_LOAD_FILE);
                            showDialog(DIALOG_LOAD_FILE);
                            Log.d(TAG, mPath.getAbsolutePath());

                        }

                        // Checks if 'up' was clicked
                        else if (mChosenFile.equalsIgnoreCase("up") && !sel.exists()) {

                            // present directory removed from list
                            String s = mStrings.remove(mStrings.size() - 1);

                            // path modified to exclude present directory
                            mPath = new File(mPath.toString().substring(0,
                                    mPath.toString().lastIndexOf(s)));
                            mFileList = null;

                            // if there are no more directories in the list, then
                            // its the first level
                            if (mStrings.isEmpty()) {
                                mFirstLvl = true;
                            }
                            loadFileList();

                            removeDialog(DIALOG_LOAD_FILE);
                            showDialog(DIALOG_LOAD_FILE);
                            Log.d(TAG, mPath.getAbsolutePath());

                        }
                        // File picked
                        else {
                            handlePickedFile(mChosenFile);
                        }

                    }
                });
                break;
        }
        dialog = builder.show();
        return dialog;
    }

    private void handlePickedFile(String chosenFilePath) {
        Log.d(TAG, "handlePickedFile: chosen file path = " + chosenFilePath);
        switch (mReqCode){
            case ACTION_PICK_ICON:{
                Log.d(TAG, "case ACTION_PICK_ICON");
                Intent data = new Intent();
                data.putExtra(FileExplorer.KEY_CHOSEN_FILE_PATH, mPath +"/" + chosenFilePath);
                setResult(0, data);
                this.finish();
                break;
            }
            default:{
                break;
            }
        }
    }

    private class Item {
        public String file;
        public int icon;

        public Item(String file, Integer icon) {
            this.file = file;
            this.icon = icon;
        }

        @Override
        public String toString() {
            return file;
        }
    }
}
