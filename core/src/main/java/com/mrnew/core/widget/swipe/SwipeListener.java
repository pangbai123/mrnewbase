package com.mrnew.core.widget.swipe;


/**
 * 滑动监听器
 *
 */
public interface SwipeListener {

	public void onStartOpen(SwipeItem layout);

	public void onOpen(SwipeItem layout);

	public void onStartClose(SwipeItem layout);

	public void onClose(SwipeItem layout);

	public void onUpdate(SwipeItem layout, int leftOffset, int topOffset);

	public void onHandRelease(SwipeItem layout, float xvel, float yvel);

}
