
package se.kjellstrand.facereplace.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.FaceDetector.Face;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import se.kjellstrand.facereplace.R;
import se.kjellstrand.facereplace.view.FaceView;
import se.kjellstrand.facereplace.util.FaceHelper;
import se.kjellstrand.facereplace.util.FileHandler;

public class FaceReplaceActivity extends Activity {
    private static final String APP_STORAGE_PATH = "/facereplace/";
    private static final String TMP_PHOTO_PATH = APP_STORAGE_PATH + "tmp.jpg";
    private static final String SHARE_PHOTO_PATH = APP_STORAGE_PATH + "share.jpg";
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

        File appStoragePath = new File(Environment.getExternalStorageDirectory() + APP_STORAGE_PATH);
        if (!appStoragePath.exists()) {
            boolean createDir = appStoragePath.mkdirs();
            Log.d(TAG, "creating dir " + appStoragePath.getPath() + " : " + createDir);
        }

        setButtonOnClickListeners();

        if (savedInstanceState != null && savedInstanceState.containsKey(FILE_URI_KEY)) {
            mFileUri = Uri.parse(savedInstanceState.getString(FILE_URI_KEY));
        }

        mFaceView = (FaceView) findViewById(R.id.resultImageView);
        File file = new File(getTempFileUri().getPath());
        Log.d(TAG, "tmp file path: " + file.getPath());

        Log.d(TAG, "file.exists(): " + file.exists());
        if (!file.exists()) {
            // TODO dialog med "take a picture with some faces in it."
            boolean createdNewFile = false;
            try {
                createdNewFile = file.createNewFile();
                Log.d(TAG, "Created new file: " + createdNewFile);
                pickImage();
            } catch (IOException e) {
                // TODO handle failiure to create file, with toast and exit.
                e.printStackTrace();
            }
        } else {

            mFileUri = Uri.fromFile(file);
            Uri srcImageUri = Uri.fromFile(file);
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
                Log.d(TAG, "ah: "+mFaceView.getHeight() + " w: "+mFaceView.getWidth());

                mFaceView.invalidate();
            }
        });

        findViewById(R.id.share).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                        SHARE_PHOTO_PATH));
                FileHandler.writeImageToSDCard(uri, mFaceView.getDrawingCache());

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");

                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.putExtra(Intent.EXTRA_TITLE, "Test title");
                share.putExtra(Intent.EXTRA_TEXT, "http://www.url.com");
                startActivity(Intent.createChooser(share, "Share Image"));
            }
        });
        // android.R.drawable.ic_menu_share
        // TODO swap all faces to one
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
        Log.d(TAG, "onActivityResult");
        if (requestCode == CAPTURE_PIC && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "picture was captured");
            Uri imageUri = null;
            if (data != null) {
                imageUri = data.getData();
                Log.d(TAG, "got data");
            }
            if (imageUri == null) {
                imageUri = mFileUri;
                Log.d(TAG, "set uri to broken null value");
            }
            Uri srcImageUri = getTempFileUri();
            loadImageAndFindFaces(imageUri, srcImageUri);

            int randomOrderArray[] = getRandomFaceOrder(mSrcFaces);
            mFaceView.setSrcFaces(randomOrderArray, mSrcFaces);

            mFaceView.invalidate();
        }
    }

    private void loadImageAndFindFaces(Uri dstImageUri, Uri srcImageUri) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.heightPixels;
        int height = metrics.widthPixels;
        
        Bitmap srcBitmap = FileHandler.getImageFromSDCard(srcImageUri.getPath(), width, height);
        mSrcFaces = FaceHelper.findFaces(srcBitmap);
        ArrayList<Bitmap> srcBitmaps = FaceHelper.getBitmapsForFaces(mSrcFaces, srcBitmap);
        mFaceView.setSrcBitmaps(srcBitmaps);

        Log.d(TAG, "bh: "+mFaceView.getHeight() + " w: "+mFaceView.getWidth());
        Bitmap dstBitmap = FileHandler.getImageFromSDCard(dstImageUri.getPath(), width, height);
        mFaceView.setDstBitmap(dstBitmap);
        Log.d(TAG, "ah: "+mFaceView.getHeight() + " w: "+mFaceView.getWidth());
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
                TMP_PHOTO_PATH));
    }

}
