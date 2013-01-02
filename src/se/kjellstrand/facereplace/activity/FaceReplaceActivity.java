
package se.kjellstrand.facereplace.activity;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.FaceDetector.Face;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import se.kjellstrand.facereplace.R;
import se.kjellstrand.facereplace.view.FaceView;
import se.kjellstrand.facereplace.util.FaceHelper;
import se.kjellstrand.facereplace.util.FileHandler;

public class FaceReplaceActivity extends Activity {
    private static final int CAPTURE_PIC = 111101010;

    private static final String FILE_URI_KEY = "FILE_URI_KEY";

    private String TAG = FaceReplaceActivity.class.getSimpleName();

    private Uri mFileUri = null;

    private FaceView faceView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        setButtonOnClickListeners();

        if (savedInstanceState != null && savedInstanceState.containsKey(FILE_URI_KEY)) {
            mFileUri = Uri.parse(savedInstanceState.getString(FILE_URI_KEY));
        }

        faceView = (FaceView) findViewById(R.id.resultImageView);

        mFileUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                "/download/img.jpg"));
        Log.d("TAG", "PATH: " + mFileUri.getPath());
        if (mFileUri == null) {
            pickImage();
        } else {
            loadImageAndFindFaces(mFileUri);

            setRandomFaceOrder();

            faceView.invalidate();
        }
    }

    private void setButtonOnClickListeners() {
        findViewById(R.id.takeAPicture).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
        
        findViewById(R.id.randomizeFaces).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setRandomFaceOrder();
                faceView.invalidate();
            }
        });
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

            setRandomFaceOrder();

            faceView.invalidate();
        }
    }

    private void loadImageAndFindFaces(Uri imageUri) {
        Bitmap srcBitmap = FileHandler.getImageFromSDCard(imageUri.getPath(), 600, 600);
        ArrayList<Face> srcFaces = FaceHelper.findFaces(srcBitmap);
        ArrayList<Bitmap> srcBitmaps = FaceHelper.getBitmapsForFaces(srcFaces, srcBitmap);
        faceView.setSrcBitmaps(srcBitmaps);

        Bitmap dstBitmap = FileHandler.getImageFromSDCard(imageUri.getPath(), 600, 600);
        faceView.setBitmap(dstBitmap);
    }

    private void setRandomFaceOrder() {
        int[] array = new int[faceView.getNumberOfFaces()];
        for (int i = 0; i < array.length; i++) {
            array[i] = (int) (Math.random() * array.length);
            Log.d(TAG, "a: " + array[i]);
        }
        faceView.setSrcToDstFaceIndexArray(array);
    }

    private Uri getTempFileUri() {
        return Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                "FaceReplace" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
    }

}
