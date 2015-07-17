package com.saharw.nfcconfig.lists.adapters;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.saharw.nfcconfig.R;
import com.saharw.nfcconfig.Utils.DisplayHelper;
import com.saharw.nfcconfig.asyncTasks.DecodeBitmapAsync;
import com.saharw.nfcconfig.lists.ListItemConfig;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by sahar on 7/12/15.
 */
public class ConfigAdapter extends ArrayAdapter<ListItemConfig> {
    private static final int DP_ITEM_HEIGHT = 80;
    private static final String TAG = "ConfigAdapter";
    private final Activity mContext;
    private final ArrayList<ListItemConfig> mItems;

    public ConfigAdapter(Activity context, ArrayList<ListItemConfig> items) {
        super(context, 0, items);
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if(rowView == null){
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_config, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView)rowView.findViewById(R.id.tvName);
            viewHolder.image = (ImageView)rowView.findViewById(R.id.imgVName);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder)rowView.getTag();
        ListItemConfig item = mItems.get(position);
        String s = item.mConfigName;
        holder.text.setText(s);
        Bitmap bitmap = null;
        try {
            bitmap = new DecodeBitmapAsync().execute(item.mConfigIconPath).get();
        } catch (InterruptedException e) {
            Log.e(TAG, "InterruptedException",e);
        } catch (ExecutionException e) {
            Log.e(TAG, "ExecutionException", e);
        }
        if(bitmap != null) {
            holder.image.setImageBitmap(bitmap);
        }
        rowView.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) DisplayHelper.getPxFromDP(mContext, DP_ITEM_HEIGHT)));
        return rowView;
    }

    private static class ViewHolder{
        public TextView text;
        public ImageView image;
    }
}
