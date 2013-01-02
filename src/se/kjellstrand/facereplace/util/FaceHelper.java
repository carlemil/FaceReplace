
package se.kjellstrand.facereplace.util;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.util.Log;

public class FaceHelper {

    private static final String TAG = FaceHelper.class.getSimpleName();

    private static final float GOLDEN_RATIO = 1.61803399f;

    private static FaceDetector mFacesDetector;

    public static final int NUM_FACES = 10;

    public static ArrayList<Face> findFaces(Bitmap bitmap) {
        ArrayList<Face> facesList = new ArrayList<Face>();
        long startTime = System.currentTimeMillis();
        if (bitmap != null) {
            Face[] faces = new Face[FaceHelper.NUM_FACES];
            mFacesDetector = new FaceDetector(bitmap.getWidth(),
                    bitmap.getHeight(), FaceHelper.NUM_FACES);
            int numberOfFaces = mFacesDetector.findFaces(bitmap, faces);
            Log.d(TAG, "Finding faces took " +
                    (System.currentTimeMillis() - startTime) + " ms.");
            for (int i = 0; i < numberOfFaces; i++) {
                facesList.add(faces[i]);
            }
        }

        return facesList;
    }

    @SuppressWarnings("static-access")
    public static ArrayList<Bitmap> getBitmapsForFaces(ArrayList<Face> faces, Bitmap bitmap) {
        ArrayList<Bitmap> srcBitmaps = new ArrayList<Bitmap>();
        for (Face face : faces) {
            if (face != null) {
                Rect src = getFaceRect(face);
                try {
                    srcBitmaps.add(bitmap.createBitmap(bitmap, src.left, src.top,
                            src.right - src.left, src.bottom - src.top, null, false));
                } catch (Exception e) {
                    Log.e("Error", "in getBitmapsForFaces: " + e.getMessage());
                }
            }
        }
        return srcBitmaps;
    }

    public static Rect getFaceRect(Face face) {
        // GOLDEN_RATIO
        // float xRatio = ((float) getWidth()) / mBitmap.getWidth();
        // float yRatio = ((float) getHeight()) / mBitmap.getHeight();
        // TODO improve scaling by looking at pose and euler y
        PointF midPoint = new PointF();
        face.getMidPoint(midPoint);
        int x = (int) (midPoint.x - (face.eyesDistance()));
        int y = (int) (midPoint.y - (face.eyesDistance()));
        int width = x + (int) (face.eyesDistance() * 2);
        int height = y + (int) (face.eyesDistance() * 2 * GOLDEN_RATIO);
        return new Rect(x, y, width, height);
    }

}
