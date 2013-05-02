/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees.widgets;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Scroller;

/**
 * HorizontialListView.java
 * 
 * @author MURVIN BHANTOOA
 * @date 02/05/2012
 */
public class HorizontialListView extends AdapterView<ListAdapter> {

	public boolean mAlwaysOverrideTouch = true;
	protected ListAdapter adapter;
	private int leftViewIndex = -1;
	private int rightViewIndex;
	protected float currX;
	protected float nextX;
	private float maxX = Integer.MAX_VALUE;
	private int displayOffset;
	protected Scroller scroller;
	private GestureDetector gestureDetector;
	private Queue<View> mRemovedViewQueue = new LinkedList<View>();
	private OnItemSelectedListener onSelectedListener;
	private OnItemClickListener onClickListener;
	private boolean hasDataChanged = false;

	public HorizontialListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();

	}

	private synchronized void initView() {
		leftViewIndex = -1;
		rightViewIndex = 0;
		displayOffset = 0;
		currX = 0;
		nextX = 0;
		maxX = Integer.MAX_VALUE;
		scroller = new Scroller(getContext());
		gestureDetector = new GestureDetector(getContext(), mOnGesture);
	}

	@Override
	public void setOnItemSelectedListener(
			AdapterView.OnItemSelectedListener listener) {
		onSelectedListener = listener;
	}

	@Override
	public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
		onClickListener = listener;
	}

	private DataSetObserver mDataObserver = new DataSetObserver() {

		@Override
		public void onChanged() {
			synchronized (HorizontialListView.this) {
				hasDataChanged = true;
			}
			invalidate();
			requestLayout();
		}

		@Override
		public void onInvalidated() {
			reset();
			invalidate();
			requestLayout();
		}
	};

	@Override
	public ListAdapter getAdapter() {
		return adapter;
	}

	@Override
	public View getSelectedView() {
		return null;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		if (this.adapter != null) {
			this.adapter.unregisterDataSetObserver(mDataObserver);
		}
		this.adapter = adapter;
		this.adapter.registerDataSetObserver(mDataObserver);
		reset();
	}

	private synchronized void reset() {
		initView();
		removeAllViewsInLayout();
		requestLayout();
	}

	@Override
	public void setSelection(int position) {
	}

	private void addAndMeasureChild(final View child, int viewPos) {
		LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
		}

		addViewInLayout(child, viewPos, params, true);
		child.measure(
				MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.UNSPECIFIED));
	}

	@Override
	protected synchronized void onLayout(boolean changed, int left, int top,
			int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (adapter == null) {
			return;
		}

		if (hasDataChanged) {
			float oldCurrentX = currX;
			initView();
			removeAllViewsInLayout();
			nextX = oldCurrentX;
			hasDataChanged = false;
		}

		if (scroller.computeScrollOffset()) {
			int scrollx = scroller.getCurrX();
			nextX = scrollx;
		}

		if (nextX < 0) {
			nextX = 0;
			scroller.forceFinished(true);
		}
		if (nextX > maxX) {
			nextX = maxX;
			scroller.forceFinished(true);
		}

		float dx = currX - nextX;

		removeNonVisibleItems(dx);
		fillList(dx);
		positionItems(dx);

		currX = nextX;

		if (!scroller.isFinished()) {
			post(new Runnable() {
				public void run() {
					requestLayout();
				}
			});
		}
	}

	private void fillList(final float dx) {
		int edge = 0;
		View child = getChildAt(getChildCount() - 1);
		if (child != null) {
			edge = child.getRight();
		}
		fillListRight(edge, dx);

		edge = 0;
		child = getChildAt(0);
		if (child != null) {
			edge = child.getLeft();
		}
		fillListLeft(edge, dx);

	}

	private void fillListRight(float rightEdge, final float dx) {
		while (rightEdge + dx < getWidth()
				&& rightViewIndex < adapter.getCount()) {

			View child = adapter.getView(rightViewIndex,
					mRemovedViewQueue.poll(), this);
			addAndMeasureChild(child, -1);
			rightEdge += child.getMeasuredWidth();

			if (rightViewIndex == adapter.getCount() - 1) {
				maxX = currX + rightEdge - getWidth();
			}
			rightViewIndex++;
		}

	}

	private void fillListLeft(int leftEdge, final float dx) {
		while (leftEdge + dx > 0 && leftViewIndex >= 0) {
			View child = adapter.getView(leftViewIndex,
					mRemovedViewQueue.poll(), this);
			addAndMeasureChild(child, 0);
			leftEdge -= child.getMeasuredWidth();
			leftViewIndex--;
			displayOffset -= child.getMeasuredWidth();
		}
	}

	private void removeNonVisibleItems(final float dx) {
		View child = getChildAt(0);
		while (child != null && child.getRight() + dx <= 0) {
			displayOffset += child.getMeasuredWidth();
			mRemovedViewQueue.offer(child);
			removeViewInLayout(child);
			leftViewIndex++;
			child = getChildAt(0);

		}

		child = getChildAt(getChildCount() - 1);
		while (child != null && child.getLeft() + dx >= getWidth()) {
			mRemovedViewQueue.offer(child);
			removeViewInLayout(child);
			rightViewIndex--;
			child = getChildAt(getChildCount() - 1);
		}
	}

	private void positionItems(final float dx) {
		if (getChildCount() > 0) {
			displayOffset += dx;
			int left = displayOffset;
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				int childWidth = child.getMeasuredWidth();
				child.layout(left, 0, left + childWidth,
						child.getMeasuredHeight());
				left += childWidth;
			}
		}
	}

	public synchronized void scrollTo(float x) {
		scroller.startScroll((int)nextX, 0, (int)(x - nextX), 0);
		requestLayout();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean handled = gestureDetector.onTouchEvent(ev);
		return handled;
	}

	protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		synchronized (HorizontialListView.this) {
			scroller.fling((int)nextX, 0, (int) -velocityX, 0, 0, (int)maxX, 0, 0);
		}
		requestLayout();

		return true;
	}

	protected boolean onDown(MotionEvent e) {
		scroller.forceFinished(true);
		return true;
	}

	private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {

		@Override
		public boolean onDown(MotionEvent e) {
			return HorizontialListView.this.onDown(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return HorizontialListView.this.onFling(e1, e2, velocityX,
					velocityY);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {

			synchronized (HorizontialListView.this) {
				nextX += (int) distanceX;
			}
			requestLayout();

			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			Rect viewRect = new Rect();
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				int left = child.getLeft();
				int right = child.getRight();
				int top = child.getTop();
				int bottom = child.getBottom();
				viewRect.set(left, top, right, bottom);
				if (viewRect.contains((int) e.getX(), (int) e.getY())) {
					if (onClickListener != null) {
						onClickListener.onItemClick(HorizontialListView.this,
								child, leftViewIndex + 1 + i,
								adapter.getItemId(leftViewIndex + 1 + i));
					}
					if (onSelectedListener != null) {
						onSelectedListener.onItemSelected(
								HorizontialListView.this, child, leftViewIndex
										+ 1 + i,
								adapter.getItemId(leftViewIndex + 1 + i));
					}
					break;
				}

			}
			return true;
		}
	};
}
