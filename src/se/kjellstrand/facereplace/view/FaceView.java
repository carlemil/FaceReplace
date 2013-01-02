
package se.kjellstrand.facereplace.view;

import java.util.ArrayList;

import se.kjellstrand.facereplace.util.FaceHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.FaceDetector.Face;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class FaceView extends ImageView {

    private final String TAG = FaceView.class.getSimpleName();

    private ArrayList<Face> mDstFaces = new ArrayList<Face>();

    private Bitmap mDstBitmap;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    private int[] mSrcToDstFaceIndexArray = new int[FaceHelper.NUM_FACES];

    private ArrayList<Bitmap> mSrcBitmaps;

    public FaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawFacesOnCanvas(canvas);
    }
    
    private void drawFacesOnCanvas(Canvas canvas) {
        if (mDstBitmap != null) {
            canvas.drawBitmap(mDstBitmap, null, new Rect(0, 0,
                    mDstBitmap.getWidth(), mDstBitmap.getHeight()), mPaint);
            int sc = canvas.saveLayer(null, mPaint, Canvas.MATRIX_SAVE_FLAG |
                    Canvas.CLIP_SAVE_FLAG |
                    Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                    Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                    Canvas.CLIP_TO_LAYER_SAVE_FLAG);
            int i = 0;
            for (Face face : mDstFaces) {
                if (face != null) {
                    Rect dst = FaceHelper.getFaceRect(face);

                    mPaint.setMaskFilter(new BlurMaskFilter(6.0f, BlurMaskFilter.Blur.NORMAL));

                    mPaint.setXfermode(null);
                    canvas.drawOval(new RectF(dst), mPaint);

                    mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

                    canvas.drawBitmap(mSrcBitmaps.get(mSrcToDstFaceIndexArray[i++]), null, dst,
                            mPaint);
                }
            }
            mPaint.setXfermode(null);
            canvas.restoreToCount(sc);
        }
    }

    public void setBitmap(Bitmap bitmap) {
        // Create a bitmap of the same size
        mDstBitmap = bitmap;

        long startTime = System.currentTimeMillis();
        boolean isMutable = true;
        mDstBitmap = mDstBitmap.copy(Bitmap.Config.ARGB_8888, isMutable);
        Log.d(TAG, "Converting bitmap to 8888 format took: "
                + (System.currentTimeMillis() - startTime) + " ms.");

        mDstFaces = FaceHelper.findFaces(bitmap);
    }

    public int getNumberOfFaces() {
        return mDstFaces == null ? 0 : mDstFaces.size();
    }

    public void setSrcToDstFaceIndexArray(int[] array) {
        this.mSrcToDstFaceIndexArray = array;
    }
    
    public int[] getSrcToDstFaceIndexArray() {
        return this.mSrcToDstFaceIndexArray;
    }

    public void setSrcBitmaps(ArrayList<Bitmap> mSrcBitmaps) {
        this.mSrcBitmaps = mSrcBitmaps;
    }
    public ArrayList<Bitmap> getSrcBitmaps() {
        return mSrcBitmaps;
    }

}
