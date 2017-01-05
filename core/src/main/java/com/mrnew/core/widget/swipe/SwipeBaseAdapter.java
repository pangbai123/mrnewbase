package com.mrnew.core.widget.swipe;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.mrnew.core.BaseListAdapter;

import java.util.*;

public abstract class SwipeBaseAdapter<T, Holder> extends BaseListAdapter<T, Holder> {

    public static final String TAG = "SwipeBaseAdapter";

    public final int INVALID_POSITION = -1;
    /**
     * 显示模式，默认单开
     */
    private Mode mode = Mode.Single;
    /**
     * 当前打开的item的position
     */
    protected int openPosition = INVALID_POSITION;
    /**
     * 当前打开的所有item的position
     */
    protected Set<Integer> openPositions = new HashSet<Integer>();
    /**
     * 当前打开的所有ZSwipeItem对象
     */
    protected Set<SwipeItem> mShownLayouts = new HashSet<SwipeItem>();

    public SwipeBaseAdapter(Context context, List list) {
        super(context, list);
    }

    public abstract int getSwipeLayoutResourceId(int position);

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = onCreateView(inflater, holder, parent, getItemViewType(position));
            initialize(convertView, position);
        } else {
            updateConvertView(convertView, position);
        }
        holder = (Holder) convertView.getTag();
        onBindViewHolder(holder, getItem(position), position);
        return convertView;

    }

    /**
     * 初始化item布局调用
     *
     * @param target
     * @param position
     */
    public void initialize(View target, int position) {

        int resId = getSwipeLayoutResourceId(position);

        OnLayoutListener onLayoutListener = new OnLayoutListener(position);
        SwipeItem swipeLayout = (SwipeItem) target.findViewById(resId);
        if (swipeLayout == null)
            throw new IllegalStateException(
                    "can not find SwipeLayout in target view");

        SwipeMemory swipeMemory = new SwipeMemory(position);
        // 添加滑动监听器
        swipeLayout.addSwipeListener(swipeMemory);
        // 添加布局监听器
        swipeLayout.addOnLayoutListener(onLayoutListener);
        swipeLayout.setTag(resId, new ValueBox(position, swipeMemory,
                onLayoutListener));
        mShownLayouts.add(swipeLayout);

    }

    /**
     * 复用item布局的时候调用
     *
     * @param target
     * @param position
     */
    public void updateConvertView(View target, int position) {

        int resId = getSwipeLayoutResourceId(position);

        SwipeItem swipeLayout = (SwipeItem) target.findViewById(resId);
        if (swipeLayout == null)
            throw new IllegalStateException(
                    "can not find SwipeLayout in target view");

        ValueBox valueBox = (ValueBox) swipeLayout.getTag(resId);
        valueBox.swipeMemory.setPosition(position);
        valueBox.onLayoutListener.setPosition(position);
        valueBox.position = position;

        Log.d(TAG, "updateConvertView=" + position);

    }

    private void closeAllExcept(SwipeItem layout) {

        for (SwipeItem s : mShownLayouts) {
            if (s != layout)
                s.close();
        }
    }

    /**
     * 获取打开的所有的item的position信息
     *
     * @return
     */
    public List<Integer> getOpenItems() {

        if (mode == Mode.Multiple) {
            return new ArrayList<Integer>(openPositions);
        } else {
            return Arrays.asList(openPosition);
        }
    }

    /**
     * position位置的item是否打开
     *
     * @param position
     * @return
     */
    public boolean isOpen(int position) {
        if (mode == Mode.Multiple) {
            return openPositions.contains(position);
        } else {
            return openPosition == position;
        }
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        openPositions.clear();
        mShownLayouts.clear();
        openPosition = INVALID_POSITION;
    }

    class ValueBox {
        OnLayoutListener onLayoutListener;
        SwipeMemory swipeMemory;
        int position;

        ValueBox(int position, SwipeMemory swipeMemory,
                 OnLayoutListener onLayoutListener) {
            this.swipeMemory = swipeMemory;
            this.onLayoutListener = onLayoutListener;
            this.position = position;
        }
    }

    class OnLayoutListener implements OnSwipeLayoutListener {

        private int position;

        OnLayoutListener(int position) {
            this.position = position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void onLayout(SwipeItem v) {

            if (isOpen(position)) {
                v.open(false, false);
            } else {
                v.close(false, false);
            }
        }

    }

    class SwipeMemory implements SwipeListener {

        private int position;

        SwipeMemory(int position) {
            this.position = position;
        }

        @Override
        public void onClose(SwipeItem layout) {

            if (mode == Mode.Multiple) {
                openPositions.remove(position);
            } else {
                openPosition = INVALID_POSITION;
            }
        }

        @Override
        public void onStartOpen(SwipeItem layout) {

            if (mode == Mode.Single) {
                closeAllExcept(layout);
            }
        }

        @Override
        public void onOpen(SwipeItem layout) {

            if (mode == Mode.Multiple)
                openPositions.add(position);
            else {
                closeAllExcept(layout);
                openPosition = position;
            }
        }

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void onStartClose(SwipeItem layout) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onUpdate(SwipeItem layout, int leftOffset, int topOffset) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onHandRelease(SwipeItem layout, float xvel, float yvel) {
            // TODO Auto-generated method stub

        }
    }

}
