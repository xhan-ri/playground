package org.xiaofeng.playground;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by xiaofeng on 12/15/15.
 */
public class FlowLayoutManager extends RecyclerView.LayoutManager {
	private static final String LOG_TAG = "FlowLayoutManager";
	RecyclerView recyclerView;
	@Override
	public RecyclerView.LayoutParams generateDefaultLayoutParams() {
		return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
	}

	@Override
	public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
		super.onLayoutChildren(recycler, state);
		removeAndRecycleAllViews(recycler);
		int left = recyclerView.getPaddingLeft(), top = recyclerView.getPaddingTop(), right = 0, bottom = 0;
		int itemCount = getItemCount();
		int containerWidth = recyclerView.getMeasuredWidth();
		int currentMaxHeight = 0;
		for (int i = 0; i < itemCount; i ++) {
			View child = recycler.getViewForPosition(i);
			measureChildWithMargins(child, 0, 0);
			int itemWidth = getDecoratedMeasuredWidth(child), itemHeight = getDecoratedMeasuredHeight(child);
			if (left + itemWidth >= containerWidth - recyclerView.getPaddingRight()) {
				left = leftVisibleEdge();
				top += currentMaxHeight;
				currentMaxHeight = 0;
			}
			right = left + itemWidth;
			bottom = top + itemHeight;
			if (childVisible(left, top, right, bottom)) {
				Log.i(LOG_TAG, "Visible Item " + i + ": left = " + left + ", top = " + top + ", right = " + right + ", bottom = " + bottom);
				addView(child);
				layoutDecorated(child, left, top, right, bottom);
			} else {
				return;
			}

			currentMaxHeight = Math.max(currentMaxHeight, itemHeight);
			left = right;
		}

	}

	@Override
	public boolean canScrollHorizontally() {
		return false;
	}

	@Override
	public boolean canScrollVertically() {
		return true;
	}

	@Override
	public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
		if (dy == 0) {
			return 0;
		}
		if (getItemCount() == 0) {
			return 0;
		}

		for (int i = 0; i < getChildCount(); i ++) {
			View child = getChildAt(i);
			if (!childVisible(child)) {
				removeAndRecycleView(child, recycler);
			}
		}

		if (getChildCount() == 0) {
			return 0;
		}



		return dy > 0? contentMoveUp(dy, recycler, state) : contentMoveDown(dy, recycler, state);
	}

	/**
	 * Contents moving up,
	 * @param dy
	 * @param recycler
	 * @param state
	 * @return
	 */
	private int contentMoveUp(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
		int maxHeightIndexInFirstLine = getMaxHeightIndexInLine(0);

	}

	private int contentMoveDown(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {

	}

	@Override
	public void onAttachedToWindow(RecyclerView view) {
		super.onAttachedToWindow(view);
		this.recyclerView = view;
	}

	@Override
	public boolean supportsPredictiveItemAnimations() {
		return false;
	}

	private int leftVisibleEdge() {
		return getPaddingLeft();
	}

	private int rightVisibleEdge() {
		return getWidth() - getPaddingRight();
	}

	private int topVisibleEdge() {
		return getPaddingTop();
	}

	private int bottomVisibleEdge() {
		return getHeight() - getPaddingBottom();
	}

	private boolean childVisible(View child) {
		return childVisible(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
	}
	private boolean childVisible(int left, int top, int right, int bottom) {
		return Rect.intersects(new Rect(leftVisibleEdge(), topVisibleEdge(), rightVisibleEdge(), bottomVisibleEdge()),
				new Rect(left, top, right, bottom));
	}

//	private int getMaxHeightInLine(int index) {
//		return Math.max(getMaxHeightInLineBefore(index), getMaxHeightInLineAfter(index));
//	}
//	private int getMaxHeightInLineBefore(int indexOfChild) {
//		final View child = getChildAt(indexOfChild);
//		int maxHeight = getDecoratedMeasuredHeight(child);
//		// current child already first in line, just return current child height.
//		if (getDecoratedLeft(child) == leftVisibleEdge()) {
//			return maxHeight;
//		}
//		for (int i = indexOfChild - 1; i >= 0; i --) {
//			View beforeChild = getChildAt(i);
//			maxHeight = Math.max(maxHeight, getDecoratedMeasuredHeight(beforeChild));
//			if (firstOneInTheLine(i)) {
//				return maxHeight;
//			}
//		}
//		return maxHeight;
//	}
//
//	private int getMaxHeightInLineAfter(int indexOfChild) {
//		final View child = getChildAt(indexOfChild);
//		int maxHeight = getDecoratedMeasuredHeight(child);
//		if (lastOneInTheLine(indexOfChild)) {
//			return maxHeight;
//		}
//		for (int i = indexOfChild + 1; i < getItemCount(); i ++) {
//			View afterChild = getChildAt(i);
//			if (firstOneInTheLine(i)) {
//				// already reached the first one of next line.
//				return maxHeight;
//			} else {
//				maxHeight = Math.max(maxHeight, getDecoratedMeasuredHeight(afterChild));
//			}
//		}
//		return maxHeight;
//	}

	private boolean firstOneInTheLine(int index) {
		if (index == 0) {
			return true;
		} else {
			return getDecoratedLeft(getChildAt(index)) <= leftVisibleEdge();
		}
	}

	private boolean lastOneInTheLine(int index) {
		if (getChildCount() == 0 || index == getChildCount() - 1) {
			return true;
		}
		return firstOneInTheLine(index + 1);
	}

	private int getMaxHeightIndexInLine(int index) {
		final View child = getChildAt(index);
		int maxIndexBefore = index, maxIndexAfter = index, maxHeightBefore = getDecoratedMeasuredHeight(child), maxHeightAfter = getDecoratedMeasuredHeight(child);
		int currentIndex = index;
		while (currentIndex >= 0 && !firstOneInTheLine(currentIndex)) {
			final View beforeChild = getChildAt(currentIndex);
			if (getDecoratedMeasuredHeight(beforeChild) > maxHeightBefore) {
				maxIndexBefore = currentIndex;
			}
			currentIndex --;
		}

		currentIndex = index;
		while (currentIndex < getChildCount() && !lastOneInTheLine(currentIndex)) {
			final View afterChild = getChildAt(currentIndex);
			if (getDecoratedMeasuredHeight(afterChild) > maxHeightAfter) {
				maxIndexAfter = currentIndex;
			}
			currentIndex ++;
		}
		if (maxHeightBefore >= maxHeightAfter) {
			return maxIndexBefore;
		}
		return maxIndexAfter;
	}

	private List<View> getAllViewsInLine(int index) {
		int firstItemIndex = index;
		while(!firstOneInTheLine(firstItemIndex)) {
			firstItemIndex --;
		}

		List<View> viewList = new LinkedList<>();
		viewList.add(getChildAt(firstItemIndex));
		int nextItemIndex = firstItemIndex + 1;
		while (nextItemIndex < getChildCount() && !firstOneInTheLine(nextItemIndex)) {
			viewList.add(getChildAt(nextItemIndex));
			nextItemIndex ++;
		}
		return viewList;
	}
}
