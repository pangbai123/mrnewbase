package com.mrnew.core.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.mrnew.R;

/**
 * 图像合成控件，
 * 通过在xml中设置crop_shape，得到shape，并且裁剪前景
 */
public class CorpShapeImageView extends ImageView {
    private Paint mPaint;
    private Bitmap mMaskBitmap;

    public CorpShapeImageView(Context context) {
        super(context);
    }

    public CorpShapeImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CorpShapeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView, defStyle, 0);
        Drawable mDrawable = a.getDrawable(R.styleable.RoundedImageView_crop_shape);
        mMaskBitmap = drawableToBitmap(mDrawable);
        mPaint = new Paint();
        a.recycle();
    }


    /**
     * 绘制view的内容
     */
    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (null != drawable) {
            Bitmap bmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            //创建画布
            Canvas drawCanvas = new Canvas(bmp);
            drawable.draw(drawCanvas);

            if (mMaskBitmap != null) {
                mPaint.reset();
                mPaint.setFilterBitmap(false);
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

                drawCanvas.drawBitmap(mMaskBitmap, 0, 0, mPaint);
                mPaint.setXfermode(null);

                canvas.drawBitmap(bmp, 0, 0, null);
            }
        }
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) return null;
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap;
        int width = Math.max(drawable.getIntrinsicWidth(), 1);
        int height = Math.max(drawable.getIntrinsicHeight(), 1);
        try {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
            bitmap = null;
        }

        return bitmap;
    }

}
