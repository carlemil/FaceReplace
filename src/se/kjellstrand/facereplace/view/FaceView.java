
package se.kjellstrand.facereplace.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class FaceView extends View {
    private final String TAG = FaceView.class.getSimpleName();

    private static final int NUM_FACES = 10;
    private static final float GOLDEN_RATIO = 1.61803399f;
    private Bitmap mBitmap;

    private FaceDetector facesDetector;
    private final FaceDetector.Face faces[] = new FaceDetector.Face[NUM_FACES];

    private final RectF oval = new RectF();
    private PointF midPoint = new PointF();
    private Paint paint = new Paint(); // Paint.ANTI_ALIAS_FLAG

    public FaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setBitmap(Bitmap bitmap) {
        // Create a bitmap of the same size
        mBitmap = bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        
        if (mBitmap != null) {

            float xRatio = ((float) getWidth()) / mBitmap.getWidth();
            float yRatio = ((float) getHeight()) / mBitmap.getHeight();
            Log.d(TAG, "bm w and h: " + mBitmap.getWidth() + "  " + mBitmap.getHeight());

            paint.setColor(Color.RED);

            for (Face face : faces) {
                if (face != null) {

                    Log.d(TAG, "draw circle");

                    face.getMidPoint(midPoint);
                    // float left = point.x * xRatio - face.eyesDistance() *
                    // xRatio;
                    // float right = point.x * xRatio + face.eyesDistance() *
                    // xRatio;
                    // float top = point.y * yRatio - face.eyesDistance() *
                    // GOLDEN_RATIO * yRatio;
                    // float bottom = point.y * yRatio + face.eyesDistance() *
                    // GOLDEN_RATIO * yRatio;

                    Log.d(TAG, "(face.eyesDistance() / 2) * xRatio: " +
                            (face.eyesDistance() / 2) * xRatio);

                    try {

                        int x = (int) (midPoint.x - (face.eyesDistance() / 2));
                        int y = (int) (midPoint.y - (face.eyesDistance() / 2));
                        int width = (int) (face.eyesDistance());
                        int height = (int) (face.eyesDistance());

                        int[] pixels = new int[mBitmap.getWidth()*mBitmap.getHeight()];

                        Log.d(TAG, mBitmap.getWidth() + "  " +
                                x + "  " + y + "  " +
                                width + "  " +
                                height);

                        mBitmap.getPixels(pixels, 0, mBitmap.getWidth(),
                                x, y, width, height);

                        mBitmap.setPixels(pixels, 0, mBitmap.getWidth(),
                                0, 0, width, height);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            canvas.drawBitmap(mBitmap, null, new Rect(0, 0,
                    mBitmap.getWidth(), mBitmap.getHeight()), paint);

        }
    }

    public void findFaces() {
        long startTime = System.currentTimeMillis();
        if (mBitmap != null) {
            facesDetector = new FaceDetector(
                    mBitmap.getWidth(), mBitmap.getHeight(),
                    NUM_FACES);
            facesDetector.findFaces(mBitmap, faces);
        }
        Log.d(TAG, "Finding faces took " + (System.currentTimeMillis() - startTime) + " ms.");
    }
}
