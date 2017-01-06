package com.mrnew.core.widget;


import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import com.mrnew.R;

/**
 * 圆角imageview
 */
@SuppressWarnings("UnusedDeclaration")
public class RoundedImageView extends ImageView {

    public static final String TAG = "RoundedImageView";
    public static final float DEFAULT_RADIUS = 0f;
    public static final float DEFAULT_BORDER_WIDTH = 0f;
    private static final ScaleType[] SCALE_TYPES = {
            ScaleType.MATRIX,
            ScaleType.FIT_XY,
            ScaleType.FIT_START,
            ScaleType.FIT_CENTER,
            ScaleType.FIT_END,
            ScaleType.CENTER,
            ScaleType.CENTER_CROP,
            ScaleType.CENTER_INSIDE
    };

    private float cornerRadius = DEFAULT_RADIUS;
    private float borderWidth = DEFAULT_BORDER_WIDTH;
    private ColorStateList borderColor =
            ColorStateList.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
    private boolean isOval = false;
    private boolean mutateBackground = false;

    private int mResource;
    private Drawable mDrawable;
    private Drawable mBackgroundDrawable;

    private ScaleType mScaleType;

    public RoundedImageView(Context context) {
        super(context);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView, defStyle, 0);

        int index = a.getInt(R.styleable.RoundedImageView_android_scaleType, -1);
        if (index >= 0) {
            setScaleType(SCALE_TYPES[index]);
        } else {
            // default scaletype to FIT_CENTER
            setScaleType(ScaleType.FIT_CENTER);
        }

        cornerRadius = a.getDimensionPixelSize(R.styleable.RoundedImageView_corner_radius, -1);
        borderWidth = a.getDimensionPixelSize(R.styleable.RoundedImageView_border_width, -1);

        // don't allow negative values for radius and border
        if (cornerRadius < 0) {
            cornerRadius = DEFAULT_RADIUS;
        }
        if (borderWidth < 0) {
            borderWidth = DEFAULT_BORDER_WIDTH;
        }

        borderColor = a.getColorStateList(R.styleable.RoundedImageView_border_color);
        if (borderColor == null) {
            borderColor = ColorStateList.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
        }

        mutateBackground = a.getBoolean(R.styleable.RoundedImageView_mutate_background, false);
        isOval = a.getBoolean(R.styleable.RoundedImageView_oval, false);

        updateDrawableAttrs();
        setBackground(mBackgroundDrawable);
        a.recycle();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

    /**
     * Return the current scale type in use by this ImageView.
     *
     *  ref android.R.styleable#ImageView_scaleType
     * @see ScaleType
     */
    @Override
    public ScaleType getScaleType() {
        return mScaleType;
    }

