package se.kjellstrand.facereplace.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Trace;

import se.kjellstrand.facereplace.R;

@EActivity(R.layout.main)
public class Main extends Activity {
    private static final String TAG = Main.class.getSimpleName();
    private static final int REQUEST_CODE = 1;
    
    private Bitmap           bitmap;


    /** Called when the activity is first created. */
    @Trace
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Trace
    @Click
    void takeAPicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_CODE );
    }
    
    @Trace
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
//            faceView.setBitmap(bitmap);
//            faceView.findFaces();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}