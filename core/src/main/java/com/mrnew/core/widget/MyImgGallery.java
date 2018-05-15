package com.mrnew.core.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import com.mrnew.core.util.UiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 无限图片滚动类
 *
 * @author Administrator
 */
public class MyImgGallery extends Gallery {
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
    private MyAdapter myPagerAdapter;
    private OnImageCallBack mOnImageCallBack;
    private List mBanners;
    private boolean isAutoResume = false;
    private boolean isTimerStart = false;
    private boolean isCirculate;
    private Pointer mPointer;

    public MyImgGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getX() - e2.getX() < 0.0F) {
            onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
        } else {
            onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
        }
        return true;
    }

    @Override
    public void playSoundEffect(int soundConstant) {
        //去除切换音效
    }

    public void onPause() {
        if (isTimerStart) {
            stopTimer();
            isAutoResume = true;
        }
    }

    public void onResume() {
        if (!isTimerStart) {
            if (isAutoResume) {
                isAutoResume = false;
                startTimer();
            }
        }
    }

    /**
     * 开始广告滚动
     *
     * @param mainActivity 显示广告的主界面
     *                     图片列表, 不能为null ,最少一张
     * @param scrollTime   滚动间隔 ,0为不滚动
     * @param ovalLayout   圆点容器,可为空,LinearLayout类型
     * @param isCirculate  是否无限循环
     */
    public void start(Activity mainActivity, List banners, int scrollTime, Pointer ovalLayout, boolean isCirculate) {
        if (mOnImageCallBack == null) {
            throw new RuntimeException("must be call setOnImageCallBack before start");
        }
        if (mainActivity == null) {
            return;
        }
        if (!isCirculate) {
            scrollTime = 0;
        }
        mActivity = mainActivity;
        mScrollTime = scrollTime;
        mBanners = banners;
        this.isCirculate = isCirculate;
        initListViews(banners);
        // 设置圆点
        mPointer = ovalLayout;
        initPointer();
        curIndex = 0;
        myPagerAdapter = new MyAdapter(mViews);
        setAdapter(myPagerAdapter);
        if (scrollTime > 0 && mBanners.size() > 1) {
            startTimer();
            // 触摸时停止滚动
            setOnTouchListener(mOnTouchListener);
        } else {
            stopTimer();
            setOnTouchListener(null);
        }
        if (scrollTime > 0 && mBanners.size() > 1) {
            setSelection(200 - 200 % mBanners.size(), false);
        } else {
            if (!mBanners.isEmpty()) {
                setSelection(0, false);
            }
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
    private void initListViews(List banners) {
        mViews.clear();
        int realSize = banners.size();
        int size = realSize;
        if (isCirculate && size == 2) {
            size = 4;
        }
        for (int i = 0; i < size; i++) {
            View view = mOnImageCallBack.getView(banners.get(i % realSize), i % realSize);
            mViews.add(view);
        }
    }

    // 设置圆点
    private void initPointer() {
        if (mPointer != null) {
            mPointer.initUI(mActivity, mBanners.size());
            if (mBanners.size() > 0) {
                //选中第一个
                mPointer.setCurrentIndex(0);
            }
        }
        setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mBanners.size() != 0) {
                    curIndex = position % mBanners.size();
                } else {
                    curIndex = 0;
                }
                if (mPointer != null) {
                    mPointer.setCurrentIndex(curIndex);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
        isTimerStart = false;
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
        isTimerStart = true;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
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

    private class MyAdapter extends BaseAdapter {
        private ArrayList<View> views;

        public MyAdapter(ArrayList<View> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            if (views.size() == 1) {// 一张图片时不用流动
                return 1;
            }
            if (!isCirculate) {
                return views.size();
            }
            return Integer.MAX_VALUE;
        }

        @Override
        public Object getItem(int position) {
            return views.get(position % views.size());
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            if (views.size() == 0) {
                return null;
            }
            return (View) getItem(i);
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
