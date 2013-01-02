
package se.kjellstrand.facereplace.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import se.kjellstrand.facereplace.view.FaceView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

public class FileHandler {

    private static final String TAG = FileHandler.class.getSimpleName();

    public static Bitmap getImageFromSDCard(String path, int maxWidth, int maxHeight) {
        try {
            File f = new File(path);
            ExifInterface exif = new ExifInterface(f.getPath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            int angle = 0;

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270;
            }

            Matrix mat = new Matrix();
            mat.postRotate(angle);

            BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
            bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
            bitmapFatoryOptions.inSampleSize = 1;

            if (maxHeight > 0 && maxWidth > 0) {
                bitmapFatoryOptions.inJustDecodeBounds = true;

                BitmapFactory.decodeStream(new FileInputStream(f), null,
                        bitmapFatoryOptions);

                bitmapFatoryOptions.inSampleSize = bitmapFatoryOptions.outWidth / maxWidth;
                Log.d(TAG, "sampleSize: " + bitmapFatoryOptions.inSampleSize);

            }
            bitmapFatoryOptions.inJustDecodeBounds = false;
            bitmapFatoryOptions.inMutable = true;

            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null,
                    bitmapFatoryOptions);

            return bitmap;

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (OutOfMemoryError oom) {
            oom.printStackTrace();
        }
        return null;
    }

}
