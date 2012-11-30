
package se.kjellstrand.facereplace.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Trace;
import com.googlecode.androidannotations.annotations.ViewById;

import se.kjellstrand.facereplace.R;
import se.kjellstrand.facereplace.view.FaceView;

@EActivity(R.layout.main)
public class FaceReplaceActivity extends Activity {
    private static final int CAPTURE_PIC = 111101010;

    private static final String FILE_URI_KEY = "FILE_URI_KEY";

    private Uri mFileUri = null;

    @ViewById(R.id.resultImageView)
    FaceView faceView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(FILE_URI_KEY)) {
            mFileUri = Uri.parse(savedInstanceState.getString(FILE_URI_KEY));
        }
    }
    
    @AfterViews
    void init(){
        mFileUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                "/download/img.jpg"));
        Log.d("TAG", "PATH: "+mFileUri.getPath());
        if (mFileUri == null) {
            pickImage();
        } else {
            loadImageAndFindFaces(mFileUri);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(FILE_URI_KEY, mFileUri.getPath());
    }

    public void pickImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (mFileUri == null) {
            mFileUri = getTempFileUri();
        }
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
        startActivityForResult(takePictureIntent, CAPTURE_PIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_PIC && resultCode == Activity.RESULT_OK) {

            Uri imageUri = null;
            if (data != null) {
                imageUri = data.getData();
            }
            if (imageUri == null) {
                imageUri = mFileUri;
            }

            loadImageAndFindFaces(imageUri);
        }
    }

    private void loadImageAndFindFaces(Uri imageUri) {
        Bitmap bitmap = getImageFromSDCard(imageUri.getPath());

        faceView.setBitmap(bitmap);

        faceView.findFaces();

        faceView.invalidate();
    }

    private Bitmap getImageFromSDCard(String path) {
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
            //bitmapFatoryOptions.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null,
                    bitmapFatoryOptions);
//            bitmapFatoryOptions = new BitmapFactory.Options();
//            Bitmap correctBmp = Bitmap.createBitmap(bmp, 0, 0, bitmapFatoryOptions.outWidth, bitmapFatoryOptions.outHeight,
//                    mat, true);

            return bitmap;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (OutOfMemoryError oom) {
            oom.printStackTrace();
        }
        return null;
    }

    private Uri getTempFileUri()
    {
        return Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                "FaceReplace" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
    }

}
