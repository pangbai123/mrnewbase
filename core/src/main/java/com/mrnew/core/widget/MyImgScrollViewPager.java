package com.mrnew.core.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.mrnew.core.util.UiUtils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 无限图片滚动类
 *
 * @author Administrator
 */
public class MyImgScrollViewPager extends ViewPager {
    Activity mActivity; // 上下文
    ArrayList<View> mViews = new ArrayList<>(); // 图片组
    int mScrollTime = 0;
    Timer timer;
    int curIndex = 0;
    /**
     * 触摸时按下的点 *
     */
    PointF downP = new PointF();
    /**
     * 触摸时当前的点 *
     */
    PointF curP = new PointF();
    private MyPagerAdapter myPagerAdapter;
    private OnImageCallBack mOnImageCallBack;
    private ArrayList mBanners;
    private Pointer mPointer;

    public MyImgScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 设置滑动动画时间  ,如果用默认动画时间可不用 ,反射技术实现
        new MyImgScrollViewScroller(context).setDuration(this, 700);

    }

    /**
     * 开始广告滚动
     *
     * @param mainActivity 显示广告的主界面
     *                     图片列表, 不能为null ,最少一张
     * @param scrollTime   滚动间隔 ,0为不滚动
     * @param ovalLayout   圆点容器,可为空,LinearLayout类型
     */
    public void start(Activity mainActivity, ArrayList banners, int scrollTime, Pointer ovalLayout) {
        if (mOnImageCallBack == null) {
            throw new RuntimeException("must be call setOnImageCallBack before start");
        }
        if (mainActivity == null) {
            return;
        }
        mActivity = mainActivity;
        mScrollTime = scrollTime;
        mBanners = banners;
        initListViews(banners);
        // 设置圆点
        mPointer = ovalLayout;
        initPointer();
        curIndex = 0;
        removeAllViews();
        myPagerAdapter = new MyPagerAdapter(mViews);
        setAdapter(myPagerAdapter);
        if (scrollTime != 0 && mBanners.size() > 1) {
            startTimer();
            // 触摸时停止滚动
            setOnTouchListener(mOnTouchListener);
        } else {
            stopTimer();
            setOnTouchListener(null);
        }
        if (mBanners.size() > 1) {
            setCurrentItem(100 - 100 % mBanners.size(), false);
        } else {
            setCurrentItem(0, false);
        }
        myPagerAdapter.notifyDataSetChanged();
    }

    private OnTouchListener mOnTouchListener = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                startTimer();
            } else {
                stopTimer();
            }
            return false;
        }
    };

    /**
     * 初始化
     *
     * @param banners
     */
    private void initListViews(ArrayList banners) {
        mViews.clear();
        int realSize = banners.size();
        int size = realSize;
        if (size == 2) {
            size = 4;
        }
        for (int i = 0; i < size; i++) {
            View view = mOnImageCallBack.getView(banners.get(i % realSize), i % realSize);
            mViews.add(view);
        }
    }

    // 设置圆点
    private void initPointer() {
        mPointer.initUI(mActivity, mBanners.size());
        if (mBanners.size() > 0) {
            //选中第一个
            mPointer.setCurrentIndex(0);
        }
        setOnPageChangeListener(new OnPageChangeListener() {
            public void onPageSelected(int i) {
                curIndex = i % mBanners.size();
                mPointer.setCurrentIndex(curIndex);
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageScrollStateChanged(int arg0) {
            }
        });

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
        if (mPointer != null) {
            mPointer.setResource(layoutRes, drawableId, normalRes, checkedRes);
        }
    }

    /**
     * 取得当明选中下标
     *
     * @return
     */
    public int getCurIndex() {
        return curIndex;
    }

    /**
     * 停止滚动
     */
    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * 开始滚动
     */
    public void startTimer() {
        stopTimer();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        setCurrentItem(getCurrentItem() + 1);
                    }
                });
            }
        }, mScrollTime, mScrollTime);
    }

    /**
     * 设置通用监听
     *
     * @param mOnImageCallBack
     */
    public void setOnImageCallBack(OnImageCallBack mOnImageCallBack) {
        this.mOnImageCallBack = mOnImageCallBack;
    }

    // 适配器 //循环设置
    private class MyPagerAdapter extends PagerAdapter {
        private ArrayList<View> views;

        public MyPagerAdapter(ArrayList<View> views) {
            this.views = views;
        }

        public int getCount() {
            if (views.size() == 1) {// 一张图片时不用流动
                return views.size();
            }
            return Integer.MAX_VALUE;
        }

        public Object instantiateItem(ViewGroup v, int i) {
            if (views.size() == 0) {
                return null;
            }
            View view = views.get(i % views.size());
            int index = v.indexOfChild(view);
            if (index != -1 && index != 0) {
                v.removeView(view);
            }
            if (index != 0) {
                v.addView(view, 0);
            }
            return view;
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }


        public void destroyItem(ViewGroup v, int i, Object arg2) {

        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        // 当拦截触摸事件到达此位置的时候，返回true，
        // 说明将onTouch拦截在此控件，进而执行此控件的onTouchEvent
        return true;
    }

    private int index = 0;
    private int nextIndex = 0;

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        // 每次进行onTouch事件都记录当前的按下的坐标
        curP.x = arg0.getX();
        curP.y = arg0.getY();

        if (arg0.getAction() == MotionEvent.ACTION_DOWN) {
            // 记录按下时候的坐标
            // 切记不可用 downP = curP ，这样在改变curP的时候，downP也会改变
            downP.x = arg0.getX();
            downP.y = arg0.getY();
            index = 0;
            nextIndex = 1;
            getParent().requestDisallowInterceptTouchEvent(true);
        }

        if (arg0.getAction() == MotionEvent.ACTION_MOVE) {
            index++;
            if (curP.x - downP.x == 0 && curP.y - downP.y == 0) {
                nextIndex++;
            }
            if (index == nextIndex) {
                if (Math.abs(curP.x - downP.x) <= Math.abs(curP.y - downP.y)) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    // 不需要此事件了
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
            }
        }

        if (arg0.getAction() == MotionEvent.ACTION_UP) {
            getParent().requestDisallowInterceptTouchEvent(true);
            // 在up时判断是否按下和松手的坐标为一个点
            // 如果是一个点，将执行点击事件，这是我自己写的点击事件，而不是onclick
            if (Math.abs(curP.x - downP.x) < UiUtils.dp2px(3) && Math.abs(curP.y - downP.y) < UiUtils.dp2px(3)) {
                onSingleTouch(getCurIndex());
                return true;
            }
        }

        return super.onTouchEvent(arg0);
    }

    /**
     * 单击
     */
    private void onSingleTouch(int pos) {
        mOnImageCallBack.onSingleTouch(pos);
    }


    public interface OnImageCallBack {
        View getView(Object object, int pos);

        void onSingleTouch(int pos);
    }
}
