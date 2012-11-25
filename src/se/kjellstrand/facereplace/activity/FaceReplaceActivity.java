
package se.kjellstrand.facereplace.activity;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Trace;
import com.googlecode.androidannotations.annotations.ViewById;

import se.kjellstrand.facereplace.R;
import se.kjellstrand.facereplace.view.FaceView;

@EActivity(R.layout.main)
public class FaceReplaceActivity extends Activity {
    private static final int CAPTURE_PIC = 111101010;

    private Bitmap bitmap;

    private Uri mFileUri = null;

    @ViewById(R.id.resultImageView)
    FaceView faceView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pickImage();

        if (bitmap != null) {
            faceView.setBitmap(bitmap);
            faceView.findFaces();
        }
    }

    public void pickImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mFileUri = getTempFileName();
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

            bitmap = BitmapFactory.decodeFile(imageUri.getPath());

            Log.d("", "bitmap: " + bitmap);

            faceView.setBitmap(bitmap);

            faceView.findFaces();
        }
    }

    private Uri getTempFileName()
    {
        return Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
    }

}
