package com.mrnew.core.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.mrnew.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 指示器控件
 */
public class Pointer extends LinearLayout {
    private List<ImageView> pointers = new ArrayList<ImageView>();
    private int POSINT_SIZE = 3;
    private int currentIndex = 0;

    private int mNormalID = R.drawable.news_advert_dot_d;
    private int mCheckedID = R.drawable.news_advert_dot_p;
    private int mLayoutRes = R.layout.advert_point;
    private int mDrawableId = R.id.ad_item_v;

    public Pointer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Pointer(Context context) {
        super(context);
    }

    /**
     * 设置指示点图片资源
     *
     * @param layoutRes  指示点布局文件
     * @param drawableId 变化view id
     * @param normalRes  普通状态资源文件
     * @param checkedRes 选中资源文件
     */
    public void setResource(int layoutRes, int drawableId, int normalRes, int checkedRes) {
        mLayoutRes = layoutRes;
        mDrawableId = drawableId;
        mNormalID = normalRes;
        mCheckedID = checkedRes;
    }

    /**
     * 重置初始化指示器
     *
     * @param context
     * @param count
     */
    public void initUI(Context context, int count) {
        if (count == pointers.size()) return;
        this.POSINT_SIZE = count;
        pointers.clear();
        currentIndex = 0;
        removeAllViews();
        for (int i = 0; i < POSINT_SIZE; i++) {
            View view = LayoutInflater.from(context).inflate(mLayoutRes, null);
            ImageView imageView = (ImageView) view.findViewById(mDrawableId);
            if (i == 0) {
                imageView.setImageResource(mCheckedID);
            } else {
                imageView.setImageResource(mNormalID);
            }
            this.addView(view);
            pointers.add(imageView);
        }
    }

    /**
     * 设置当前指示点位置
     *
     * @param index
     */
    public void setCurrentIndex(int index) {
        if (pointers.isEmpty()) return;
        if (index >= POSINT_SIZE) index = POSINT_SIZE - 1;
        pointers.get(currentIndex).setImageResource(mNormalID);
        pointers.get(index).setImageResource(mCheckedID);
        currentIndex = index;
    }
}
