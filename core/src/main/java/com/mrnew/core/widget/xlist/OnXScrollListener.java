package com.mrnew.core.widget.xlist;

import android.view.View;
import android.widget.AbsListView;

/**
 * Created by mrnew on 2016/10/28.
 * email:474923660@qq.com
 */
public interface OnXScrollListener extends AbsListView.OnScrollListener {
    /**
     * you can listen ListView.OnScrollListener or this one. it will invoke
     * onXScrolling when header/footer scroll back.
     */
    void onXScrolling(View view);
}
