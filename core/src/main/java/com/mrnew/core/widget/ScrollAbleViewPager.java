package com.mrnew.core.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 控制ViewPager是否通过手势滑动
 */
public class ScrollAbleViewPager extends ViewPager {
    /**
     * 是否可滚动
     */
    private boolean isScrollable = true;

    /**
     * 设置是否可滚动
     *
     * @param isScrollable
     */
    public void setIsScrollable(boolean isScrollable) {
        this.isScrollable = isScrollable;
    }

    public ScrollAbleViewPager(Context context) {
        super(context);
    }

    public ScrollAbleViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isScrollable == false) {
            return false;
        } else {
            return super.onTouchEvent(ev);
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isScrollable == false) {
            return false;
        } else {
            return super.onInterceptTouchEvent(ev);
        }

    }
}
