package se.kjellstrand.facereplace.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.util.AttributeSet;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import se.kjellstrand.facereplace.domain.Face;
import se.kjellstrand.facereplace.view.FaceView;

public class FaceFinder {
    private final String            TAG            = FaceView.class.getSimpleName();

    private static final int        NUM_FACES      = 10;

    private FaceDetector            facesDetector;
    private final FaceDetector.Face faces[]        = new FaceDetector.Face[NUM_FACES];
    private FaceDetector.Face       face           = null;

    private final PointF            eyesMidPts[]   = new PointF[NUM_FACES];
    private final float             eyesDistance[] = new float[NUM_FACES];



    public ArrayList<Face> findFaces(Bitmap bitmap) {

        ArrayList<Face> faceList = new ArrayList<Face>();

        long startTime = System.currentTimeMillis();
        int picWidth = bitmap.getWidth();
        int picHeight = bitmap.getHeight();

        facesDetector = new FaceDetector(picWidth, picHeight, NUM_FACES);
        facesDetector.findFaces(bitmap, faces);

        for(int i = 0; i < faces.length; i++) {
            face = faces[i];
            if (face != null) {
                PointF eyesMP = new PointF();
                face.getMidPoint(eyesMP);
                eyesDistance[i] = face.eyesDistance();
                eyesMidPts[i] = eyesMP;

                Log.d("Face",
                        i + " " + face.confidence() + " " + face.eyesDistance() + " " + "Pose: ("
                                + face.pose(FaceDetector.Face.EULER_X) + "," + face.pose(FaceDetector.Face.EULER_Y) + ","
                                + face.pose(FaceDetector.Face.EULER_Z) + ")" + "Eyes Midpoint: (" + eyesMidPts[i].x + ","
                                + eyesMidPts[i].y + ")");
                Face f = new Face();
                f.setConfidence(face.confidence());
                f.setEyesDistance(face.eyesDistance());
                f.setPoseEulerX(face.pose(FaceDetector.Face.EULER_X));
                f.setPoseEulerY(face.pose(FaceDetector.Face.EULER_Y));
                f.setPoseEulerZ(face.pose(FaceDetector.Face.EULER_Z));
                f.setEyesMidPointX(eyesMidPts[i].x);
                f.setEyesMidPointY(eyesMidPts[i].y);
                // must cut out and downsize the image here.
                f.setImage(convertBitmapToByteArray(bitmap));
                f.setTag("some identifyer?");
                faceList.add(f);

            }
        }
        Log.d(TAG, "Finding faces took " + (System.currentTimeMillis() - startTime) + " ms.");
        return faceList;
    }
    
    private Bitmap cropToFace(Bitmap bitmap, Face face){
        
        do stuff here !!!
        
        Bitmap resBitmap = bitmap; 
        
        return resBitmap;
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
        bitmap.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos); 

        return bos.toByteArray();
    }
}
