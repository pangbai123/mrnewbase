/**
 * @file XFooterView.java
 * @create Mar 31, 2012 9:33:43 PM
 * @author Maxwin
 * @description XListView's footer
 */
package com.mrnew.core.widget.xlist;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;


public abstract class BaseXListFooter extends LinearLayout {
    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_LOADING = 2;
    public final static int STATE_STOP = 3;

    private Context mContext;

    public BaseXListFooter(Context context) {
        super(context);
    }

    public BaseXListFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public abstract void setState(int state);

    public abstract void setBottomMargin(int height);

    public abstract int getBottomMargin();

    public abstract void hide();

    public abstract void show();

    public abstract void onLoadSuccess();
}