    /**
     * Controls how the image should be resized or moved to match the size
     * of this ImageView.
     *
     * @param scaleType The desired scaling mode.
     */
    @Override
    public void setScaleType(ScaleType scaleType) {
        assert scaleType != null;

        if (mScaleType != scaleType) {
            mScaleType = scaleType;

            switch (scaleType) {
                case CENTER:
                case CENTER_CROP:
                case CENTER_INSIDE:
                case FIT_CENTER:
                case FIT_START:
                case FIT_END:
                case FIT_XY:
                    super.setScaleType(ScaleType.FIT_XY);
                    break;
                default:
                    super.setScaleType(scaleType);
                    break;
            }

            updateDrawableAttrs();
            updateBackgroundDrawableAttrs(false);
            invalidate();
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        mResource = 0;
        mDrawable = RoundedDrawable.fromDrawable(drawable);
        updateDrawableAttrs();
        super.setImageDrawable(mDrawable);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        mResource = 0;
        mDrawable = RoundedDrawable.fromBitmap(bm);
        updateDrawableAttrs();
        super.setImageDrawable(mDrawable);
    }

    @Override
    public void setImageResource(int resId) {
        if (mResource != resId) {
            mResource = resId;
            mDrawable = resolveResource();
            updateDrawableAttrs();
            super.setImageDrawable(mDrawable);
        }
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        setImageDrawable(getDrawable());
    }

    private Drawable resolveResource() {
        Resources rsrc = getResources();
        if (rsrc == null) {
            return null;
        }

        Drawable d = null;

        if (mResource != 0) {
            try {
                d = rsrc.getDrawable(mResource);
            } catch (Exception e) {
                Log.w(TAG, "Unable to find resource: " + mResource, e);
                // Don't try again.
                mResource = 0;
            }
        }
        return RoundedDrawable.fromDrawable(d);
    }

    @Override
    public void setBackground(Drawable background) {
        setBackgroundDrawable(background);
    }

    private void updateDrawableAttrs() {
        updateAttrs(mDrawable, mScaleType);
    }

    private void updateBackgroundDrawableAttrs(boolean convert) {
        if (mutateBackground) {
            if (convert) {
                mBackgroundDrawable = RoundedDrawable.fromDrawable(mBackgroundDrawable);
            }
            updateAttrs(mBackgroundDrawable, ScaleType.CENTER);
        }
    }

    private void updateAttrs(Drawable drawable, ScaleType type) {
        if (drawable == null) {
            return;
        }

        if (drawable instanceof RoundedDrawable) {
            ((RoundedDrawable) drawable)
                    .setScaleType(type)
                    .setCornerRadius(cornerRadius)
                    .setBorderWidth(borderWidth)
                    .setBorderColor(borderColor)
                    .setOval(isOval);
        } else if (drawable instanceof LayerDrawable) {
            // loop through layers to and set drawable attrs
            LayerDrawable ld = ((LayerDrawable) drawable);
            for (int i = 0, layers = ld.getNumberOfLayers(); i < layers; i++) {
                updateAttrs(ld.getDrawable(i), type);
            }
        }
    }

    @Override
    @Deprecated
    public void setBackgroundDrawable(Drawable background) {
        mBackgroundDrawable = background;
        updateBackgroundDrawableAttrs(true);
        super.setBackgroundDrawable(mBackgroundDrawable);
    }

    /**
     * 获取圆角
     *
     * @return
     */
    public float getCornerRadius() {
        return cornerRadius;
    }

    /**
     * 设置圆角
     *
     * @param resId
     */
    public void setCornerRadius(int resId) {
        setCornerRadius(getResources().getDimension(resId));
    }

    /**
     * 设置圆角
     *
     * @param radius
     */
    public void setCornerRadius(float radius) {
        if (cornerRadius == radius) {
            return;
        }

        cornerRadius = radius;
        updateDrawableAttrs();
        updateBackgroundDrawableAttrs(false);
    }

    /**
     * 获取描边大小
     */
    public float getBorderWidth() {
        return borderWidth;
    }

    /**
     * 设置描边大小
     */
    public void setBorderWidth(int resId) {
        setBorderWidth(getResources().getDimension(resId));
    }

    /**
     * 设置描边大小
     */
    public void setBorderWidth(float width) {
        if (borderWidth == width) {
            return;
        }

        borderWidth = width;
        updateDrawableAttrs();
        updateBackgroundDrawableAttrs(false);
        invalidate();
    }

    /**
     * 获取描边颜色
     */
    public int getBorderColor() {
        return borderColor.getDefaultColor();
    }

    /**
     * 设置描边颜色
     *
     * @param color
     */
    public void setBorderColor(int color) {
        setBorderColor(ColorStateList.valueOf(color));
    }

    /**
     * 获取描边颜色
     */
    public ColorStateList getBorderColors() {
        return borderColor;
    }

    /**
     * 设置描边颜色
     *
     * @param colors
     */
    public void setBorderColor(ColorStateList colors) {
        if (borderColor.equals(colors)) {
            return;
        }

        borderColor =
                (colors != null) ? colors : ColorStateList.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
        updateDrawableAttrs();
        updateBackgroundDrawableAttrs(false);
        if (borderWidth > 0) {
            invalidate();
        }
    }

    /**
     * 获取是否为椭圆
     *
     * @return
     */
    public boolean isOval() {
        return isOval;
    }

    /**
     * 设置是否为椭圆
     *
     * @param oval
     */
    public void setOval(boolean oval) {
        isOval = oval;
        updateDrawableAttrs();
        updateBackgroundDrawableAttrs(false);
        invalidate();
    }

    /**
     * 获取是否连背景一起裁剪
     *
     * @return
     */
    public boolean isMutateBackground() {
        return mutateBackground;
    }

    /**
     * 设置是否连背景一起裁剪
     *
     */
    public void setMutateBackground(boolean mutate) {
        if (mutateBackground == mutate) {
            return;
        }

        mutateBackground = mutate;
        updateBackgroundDrawableAttrs(true);
        invalidate();
    }
}
