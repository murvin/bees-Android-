/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees.widgets;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * SnapHorzListView.java
 * 
 * @author MURVIN BHANTOOA
 * @date 04/05/2012
 */
public class SnapHorzListView extends HorizontalScrollView {
	private static final int SWIPE_MIN_DISTANCE = 5;
	private static final int SWIPE_THRESHOLD_VELOCITY = 300;
	private IListListener listener;

	@SuppressWarnings("rawtypes")
	private ArrayList mItems = null;
	private GestureDetector mGestureDetector;
	private int activeIndex = 0;

	public SnapHorzListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SnapHorzListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SnapHorzListView(Context context) {
		super(context);
	}

	public void setListListener(IListListener listener) {
		this.listener = listener;
	}

	public int getActiveIndex() {
		return this.activeIndex;
	}

	public void setActiveIndex(int activeIndex) {
		if (activeIndex >= 0 && this.activeIndex != activeIndex) {
			boolean isLeftFling = this.activeIndex < activeIndex;
			if (isLeftFling) {
				this.activeIndex = activeIndex - 1;
				handleLeftFling();
			} else {
				this.activeIndex = activeIndex + 1;
				handleRightFling();
			}
		}
	}

	public void setFeatureItems(@SuppressWarnings("rawtypes") ArrayList items) {
		LinearLayout internalWrapper = new LinearLayout(getContext());
		internalWrapper.setLayoutParams(new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		internalWrapper.setOrientation(LinearLayout.HORIZONTAL);
		addView(internalWrapper);
		this.mItems = items;

		listener.loadView(internalWrapper);

		setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				// If the user swipes
				if (mGestureDetector.onTouchEvent(event)) {
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP
						|| event.getAction() == MotionEvent.ACTION_CANCEL) {
					int scrollX = getScrollX();

					int featureWidth = v.getMeasuredWidth();
					activeIndex = ((scrollX + (featureWidth / 2)) / featureWidth);

					float offset = (activeIndex * featureWidth);
					if ((featureWidth % 2) == 1) {
						offset -= activeIndex;
					}
					smoothScrollTo((int) offset, 0);

					return true;
				} else {
					return false;
				}
			}
		});
		mGestureDetector = new GestureDetector(new MyGestureDetector());
	}

	private void handleLeftFling() {
		int featureWidth = getMeasuredWidth();
		if (activeIndex == mItems.size() - 1) {
			if (listener != null) {
				listener.onLastItemReached();
			}
		} else {
			activeIndex = (activeIndex < (mItems.size() - 1)) ? activeIndex + 1
					: mItems.size() - 1;
			int offset = (activeIndex * featureWidth);
			if ((featureWidth % 2) == 1) {
				offset -= activeIndex;
			}
			smoothScrollTo(offset, 0);
		}
	}

	private void handleRightFling() {
		int featureWidth = getMeasuredWidth();
		if (activeIndex == 0) {
			if (listener != null) {
				listener.onFirstItemReached();
			}
		} else {
			activeIndex = (activeIndex > 0) ? activeIndex - 1 : 0;
			int offset = (activeIndex * featureWidth);
			if ((featureWidth % 2) == 1) {
				offset -= activeIndex;
			}
			smoothScrollTo(offset, 0);
		}
	}

	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				// right to left
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					handleLeftFling();
					return true;
				}
				// left to right
				else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					handleRightFling();
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	}

	public interface IListListener {
		void loadView(LinearLayout internalWrapper);

		void onLastItemReached();

		void onFirstItemReached();
	}
}