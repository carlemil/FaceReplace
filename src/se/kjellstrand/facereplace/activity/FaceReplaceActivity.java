
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
    private static final String TAG = FaceReplaceActivity.class.getSimpleName();
    private static final int CAPTURE_PIC = 111101010;
    private static final String FILE_URI_KEY = "FILE_URI_KEY";

    private ArrayList<Face> mSrcFaces = new ArrayList<Face>();

    private Uri mFileUri = null;

    private FaceView mFaceView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        setButtonOnClickListeners();

        if (savedInstanceState != null && savedInstanceState.containsKey(FILE_URI_KEY)) {
            mFileUri = Uri.parse(savedInstanceState.getString(FILE_URI_KEY));
        }

        mFaceView = (FaceView) findViewById(R.id.resultImageView);
        File file = new File(Environment.getExternalStorageDirectory(),
                "/download/img.jpg");
        mFileUri = Uri.fromFile(file);

        if (mFileUri == null) {
            pickImage();
        } else {
            Uri srcImageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                    "/download/img4.jpg"));
            loadImageAndFindFaces(mFileUri, srcImageUri);

            int array[] = getRandomFaceOrder(mSrcFaces);
            mFaceView.setSrcFaces(array, mSrcFaces);

            mFaceView.invalidate();
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
                int randomOrderArray[] = getRandomFaceOrder(mSrcFaces);
                mFaceView.setSrcFaces(randomOrderArray, mSrcFaces);

                mFaceView.invalidate();
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
            Uri srcImageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                    "/download/img4.jpg"));
            loadImageAndFindFaces(imageUri, srcImageUri);

            int randomOrderArray[] = getRandomFaceOrder(mSrcFaces);
            mFaceView.setSrcFaces(randomOrderArray, mSrcFaces);

            mFaceView.invalidate();
        }
    }

    private void loadImageAndFindFaces(Uri dstImageUri, Uri srcImageUri) {
        // TODO measure screen/layout size properly and use better values
        Bitmap srcBitmap = FileHandler.getImageFromSDCard(srcImageUri.getPath(), 600, 600);
        mSrcFaces = FaceHelper.findFaces(srcBitmap);
        ArrayList<Bitmap> srcBitmaps = FaceHelper.getBitmapsForFaces(mSrcFaces, srcBitmap);
        mFaceView.setSrcBitmaps(srcBitmaps);

        Bitmap dstBitmap = FileHandler.getImageFromSDCard(dstImageUri.getPath(), 600, 600);
        mFaceView.setDstBitmap(dstBitmap);
    }

    private int[] getRandomFaceOrder(ArrayList<Face> srcFaces) {
        int numberOfSourceFaces = mFaceView.getSrcBitmaps().size();
        int[] array = new int[mFaceView.getNumberOfFaces()];
        for (int i = 0; i < array.length; i++) {
            array[i] = (int) (Math.random() * numberOfSourceFaces);
        }
        return array;
    }

    private Uri getTempFileUri() {
        return Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                "FaceReplace" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
    }

}
