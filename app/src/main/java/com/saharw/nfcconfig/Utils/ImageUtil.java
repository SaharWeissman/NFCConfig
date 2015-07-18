package com.saharw.nfcconfig.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.widget.ImageView;

import com.saharw.nfcconfig.App;

import java.io.IOException;

/**
 * Created by sahar on 7/17/15.
 */
public class ImageUtil {
    private static final String TAG = "ImageUtil";

    public static int calcInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if(height > reqHeight || width > reqWidth){
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap tryReadImgFromPath(String path) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // don't really load bitmap just get width & height.
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeFile(path, options);

            // image view will be in fixed size
            int imgViewWidth = App.getAppContext().getResources().getDisplayMetrics().widthPixels / 2;
            int imgViewHeight = App.getAppContext().getResources().getDisplayMetrics().heightPixels / 7;

            options.inSampleSize = ImageUtil.calcInSampleSize(options, imgViewWidth, imgViewHeight);
            options.inJustDecodeBounds = false; //actually decode & load bitmap
            bitmap = BitmapFactory.decodeFile(path, options);
        }catch (Exception e){
            Log.e(TAG, "caught exception when trying to decode bitmap", e);
            bitmap = null;
        }
        if(bitmap != null) {
            try {
                ExifInterface exif = new ExifInterface(path);
                int imgOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                if(imgOrientation != 1){
                    Matrix matrix = new Matrix();
                    switch (imgOrientation) {
                        case 2:
                            matrix.setScale(-1, 1);
                            break;
                        case 3:
                            matrix.setRotate(180);
                            break;
                        case 4:
                            matrix.setRotate(180);
                            matrix.postScale(-1, 1);
                            break;
                        case 5:
                            matrix.setRotate(90);
                            matrix.postScale(-1, 1);
                            break;
                        case 6:
                            matrix.setRotate(90);
                            break;
                        case 7:
                            matrix.setRotate(-90);
                            matrix.postScale(-1, 1);
                            break;
                        case 8:
                            matrix.setRotate(-90);
                            break;
                        default:
                            break;
                    }
                    try {
                        Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                                bitmap.getHeight(), matrix, true);
                        bitmap.recycle();
                        return oriented;
                    }catch (OutOfMemoryError e){
                        e.printStackTrace();
                        return bitmap;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }else{
            return null;
        }
    }

    public static Bitmap tryReadImgFromResource(int resID) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // don't really load bitmap just get width & height.
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeResource(App.getAppContext().getResources(), resID);

            // image view will be in fixed size
            int imgViewWidth = App.getAppContext().getResources().getDisplayMetrics().widthPixels / 2;
            int imgViewHeight = App.getAppContext().getResources().getDisplayMetrics().heightPixels / 7;

            options.inSampleSize = ImageUtil.calcInSampleSize(options, imgViewWidth, imgViewHeight);
            options.inJustDecodeBounds = false; //actually decode & load bitmap
            bitmap = BitmapFactory.decodeResource(App.getAppContext().getResources(), resID);
        }catch (Exception e){
            Log.e(TAG, "caught exception when trying to decode bitmap", e);
            bitmap = null;
        }
        if(bitmap != null) {
            return bitmap;
        }else{
            return null;
        }
    }
}
