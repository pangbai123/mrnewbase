package com.mrnew.core.widget.xlist;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import com.mrnew.R;
import com.mrnew.core.util.UiUtils;

public class XScrollView extends ScrollView implements OnScrollListener {
//    private static final String TAG = "XScrollView";

    private final static int SCROLL_BACK_HEADER = 0;
    private final static int SCROLL_BACK_FOOTER = 1;

    private final static int SCROLL_DURATION = 400;

    // when pull up >= 50px
    private final static int PULL_LOAD_MORE_DELTA = 50;

    // support iOS like pull
    private final static float OFFSET_RADIO = 1.8f;

    private float mLastY = -1;

    // used for scroll back
    private Scroller mScroller;
    // user's scroll listener
    private OnScrollListener mScrollListener;
    // for mScroller, scroll back from header or footer.
    private int mScrollBack;

    // the interface to trigger refresh and load more.
    private IXListListener mListener;

    private LinearLayout mLayout;
    private LinearLayout mContentLayout;

    private BaseXListHeader mHeaderView;
    // header view content, use it to calculate the Header's height. And hide it when disable pull refresh.
    private View mHeaderViewContent;
    private int mHeaderViewHeight = UiUtils.dp2px(60);

    private BaseXListFooter mFooterView;

    private boolean mEnablePullRefresh = true;
    private boolean mPullRefreshing = false;

    private boolean mEnablePullLoad = true;
    private boolean mEnableAutoLoad = false;
    private boolean mPullLoading = false;
    private ScrollViewListener scrollViewListener;

    public XScrollView(Context context) {
        super(context);
    }

