
package se.kjellstrand.facereplace.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import se.kjellstrand.facereplace.view.FaceView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class FileHandler {

    private static final String TAG = FileHandler.class.getSimpleName();

    public static Bitmap getImageFromSDCard(String path, int maxWidth, int maxHeight) {
        try {
            File f = new File(path);
            ExifInterface exif = new ExifInterface(f.getPath());
//            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_NORMAL);
//
//            int angle = 0;
//
//            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
//                angle = 90;
//            }
//            else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
//                angle = 180;
//            }
//            else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
//                angle = 270;
//            }

            Matrix mat = new Matrix();
//            mat.postRotate(angle);

            BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
            bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
            bitmapFatoryOptions.inSampleSize = 1;

            if (maxHeight > 0 && maxWidth > 0) {
                bitmapFatoryOptions.inJustDecodeBounds = true;

                BitmapFactory.decodeStream(new FileInputStream(f), null,
                        bitmapFatoryOptions);

                // TODO calc max of w/h
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

    public static void writeImageToSDCard(Uri uri, Bitmap image) {
        OutputStream outStream = null;
        File file = new File(uri.getPath());
        try {
            outStream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
