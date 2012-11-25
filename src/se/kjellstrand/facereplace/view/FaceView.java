
package se.kjellstrand.facereplace.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.FaceDetector;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class FaceView extends View {
    private final String TAG = FaceView.class.getSimpleName();

    private static final int NUM_FACES = 10;
    private static final float GOLDEN_RATIO = 1.61803399f;
    private static Bitmap bitmap;

    private FaceDetector facesDetector;
    private final FaceDetector.Face faces[] = new FaceDetector.Face[NUM_FACES];
    private FaceDetector.Face face = null;

    private final PointF eyesMidPts[] = new PointF[NUM_FACES];
    private final float eyesDistance[] = new float[NUM_FACES];

    public FaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setBitmap(Bitmap bitmap) {
        FaceView.bitmap = bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (bitmap != null) {
            float xRatio = ((float) getWidth()) / bitmap.getWidth();
            float yRatio = ((float) getHeight()) / bitmap.getHeight();
            Log.d(TAG, "bm w and h: " + bitmap.getWidth() + "  " + bitmap.getHeight());
            Paint tmpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            tmpPaint.setColor(0xffffffff);
            canvas.drawBitmap(bitmap, null, new Rect(0, 0, getWidth(), getHeight()), tmpPaint);
            for (int i = 0; i < eyesMidPts.length; i++) {
                if (eyesMidPts[i] != null) {
                    tmpPaint.setStrokeWidth(3);
                    // canvas.drawCircle(eyesMidPts[i].x * xRatio,
                    // eyesMidPts[i].y * yRatio, eyesDistance[i] / 2, tmpPaint);
                    // canvas.drawCircle(eyesMidPts[i].x * xRatio,
                    // eyesMidPts[i].y * yRatio, eyesDistance[i] / 6, tmpPaint);
                    Log.d(TAG, "draw circle");
                    canvas.drawCircle(eyesMidPts[i].x * xRatio, eyesMidPts[i].y * yRatio,
                            eyesDistance[i] / 2, tmpPaint);
                    RectF oval = new RectF();
                    float left = eyesMidPts[i].x * xRatio - eyesDistance[i] * xRatio;
                    float right = eyesMidPts[i].x * xRatio + eyesDistance[i] * xRatio;
                    float top = eyesMidPts[i].y * yRatio - eyesDistance[i] * GOLDEN_RATIO * yRatio;
                    float bottom = eyesMidPts[i].y * yRatio + eyesDistance[i] * GOLDEN_RATIO
                            * yRatio;

                    Log.d(TAG, "(eyesDistance[i] / 2) * xRatio: " + (eyesDistance[i] / 2) * xRatio);

                    oval.set(left, top, right, bottom);
                    canvas.drawOval(oval, tmpPaint);
                    Log.d(TAG, "oval: " + oval);

                }
            }
        }
    }

    public void findFaces() {
        long startTime = System.currentTimeMillis();
        int picWidth = bitmap.getWidth();
        int picHeight = bitmap.getHeight();

        if (bitmap != null) {

            facesDetector = new FaceDetector(picWidth, picHeight, NUM_FACES);
            facesDetector.findFaces(bitmap, faces);

            for (int i = 0; i < faces.length; i++) {
                face = faces[i];
                if (face != null) {
                    PointF eyesMP = new PointF();
                    face.getMidPoint(eyesMP);
                    eyesDistance[i] = face.eyesDistance();
                    eyesMidPts[i] = eyesMP;

                    Log.d("Face",
                            i + " " + face.confidence() + " " + face.eyesDistance() + " "
                                    + "Pose: ("
                                    + face.pose(FaceDetector.Face.EULER_X) + ","
                                    + face.pose(FaceDetector.Face.EULER_Y) + ","
                                    + face.pose(FaceDetector.Face.EULER_Z) + ")"
                                    + "Eyes Midpoint: (" + eyesMidPts[i].x + ","
                                    + eyesMidPts[i].y + ")");

                }
            }
        }
        Log.d(TAG, "Finding faces took " + (System.currentTimeMillis() - startTime) + " ms.");

    }
}
