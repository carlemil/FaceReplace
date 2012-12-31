
package se.kjellstrand.facereplace.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import se.kjellstrand.facereplace.R;
import se.kjellstrand.facereplace.util.FaceHelper;
import se.kjellstrand.facereplace.view.FaceView;

public class CaptureImageActivity extends Activity {
    
    private static final String RESPONSE_DATA = "data";
    private static final int REQUEST_CODE = 1;

    private final String TAG = CaptureImageActivity.class.getSimpleName();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.capture);

        // start camera and wait for result
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get(RESPONSE_DATA);
            FaceView faceView = (FaceView) findViewById(R.id.resultImageView);
            faceView.setBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
