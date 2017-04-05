/**
 * @file XListView.java
 * @package me.maxwin.view
 * @create Mar 18, 2012 6:28:41 PM
 * @author Maxwin
 * @description An ListView support (a) Pull down to refresh, (b) Pull up to load more.
 * Implement IXListViewListener, and see stopRefresh() / stopLoadMore().
 */
package com.mrnew.core.widget.xlist;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import com.mrnew.core.util.UiUtils;

public class XListView extends ListView implements OnScrollListener {

    private float mLastY = -1; // save event y
    private Scroller mScroller; // used for scroll back
    private OnScrollListener mScrollListener; // user's scroll listener

    // the interface to trigger refresh and load more.
    private IXListListener mListViewListener;

    // -- header view
    private BaseXListHeader mHeaderView;
    // header view content, use it to calculate the Header's height. And hide it
    // when disable pull refresh.
    private View mHeaderViewContent;
    private int mHeaderViewHeight = UiUtils.dp2px(60); // header view's height
    private boolean mEnablePullRefresh = true;
    private boolean mPullRefreshing = false; // is refreashing.

    // -- footer view
    private BaseXListFooter mFooterView;
    private boolean mEnablePullLoad;
    private boolean mPullLoading;
    private boolean mIsFooterReady = false;

    // total list items, used to detect is at the bottom of listview.
    private int mTotalItemCount;

    // for mScroller, scroll back from header or footer.
    private int mScrollBack;
    private final static int SCROLLBACK_HEADER = 0;
    private final static int SCROLLBACK_FOOTER = 1;

    private final static int SCROLL_DURATION = 400; // scroll back duration
    private final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px
    // at bottom, trigger
    // load more.
    private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
    // feature.
    private Handler mHandler = new Handler();


    //记录首次按下位置
    private float mFirstPosition = 0;
    //是否正在放大
    private Boolean mScaling = false;
    private ImageView mScaleImageView;
    private int imgHight;
    private boolean ishaveImg = false;
    private OnScrollListener mScrollListener1;

    /**
     * @param context
     */
    public XListView(Context context) {
        super(context);
    }

