
package se.kjellstrand.facereplace.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class FaceView extends View {
    private final String TAG = FaceView.class.getSimpleName();

    private static final int NUM_FACES = 10;
    private static final float GOLDEN_RATIO = 1.61803399f;

    private final FaceDetector.Face mFaces[] = new FaceDetector.Face[NUM_FACES];

    private Bitmap mBitmap;

    private FaceDetector mFacesDetector;

    private PointF mMidPoint = new PointF();
    private Paint mPaint = new Paint(); // Paint.ANTI_ALIAS_FLAG

    private int[] mFacesIndex = new int[NUM_FACES];;

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
        mFacesIndex[0]=1;
        mFacesIndex[1]=2;
        mFacesIndex[2]=3;
        mFacesIndex[3]=0;
        drawFacesOnCanvas(canvas);
    }

    private void drawFacesOnCanvas(Canvas canvas) {
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, null, new Rect(0, 0,
                    mBitmap.getWidth(), mBitmap.getHeight()), mPaint);
            int i=0;
            for (Face face : mFaces) {
                if (face != null) {
                    Rect src = getFaceRect(mFaces[mFacesIndex [i++]]);
                    Rect dst = getFaceRect(face);
                    
                    Log.d(TAG, "src rect: " + src);
                    Log.d(TAG, "dst rect: " + dst);
                    canvas.drawBitmap(mBitmap, src, dst, mPaint);
                }
            }
        }
    }

    private Rect getFaceRect(Face face) {
        // GOLDEN_RATIO
        // float xRatio = ((float) getWidth()) / mBitmap.getWidth();
        // float yRatio = ((float) getHeight()) / mBitmap.getHeight();
        face.getMidPoint(mMidPoint);
        int x = (int) (mMidPoint.x - (face.eyesDistance()));
        int y = (int) (mMidPoint.y - (face.eyesDistance()));
        int width = x + Math.abs((int) (face.eyesDistance() * 2));
        int height = y + Math.abs((int) (face.eyesDistance() * 2 * GOLDEN_RATIO));
        return new Rect(x, y, width, height);
    }

    public void findFaces() {
        long startTime = System.currentTimeMillis();
        if (mBitmap != null) {
            mFacesDetector = new FaceDetector(mBitmap.getWidth(),
                    mBitmap.getHeight(), NUM_FACES);
            mFacesDetector.findFaces(mBitmap, mFaces);
        }
        Log.d(TAG, "Finding faces took " + (System.currentTimeMillis() - startTime) + " ms.");
    }
}
