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
		removeAndRecycleAllViews(recycler);
		Log.i(LOG_TAG, "RecyclerView state = " + state);
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
				addView(child);
				layoutDecorated(child, left, top, right, bottom);
			} else {
				recycler.recycleView(child);
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
//		Log.i(LOG_TAG, "scrollVerticallyBy - dy = " + dy);
		if (dy == 0) {
			return 0;
		}
		if (getItemCount() == 0) {
			return 0;
		}

		return dy > 0? contentMoveUp(dy, recycler, state) : contentMoveDown(dy, recycler, state);
	}

	/**
	 * Contents moving up,
	 */
	private int contentMoveUp(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
		int actualDy = dy;
		int maxHeightIndex = getMaxHeightIndexInLine(getChildCount() - 1);
		View maxHeightItem = getChildAt(maxHeightIndex);
		int offscreenBottom = getDecoratedBottom(maxHeightItem) - bottomVisibleEdge();
		if (offscreenBottom >= dy) {
			offsetChildrenVertical(-dy);
			return dy;
		}
		while (getChildAdapterPosition(getChildCount() - 1) < getItemCount() - 1) {
			addNewLineAtBottom(recycler, state);
			maxHeightIndex = getMaxHeightIndexInLine(getChildCount() - 1);
			maxHeightItem = getChildAt(maxHeightIndex);
			offscreenBottom += getDecoratedMeasuredHeight(maxHeightItem);
			if (offscreenBottom >= dy) {
				break;
			}
		}

		if (offscreenBottom < dy) {
			actualDy = offscreenBottom;
		}
		offsetChildrenVertical(-actualDy);
		while (!lineVisible(0)) {
			recycleLine(0, recycler);
		}
		return actualDy;
	}

	private int contentMoveDown(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
		int actualDy = dy;
		int maxHeightItemIndex = getMaxHeightIndexInLine(0);
		View maxHeightItem = getChildAt(maxHeightItemIndex);
		int offScreenTop = topVisibleEdge() - getDecoratedTop(maxHeightItem);
		if (offScreenTop > Math.abs(actualDy)) {
			offsetChildrenVertical(-dy);
			return dy;
		}
		while (getChildAdapterPosition(0) > 0) {
			addNewLineAtTop(recycler, state);
			maxHeightItemIndex = getMaxHeightIndexInLine(0);
			maxHeightItem = getChildAt(maxHeightItemIndex);
			offScreenTop += getDecoratedMeasuredHeight(maxHeightItem);
			if (offScreenTop >= Math.abs(dy)) {
				break;
			}
		}

		if (offScreenTop < Math.abs(dy)) {
			actualDy = -offScreenTop;
		}

		offsetChildrenVertical(-actualDy);
		while (!lineVisible(getChildCount() - 1)) {
			recycleLine(getChildCount() - 1, recycler);
		}
		return actualDy;
	}

	private void addNewLineAtTop(RecyclerView.Recycler recycler, RecyclerView.State state) {
		int left, bottom = getDecoratedTop(getChildAt(getMaxHeightIndexInLine(0))), top, right;
		int maxHeight = 0;
		List<View> newChildList = new LinkedList<>();
		int posFromStart = 0;
		right = leftVisibleEdge();
		int endAdapterPosition = getChildAdapterPosition(0) - 1;
		while (posFromStart <= endAdapterPosition) {
			View newChild = recycler.getViewForPosition(posFromStart);
			measureChildWithMargins(newChild, 0, 0);
			// add view to make sure not be recycled.
			addView(newChild, newChildList.size());
			if (right + getDecoratedMeasuredWidth(newChild) > rightVisibleEdge()) {
				for (View viewToRecycle : newChildList) {
					removeAndRecycleView(viewToRecycle, recycler);
				}
				newChildList.clear();
				newChildList.add(newChild);
				right = leftVisibleEdge() + getDecoratedMeasuredWidth(newChild);
				maxHeight = getDecoratedMeasuredHeight(newChild);
			} else {
				newChildList.add(newChild);
				right += getDecoratedMeasuredWidth(newChild);
				maxHeight = Math.max(maxHeight, getDecoratedMeasuredHeight(newChild));
			}
			posFromStart ++;
		}
		left = leftVisibleEdge();
		top = bottom - maxHeight;
		for (int i = 0; i < newChildList.size(); i ++) {
			View childView = newChildList.get(i);
			right = left + getDecoratedMeasuredWidth(childView);
			layoutDecorated(childView, left, top, right, bottom);
			left = right;
		}
	}
	private void addNewLineAtBottom(RecyclerView.Recycler recycler, RecyclerView.State state) {
		int left = leftVisibleEdge(), top = getDecoratedBottom(getChildAt(getMaxHeightIndexInLine(getChildCount() - 1)));
		int right = left, bottom = top;
		int childAdapterPosition = getChildAdapterPosition(getChildCount() - 1) + 1;
		// no item to add
		if (childAdapterPosition == getItemCount()) {
			return;
		}
		while (childAdapterPosition < getItemCount()) {
			View newChild = recycler.getViewForPosition(childAdapterPosition);
			measureChildWithMargins(newChild, 0, 0);
			if (left + getDecoratedMeasuredWidth(newChild) > rightVisibleEdge()) {
				recycler.recycleView(newChild);
				return;
			} else {
				addView(newChild);
				right = left + getDecoratedMeasuredWidth(newChild);
				bottom = top + getDecoratedMeasuredHeight(newChild);
				layoutDecorated(newChild, left, top, right, bottom);
				left = right;
				childAdapterPosition ++;
			}
		}
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

	private boolean childVisible(int left, int top, int right, int bottom) {
		return Rect.intersects(new Rect(leftVisibleEdge(), topVisibleEdge(), rightVisibleEdge(), bottomVisibleEdge()),
				new Rect(left, top, right, bottom));
	}

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
		Log.i(LOG_TAG, "getMaxHeightIndexInLine - index = " + index);
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

	private int getChildAdapterPosition(int index) {
		final View child = getChildAt(index);
		return ((RecyclerView.LayoutParams)child.getLayoutParams()).getViewAdapterPosition();
	}

	private boolean lineVisible(int index) {
		int maxHeightItemIndex = getMaxHeightIndexInLine(index);
		View maxHeightItem = getChildAt(maxHeightItemIndex);
		return Rect.intersects(new Rect(leftVisibleEdge(), topVisibleEdge(), rightVisibleEdge(), bottomVisibleEdge()),
				new Rect(leftVisibleEdge(), getDecoratedTop(maxHeightItem), rightVisibleEdge(), getDecoratedBottom(maxHeightItem)));
	}

	private void recycleLine(int index, RecyclerView.Recycler recycler) {
		List<View> viewList = getAllViewsInLine(index);
		for (View viewToRecycle : viewList) {
			removeAndRecycleView(viewToRecycle, recycler);
		}
	}
}