    public XListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWithContext(context, attrs);
        setVerticalScrollBarEnabled(false);
    }

    private void initWithContext(Context context, AttributeSet attrs) {
        mScroller = new Scroller(context, new DecelerateInterpolator());
        // XListView need the scroll event, and it will dispatch the event to
        // user's listener (as a proxy).
        super.setOnScrollListener(this);
        addXListHeaderView(new DefaultXListHeader(context));
        addXListFooterView(new DefaultXListFooter(context));

    }

    public void addXListHeaderView(BaseXListHeader xListHeader) {
        if (mHeaderView != null) {
            removeHeaderView(mHeaderView);
        }
        mHeaderView = xListHeader;
        mHeaderViewContent = mHeaderView.getHeaderContentView();
        addHeaderView(mHeaderView);
        mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (mHeaderViewContent.getHeight() != 0) {
                            mHeaderViewHeight = mHeaderViewContent.getHeight();
                        }
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
    }

    public void addXListFooterView(BaseXListFooter xListFooter) {
        if (mFooterView != null) {
            removeFooterView(mFooterView);
        }
        mFooterView = xListFooter;
    }

    /**
     * 外部网络刷新成功后主动调用
     */
    public void onRefreshSuccess() {
        mHeaderView.onRefreshSuccess();
    }

    /**
     * 外部网络刷新成功后主动调用
     */
    public void onLoadSuccess() {
        mFooterView.onLoadSuccess();
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        // make sure XListViewFooter is the last footer view, and only add once.
        if (!mIsFooterReady) {
            mIsFooterReady = true;
            addFooterView(mFooterView);
        }
        super.setAdapter(adapter);
    }

    /**
     * enable or disable pull down refresh feature.
     *
     * @param enable
     */
    public void setPullRefreshEnable(boolean enable) {
        mEnablePullRefresh = enable;
        if (!mEnablePullRefresh) { // disable, hide the content
            mHeaderViewContent.setVisibility(View.INVISIBLE);
        } else {
            mHeaderViewContent.setVisibility(View.VISIBLE);
        }
    }

    /**
     * enable or disable pull up load more feature.
     *
     * @param enable
     */
    public void setPullLoadEnable(boolean enable) {
        mEnablePullLoad = enable;
        if (!mEnablePullLoad) {
            mFooterView.hide();
            mFooterView.setOnClickListener(null);
        } else {
//            mPullLoading = false;
            mFooterView.show();
//            mFooterView.setState(XListViewFooter.STATE_NORMAL);
            // both "pull up" and "click" will invoke load more.
            mFooterView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLoadMore();
                }
            });
        }
    }

    /**
     * stop refresh, reset header view.
     */
    public void stopRefresh() {
        mPullRefreshing = false;
        resetHeaderHeight();
        mHeaderView.setState(BaseXListHeader.STATE_STOP);
    }

    /**
     * stop load more, reset footer view.
     */
    public void stopLoadMore() {
        mPullLoading = false;
        resetFooterHeight();
        mFooterView.setState(DefaultXListFooter.STATE_STOP);
    }


    private void invokeOnScrolling() {
        if (mScrollListener instanceof OnXScrollListener) {
            OnXScrollListener l = (OnXScrollListener) mScrollListener;
            l.onXScrolling(this);
        }
        if (mScrollListener1 instanceof OnXScrollListener) {
            OnXScrollListener l = (OnXScrollListener) mScrollListener1;
            l.onXScrolling(this);
        }
    }

    private void updateHeaderHeight(float delta) {
        mHeaderView.setVisiableHeight((int) delta
                + mHeaderView.getVisiableHeight());
        if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
            if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                mHeaderView.setState(BaseXListHeader.STATE_READY);
            } else {
                mHeaderView.setState(BaseXListHeader.STATE_NORMAL);
            }
        }
        setSelection(0); // scroll to top each time
    }

    /**
     * reset header view's height.
     */
    private void resetHeaderHeight() {
        int height = mHeaderView.getVisiableHeight();
    /*    if (height == 0) // not visible.
            return;
        // refreshing and header isn't shown fully. do nothing.
        if (mPullRefreshing && height <= mHeaderViewHeight) {
            return;
        }*/
        int finalHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mPullRefreshing) {
            finalHeight = mHeaderViewHeight;
        }
        mScrollBack = SCROLLBACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height,
                SCROLL_DURATION);
        // trigger computeScroll
        invalidate();
    }

    private void updateFooterHeight(float delta) {
        int height = mFooterView.getBottomMargin() + (int) delta;
        if (mEnablePullLoad && !mPullLoading) {
            if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load
                // more.
                mFooterView.setState(DefaultXListFooter.STATE_READY);
            } else {
                mFooterView.setState(DefaultXListFooter.STATE_NORMAL);
            }
        }
        mFooterView.setBottomMargin(height);
    }

    private void resetFooterHeight() {
        int bottomMargin = mFooterView.getBottomMargin();
        if (bottomMargin > 0) {
            mScrollBack = SCROLLBACK_FOOTER;
            mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
                    SCROLL_DURATION);
            invalidate();
        }
    }

    private void startLoadMore() {
        if (!mPullRefreshing && !mPullLoading) {//当处于刷新的时候不响应加载更多
            mPullLoading = true;
            mFooterView.setState(DefaultXListFooter.STATE_LOADING);
            if (mListViewListener != null) {
                mListViewListener.onLoadMore();
            }
        }
    }

    public void setScaleImageView(ImageView img) {
        this.mScaleImageView = img;
        ishaveImg = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (getFirstVisiblePosition() == 0
                        && (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
                    // the first item is showing, header has shown or pull down.
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                    invokeOnScrolling();
                } else if (getLastVisiblePosition() == mTotalItemCount - 1
                        && (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
                    // last item, already pulled up or want to pull up.
                    updateFooterHeight(-deltaY / OFFSET_RADIO);
                }

                if (getFirstVisiblePosition() == 0 && ishaveImg) {
                    if (imgHight == 0) {
                        imgHight = mScaleImageView.getHeight();
                    }
                    if (!mScaling) {
                        if (getScrollY() == 0) {
                            mFirstPosition = ev.getY();// 滚动到顶部时记录位置，否则正常返回
                        } else {
                            break;
                        }
                    }
                    int distance = (int) ((ev.getY() - mFirstPosition) * 0.4); //滚动距离乘以一个系数
                    if (distance < 0) { //当前位置比记录位置要小，正常返回
                        break;
                    }
                    // 处理放大
                    mScaling = true;
                    {
                        ViewGroup.LayoutParams coverLayoutParams = mScaleImageView.getLayoutParams();
                        coverLayoutParams.height = imgHight + distance;
                        coverLayoutParams.width = LayoutParams.MATCH_PARENT;
                        mScaleImageView.setLayoutParams(coverLayoutParams);
                    }
                    return true; //返回true表示已经完成触摸事件，不再处理
                }
                break;
            default:
                if (getFirstVisiblePosition() == 0 && ishaveImg) {
                    if (mScaling) {
                        // 还原
                        mScaling = false;
                        ViewGroup.LayoutParams coverLayoutParams = mScaleImageView.getLayoutParams();
                        coverLayoutParams.height = imgHight;
                        coverLayoutParams.width = LayoutParams.MATCH_PARENT;
                        mScaleImageView.setLayoutParams(coverLayoutParams);
                    }
                }

                mLastY = -1; // reset

                resetHeaderOrBottom();
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void resetHeaderOrBottom() {
        if (getFirstVisiblePosition() == 0) {
            // invoke refresh
            if (mEnablePullRefresh
                    && mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                if (!mPullLoading) {//当处于上拉加载的时候不响应刷新
                    mPullRefreshing = true;
                    mHeaderView.setState(BaseXListHeader.STATE_REFRESHING);
                    if (mListViewListener != null) {
                        mListViewListener.onRefresh();
                    }
                }
            }
            resetHeaderHeight();
        } else {
            //重写预加载，使其满足需求；mTotalItemCount=list.size()+2;
            if (mEnablePullLoad
                    && getLastVisiblePosition() >= mTotalItemCount - 7) {
                startLoadMore();
            }
            resetFooterHeight();
        }
    }


    /**
     * 自动下拉刷新
     */
    public void autoRefresh() {
        if (!mPullLoading && mEnablePullRefresh) {//当处于上拉加载的时候不响应刷新
            mPullRefreshing = true;
            mHeaderView.setState(BaseXListHeader.STATE_NORMAL);
            mHeaderView.setState(BaseXListHeader.STATE_REFRESHING);
            if (mListViewListener != null) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListViewListener.onRefresh();
                    }
                }, 500);

            }
            setPullLoadEnable(false);
            mScrollBack = SCROLLBACK_HEADER;
            mScroller.startScroll(0, 0, 0, mHeaderViewHeight,
                    SCROLL_DURATION);
            // trigger computeScroll
            invalidate();
        }
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLLBACK_HEADER) {
                mHeaderView.setVisiableHeight(mScroller.getCurrY());
            } else {
                mFooterView.setBottomMargin(mScroller.getCurrY());
            }
            postInvalidate();
            invokeOnScrolling();
        }
        super.computeScroll();
    }

    /**
     * 外部不能调用
     *
     * @param l
     */
    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    /**
     * 补充供外部调用
     *
     * @param l
     */
    public void setOnScrollListenerExt(OnScrollListener l) {
        mScrollListener1 = l;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
        if (mScrollListener1 != null) {
            mScrollListener1.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // send to user's listener
        mTotalItemCount = totalItemCount;
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
                    totalItemCount);
        }
        if (mScrollListener1 != null) {
            mScrollListener1.onScroll(view, firstVisibleItem, visibleItemCount,
                    totalItemCount);
        }
    }

    public void setXListViewListener(IXListListener l) {
        mListViewListener = l;
    }


    public boolean isRefreshing() {
        return mPullRefreshing;
    }

    public boolean isLoading() {
        return mPullLoading;
    }

}

