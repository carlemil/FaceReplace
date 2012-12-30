
package se.kjellstrand.facereplace.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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

    private ArrayList<Face> mFaces = new ArrayList<Face>();

    private Bitmap mBitmap;
    private Bitmap mOneFaceBitmap;

    private FaceDetector mFacesDetector;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    private int[] mFacesIndex = new int[NUM_FACES];

    private ArrayList<Bitmap> mSrcFaceBitmaps = new ArrayList<Bitmap>();

    private BlurMaskFilter mMediumOuterBlurMaskFilter;

    public FaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setBitmap(Bitmap bitmap) {
        // Create a bitmap of the same size
        mBitmap = bitmap;

        long startTime = System.currentTimeMillis();
        boolean isMutable = true;
        mBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, isMutable);
        Log.d(TAG, "Converting bitmap to 8888 format took: "
                + (System.currentTimeMillis() - startTime) + " ms.");
        
        mFaces = findFaces(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mFacesIndex[0] = 1;
        mFacesIndex[1] = 2;
        mFacesIndex[2] = 3;
        mFacesIndex[3] = 0;
        drawFacesOnCanvas(canvas);
    }

    private void drawFacesOnCanvas(Canvas canvas) {
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, null, new Rect(0, 0,
                    mBitmap.getWidth(), mBitmap.getHeight()), mPaint);
            int sc = canvas.saveLayer(null, mPaint, Canvas.MATRIX_SAVE_FLAG |
                    Canvas.CLIP_SAVE_FLAG |
                    Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                    Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                    Canvas.CLIP_TO_LAYER_SAVE_FLAG);
            int i = 0;
            for (Face face : mFaces) {
                if (face != null) {
                    Rect src = getFaceRect(mFaces.get(mFacesIndex[i]));
                    Rect dst = getFaceRect(face);

                    mOneFaceBitmap = mSrcFaceBitmaps.get(mFacesIndex[i++]);
                    
                    Bitmap ovalBitmap = Bitmap.createBitmap(src.right - src.left, src.bottom - src.top,
                            Bitmap.Config.ARGB_8888);

                    mMediumOuterBlurMaskFilter =
                            new BlurMaskFilter(6.0f, BlurMaskFilter.Blur.NORMAL);

                    mPaint.setMaskFilter(mMediumOuterBlurMaskFilter);

                    mPaint.setXfermode(null);
                    canvas.drawOval(new RectF(dst), mPaint);

                    mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                    canvas.drawBitmap(mOneFaceBitmap, null, dst, mPaint);

                    // canvas.drawBitmap(mBitmap, src, dst, mPaint);

                    Log.d(TAG, "src rect: " + src);
                    Log.d(TAG, "dst rect: " + dst);

                }
            }
            mPaint.setXfermode(null);
            canvas.restoreToCount(sc);
        }
    }

    @SuppressWarnings("static-access")
    public void addFacesToSrcFaceBitmapsList(ArrayList<Face> faces, Bitmap bitmap) {
        for (Face face : faces) {
            if (face != null) {
                Rect src = getFaceRect(face);

                // Experiment with false and see if it looks better
                boolean filter = true;
                Matrix m = new Matrix();
                
                // calcualte the real scale, or should we do nothing since 
                // we anyway dont know the fial size ? better send null as matrix ?
                m.setScale(2f, 2f);

                mSrcFaceBitmaps.add(bitmap.createBitmap(bitmap, src.left, src.top,
                        src.right - src.left, src.bottom - src.top, m, filter));

            }
        }
    }

    private Rect getFaceRect(Face face) {
        // GOLDEN_RATIO
        // float xRatio = ((float) getWidth()) / mBitmap.getWidth();
        // float yRatio = ((float) getHeight()) / mBitmap.getHeight();
        PointF midPoint = new PointF();
        face.getMidPoint(midPoint);
        int x = (int) (midPoint.x - (face.eyesDistance()));
        int y = (int) (midPoint.y - (face.eyesDistance()));
        int width = x + (int) (face.eyesDistance() * 2);
        int height = y + (int) (face.eyesDistance() * 2 * GOLDEN_RATIO);
        return new Rect(x, y, width, height);
    }

    public ArrayList<Face> findFaces(Bitmap bitmap) {
        ArrayList<Face> facesList = new ArrayList<Face>();
        long startTime = System.currentTimeMillis();
        if (bitmap != null) {
            Face[] faces = new Face[NUM_FACES];
            mFacesDetector = new FaceDetector(bitmap.getWidth(),
                    bitmap.getHeight(), NUM_FACES);
            int numberOfFaces = mFacesDetector.findFaces(bitmap, faces);
            Log.d(TAG, "Finding faces took " +
                    (System.currentTimeMillis() - startTime) + " ms.");
            for (int i = 0; i < numberOfFaces; i++) {
                facesList.add(faces[i]);
            }
        }

        return facesList;
    }

    public int[] getmFacesIndex() {
        return mFacesIndex;
    }

    public void setmFacesIndex(int[] mFacesIndex) {
        this.mFacesIndex = mFacesIndex;
    }

}