    public XScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWithContext(context);
        setVerticalScrollBarEnabled(false);
    }

    public boolean isRefreshing() {
        return mPullRefreshing;
    }

    private void initWithContext(Context context) {
        mLayout = (LinearLayout) View.inflate(context, R.layout.xscrollview_layout, null);
        mContentLayout = (LinearLayout) mLayout.findViewById(R.id.content_layout);

        mScroller = new Scroller(context, new DecelerateInterpolator());
        // XScrollView need the scroll event, and it will dispatch the event to user's listener (as a proxy).
        this.setOnScrollListener(this);
        addXListHeaderView(new DefaultXListHeader(context));
        addXListFooterView(new DefaultXListFooter(context));
        this.addView(mLayout);
    }

    public void addXListHeaderView(BaseXListHeader xListHeader) {
        LinearLayout headerLayout = (LinearLayout) mLayout.findViewById(R.id.header_layout);
        if (mHeaderView != null) {
            headerLayout.removeView(mHeaderView);
        }
        mHeaderView = xListHeader;
        mHeaderViewContent = mHeaderView.getHeaderContentView();
        headerLayout.addView(mHeaderView);
        ViewTreeObserver observer = mHeaderView.getViewTreeObserver();
        if (null != observer) {
            observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    if (mHeaderViewContent.getHeight() != 0) {
                        mHeaderViewHeight = mHeaderViewContent.getHeight();
                    }
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
    }

    public void addXListFooterView(BaseXListFooter xListFooter) {
        LinearLayout footLayout = (LinearLayout) mLayout.findViewById(R.id.footer_layout);
        if (mFooterView != null) {
            footLayout.removeView(mFooterView);
        }
        mFooterView = xListFooter;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        footLayout.addView(mFooterView, params);
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

    /**
     * Set the content ViewGroup for XScrollView.
     *
     * @param content
     */
    public void setContentView(View content) {
        if (mLayout == null) {
            return;
        }

        if (mContentLayout == null) {
            mContentLayout = (LinearLayout) mLayout.findViewById(R.id.content_layout);
        }

        if (mContentLayout.getChildCount() > 0) {
            mContentLayout.removeAllViews();
        }
        mContentLayout.addView(content);
    }

    /**
     * Set the content View for XScrollView.
     *
     * @param content
     */
    public void setView(View content) {
        if (mLayout == null) {
            return;
        }

        if (mContentLayout == null) {
            mContentLayout = (LinearLayout) mLayout.findViewById(R.id.content_layout);
        }
        mContentLayout.addView(content);
    }

    /**
     * Enable or disable pull down refresh feature.
     *
     * @param enable
     */
    public void setPullRefreshEnable(boolean enable) {
        mEnablePullRefresh = enable;

        // disable, hide the content
        mHeaderViewContent.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * Enable or disable pull up load more feature.
     *
     * @param enable
     */
    public void setPullLoadEnable(boolean enable) {
        mEnablePullLoad = enable;

        if (!mEnablePullLoad) {
            mFooterView.setBottomMargin(0);
            mFooterView.hide();
            mFooterView.setPadding(0, 0, 0, mFooterView.getHeight() * (-1));
            mFooterView.setOnClickListener(null);

        } else {
            mPullLoading = false;
            mFooterView.setPadding(0, 0, 0, 0);
            mFooterView.show();
            mFooterView.setState(DefaultXListFooter.STATE_NORMAL);
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
     * Enable or disable auto load more feature when scroll to bottom.
     *
     * @param enable
     */
    public void setAutoLoadEnable(boolean enable) {
        mEnableAutoLoad = enable;
    }

    /**
     * Stop refresh, reset header view.
     */
    public void stopRefresh() {
        mPullRefreshing = false;
        resetHeaderHeight();
        mHeaderView.setState(BaseXListHeader.STATE_STOP);
    }

    /**
     * 自动下拉刷新
     */
    public void autoRefresh() {
        if (!mPullLoading && mEnablePullRefresh) {//当处于上拉加载的时候不响应刷新
            mPullRefreshing = true;
            mHeaderView.setState(BaseXListHeader.STATE_NORMAL);
            mHeaderView.setState(BaseXListHeader.STATE_REFRESHING);
            if (mListener != null) {
                mHeaderView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onRefresh();
                    }
                }, 500);

            }
            setPullLoadEnable(false);
            mScrollBack = SCROLL_BACK_HEADER;
            mScroller.startScroll(0, 0, 0, mHeaderViewHeight, SCROLL_DURATION);
            // trigger computeScroll
            invalidate();
        }

    }
    /**
     * Stop load more, reset footer view.
     */
    public void stopLoadMore() {
        mPullLoading = false;
        resetFooterHeight();
        mFooterView.setState(DefaultXListFooter.STATE_STOP);
    }


    /**
     * Set listener.
     *
     * @param listener
     */
    public void setIXScrollViewListener(IXListListener listener) {
        mListener = listener;
    }


    private void invokeOnScrolling() {
        if (mScrollListener instanceof OnXScrollListener) {
            OnXScrollListener l = (OnXScrollListener) mScrollListener;
            l.onXScrolling(this);
        }
    }

    private void updateHeaderHeight(float delta) {
        mHeaderView.setVisiableHeight((int) delta + mHeaderView.getVisiableHeight());

        if (mEnablePullRefresh && !mPullRefreshing) {
            // update the arrow image unrefreshing
            if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                mHeaderView.setState(BaseXListHeader.STATE_READY);
            } else {
                mHeaderView.setState(BaseXListHeader.STATE_NORMAL);
            }
        }

        // scroll to top each time
        post(new Runnable() {
            @Override
            public void run() {
                XScrollView.this.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    private void resetHeaderHeight() {
        int height = mHeaderView.getVisiableHeight();
        if (height == 0) return;

        // refreshing and header isn't shown fully. do nothing.
        if (mPullRefreshing && height <= mHeaderViewHeight) return;

        // default: scroll back to dismiss header.
        int finalHeight = 0;
        // is refreshing, just scroll back to show all the header.
//        if (mPullRefreshing && height > mHeaderHeight) {
        if (mPullRefreshing) {
            finalHeight = mHeaderViewHeight;
        }

        mScrollBack = SCROLL_BACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);

        // trigger computeScroll
        invalidate();
    }

    private void updateFooterHeight(float delta) {
        int height = mFooterView.getBottomMargin() + (int) delta;

        if (mEnablePullLoad && !mPullLoading) {
            if (height > PULL_LOAD_MORE_DELTA) {
                // height enough to invoke load  more.
                mFooterView.setState(DefaultXListFooter.STATE_READY);
            } else {
                mFooterView.setState(DefaultXListFooter.STATE_NORMAL);
            }
        }

        mFooterView.setBottomMargin(height);

        // scroll to bottom
        post(new Runnable() {
            @Override
            public void run() {
                XScrollView.this.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void resetFooterHeight() {
        int bottomMargin = mFooterView.getBottomMargin();

        if (bottomMargin > 0) {
            mScrollBack = SCROLL_BACK_FOOTER;
            mScroller.startScroll(0, bottomMargin, 0, -bottomMargin, SCROLL_DURATION);
            invalidate();
        }
    }

    private void startLoadMore() {
        if (!mPullLoading) {
            mPullLoading = true;
            mFooterView.setState(DefaultXListFooter.STATE_LOADING);
            loadMore();
        }
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

                if (isTop() && (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
                    // the first item is showing, header has shown or pull down.
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                    invokeOnScrolling();

                } else if (isBottom() && (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
                    // last item, already pulled up or want to pull up.
                    updateFooterHeight(-deltaY / OFFSET_RADIO);

                }
                break;

            default:
                // reset
                mLastY = -1;

                resetHeaderOrBottom();
                break;
        }

        return super.onTouchEvent(ev);
    }

    private void resetHeaderOrBottom() {
        if (isTop()) {
            // invoke refresh
            if (mEnablePullRefresh && mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                mPullRefreshing = true;
                mHeaderView.setState(BaseXListHeader.STATE_REFRESHING);
                refresh();
            }
            resetHeaderHeight();

        } else if (isBottom()) {
            // invoke load more.
            if (mEnablePullLoad && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
                startLoadMore();
            }
            resetFooterHeight();
        }
    }

    private boolean isTop() {
        return getScrollY() <= 0 || mHeaderView.getVisiableHeight() > mHeaderViewHeight;
    }

    private boolean isBottom() {
        return Math.abs(getScrollY() + getHeight() - computeVerticalScrollRange()) <= 5 ||
                (getScrollY() > 0 && null != mFooterView && mFooterView.getBottomMargin() > 0);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLL_BACK_HEADER) {
                mHeaderView.setVisiableHeight(mScroller.getCurrY());
            } else {
                mFooterView.setBottomMargin(mScroller.getCurrY());
            }

            postInvalidate();
            invokeOnScrolling();
        }
        super.computeScroll();
    }

    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        // Grab the last child placed in the ScrollView, we need it to determinate the bottom position.
        View view = getChildAt(getChildCount() - 1);

        if (null != view) {
            // Calculate the scroll diff
            int diff = (view.getBottom() - (view.getHeight() + view.getScrollY()));

            // if diff is zero, then the bottom has been reached
            if (diff == 0 && mEnableAutoLoad) {
                // notify that we have reached the bottom
                startLoadMore();
            }
        }
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    public interface ScrollViewListener {
        void onScrollChanged(ScrollView scrollView, int x, int y,
                             int oldx, int oldy);

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        // send to user's listener
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    private void refresh() {
        if (mEnablePullRefresh && null != mListener) {
            mListener.onRefresh();
        }
    }

    private void loadMore() {
        if (mEnablePullLoad && null != mListener) {
            mListener.onLoadMore();
        }
    }

}
