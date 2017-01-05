/**
 * @file XListViewHeader.java
 * @create Apr 18, 2012 5:22:27 PM
 * @author Maxwin
 * @description XListView's header
 */
package com.mrnew.core.widget.xlist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;


public abstract class BaseXListHeader extends LinearLayout {

    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_REFRESHING = 2;
    public final static int STATE_STOP = 3;

    public BaseXListHeader(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public BaseXListHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public abstract void setState(int state);

    public abstract void setVisiableHeight(int height);

    public abstract int getVisiableHeight();

    public abstract View getHeaderContentView();

    public abstract void onRefreshSuccess();
}
