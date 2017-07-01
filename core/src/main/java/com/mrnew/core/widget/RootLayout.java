package com.mrnew.core.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.WindowInsets;
import android.widget.RelativeLayout;

/**
 * Created by mrnew on 2017/5/10.
 * email:474923660@qq.com
 * 为了解决输入框弹出不适配的bug，http://www.jianshu.com/p/773f6e6ab972
 */
public class RootLayout extends RelativeLayout {

    private int[] mInsets = new int[4];

    public RootLayout(Context context) {
        super(context);
    }

    public RootLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RootLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RootLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected final boolean fitSystemWindows(Rect insets) {
        // Intentionally do not modify the bottom inset. For some reason,
        // if the bottom inset is modified, window resizing stops working.

        mInsets[0] = insets.left;
        mInsets[1] = insets.top;
        mInsets[2] = insets.right;

        insets.left = 0;
        insets.top = 0;
        insets.right = 0;

        return super.fitSystemWindows(insets);
    }

    @Override
    public final WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            mInsets[0] = insets.getSystemWindowInsetLeft();
            Log.e("mInsets[0]", "" + mInsets[0]);
            mInsets[1] = insets.getSystemWindowInsetTop();
            Log.e("mInsets[1]", "" + mInsets[1]);
            mInsets[2] = insets.getSystemWindowInsetRight();
            Log.e("mInsets[2]", "" + mInsets[2]);
            return super.onApplyWindowInsets(insets.replaceSystemWindowInsets(0, 0, 0,
                    insets.getSystemWindowInsetBottom()));
        } else {
            return insets;
        }
    }
}
