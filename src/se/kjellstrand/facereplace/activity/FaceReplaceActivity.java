
package se.kjellstrand.facereplace.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.FaceDetector.Face;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import se.kjellstrand.facereplace.R;
import se.kjellstrand.facereplace.view.FaceView;
import se.kjellstrand.facereplace.util.FaceHelper;
import se.kjellstrand.facereplace.util.FileHandler;

public class FaceReplaceActivity extends Activity {
    private static final int CAPTURE_PIC = 111101010;

    private static final String FILE_URI_KEY = "FILE_URI_KEY";


    
    private Uri mFileUri = null;

    private FaceView faceView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        if (savedInstanceState != null && savedInstanceState.containsKey(FILE_URI_KEY)) {
            mFileUri = Uri.parse(savedInstanceState.getString(FILE_URI_KEY));
        }
    
        faceView = (FaceView) findViewById(R.id.resultImageView);
        
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
        Bitmap dstBitmap = FileHandler.getImageFromSDCard(imageUri.getPath());

        faceView.setBitmap(dstBitmap);

        ArrayList<Face> dstFaces = FaceHelper.findFaces(dstBitmap);

// skapa src bitmaps och sätt dom istället för dst nedan
        
        ArrayList<Bitmap> srcBitmaps = new ArrayList<Bitmap>();

        FaceHelper.addFacesToSrcFaceBitmapsList(dstFaces, dstBitmap, srcBitmaps);
        
        faceView.setSrcBitmaps(srcBitmaps);
        
        int[] array = new int[4];
        array[0] = 1;
        array[1] = 2;
        array[2] = 3;
        array[3] = 0;

        faceView.setSrcToDstFaceIndexArray(array );
        faceView.invalidate();
    }

    private Uri getTempFileUri()
    {
        return Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                "FaceReplace" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
    }

}
