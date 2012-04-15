package se.kjellstrand.facereplace.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

import se.kjellstrand.facereplace.R;
import se.kjellstrand.facereplace.R.id;
import se.kjellstrand.facereplace.R.layout;
import se.kjellstrand.facereplace.view.FaceView;

@EActivity(R.layout.capture)
public class CaptureImage extends Activity {
    private static final int REQUEST_CODE = 1;

    private final String     TAG          = CaptureImage.class.getSimpleName();
    
    private Bitmap           bitmap;

    @ViewById(R.id.resultImageView)
    FaceView         faceView;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //faceView = (FaceView) findViewById(R.id.resultImageView);
        pickImage();

        if (bitmap != null) {
            faceView.setBitmap(bitmap);
            faceView.findFaces();
        }
    }

    public void pickImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            faceView.setBitmap(bitmap);
            faceView.findFaces();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}