package cn.nullobject.criminalintent.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import androidx.annotation.NonNull;

/**
 * @author xiongda
 * Created on 2019/8/19.
 * Introduction:
 */
public class PictureUtils {

    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        // Read in the dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcW = options.outWidth;
        float srcH = options.outHeight;

        // Figure out how much to scale down by
        int inSampleSize = 1;
        if (srcH > destHeight || srcW > destWidth) {
            float hScale = srcH / destHeight;
            float wScale = srcW / destWidth;
            inSampleSize = Math.round(Math.max(hScale, wScale));
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        // Read in and create final bitmap
        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap getDefaultScaledBitmap(@NonNull final String path, @NonNull final Activity activity) {
        Point size = new Point();
        activity.getWindowManager()
                .getDefaultDisplay()
                .getSize(size);
        return getScaledBitmap(path, size.x, size.y);
    }
}
