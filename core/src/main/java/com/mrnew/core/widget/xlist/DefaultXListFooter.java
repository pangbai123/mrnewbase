/**
 * @file XFooterView.java
 * @create Mar 31, 2012 9:33:43 PM
 * @author Maxwin
 * @description XListView's footer
 */
package com.mrnew.core.widget.xlist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mrnew.R;


public class DefaultXListFooter extends BaseXListFooter {
    private Context mContext;

    private View mContentView;
    private View mProgressBar;
    private TextView mHintView;
    private int mState;

    public DefaultXListFooter(Context context) {
        super(context);
        initView(context);
    }

    public DefaultXListFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void setState(int state) {
        if (state == mState) return;
        switch (state) {
            case STATE_NORMAL:
                mProgressBar.setVisibility(INVISIBLE);
                mHintView.setVisibility(VISIBLE);
                mHintView.setText(R.string.xlistview_footer_hint_normal);
                break;
            case STATE_READY:
                mHintView.setVisibility(VISIBLE);
                mProgressBar.setVisibility(INVISIBLE);
                mHintView.setText(R.string.xlistview_footer_hint_ready);
                break;
            case STATE_LOADING:
                mHintView.setVisibility(INVISIBLE);
                mProgressBar.setVisibility(VISIBLE);
                break;
            case STATE_STOP:
                break;
            default:
        }

        mState = state;
    }

    public void setBottomMargin(int height) {
        if (height < 0) return;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        lp.bottomMargin = height;
        mContentView.setLayoutParams(lp);
    }

    public int getBottomMargin() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        return lp.bottomMargin;
    }


    /**
     * hide footer when disable pull load more
     */
    public void hide() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        lp.height = 0;
        mContentView.setLayoutParams(lp);
    }

    /**
     * show footer
     */
    public void show() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        mContentView.setLayoutParams(lp);
    }

    @Override
    public void onLoadSuccess() {
    }

    private void initView(Context context) {
        mContext = context;
        LinearLayout moreView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.xlistview_footer, null);
        addView(moreView);
        moreView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        mContentView = moreView.findViewById(R.id.xlistview_footer_content);
        mProgressBar = moreView.findViewById(R.id.xlistview_footer_progressbar);
        mHintView = (TextView) moreView.findViewById(R.id.xlistview_footer_hint_textview);
    }


}
