package org.xiaofeng.playground;

import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v7.widget.LinearSmoothScroller;
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
	int firstChildAdapterPosition = 0;
	RecyclerView.Recycler recyclerRef;
	@Override
	public RecyclerView.LayoutParams generateDefaultLayoutParams() {
		return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
	}

	@Override
	public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
		Log.i(LOG_TAG, "State = " + state);
		recyclerRef = recycler;
		if (state.isPreLayout()) {
			onPreLayoutChildren(recycler, state);
		} else {
			onRealLayoutChildren(recycler);
		}
	}

	private void onPreLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
		int firstItemAdapterPosition = getChildAdapterPosition(0);
		int currentItemPosition = firstItemAdapterPosition < 0 ? 0 : firstItemAdapterPosition;
		int left = leftVisibleEdge(), top = topVisibleEdge(), height = 0;
		int realLayoutLeft = leftVisibleEdge(), realLayoutTop = topVisibleEdge(), realLayoutHeight = 0;
		detachAndScrapAttachedViews(recycler);
		while (currentItemPosition < getItemCount()) {
			View child = recycler.getViewForPosition(currentItemPosition);
			measureChildWithMargins(child, 0, 0);
			int childWidth = getDecoratedMeasuredWidth(child);
			int childHeight = getDecoratedMeasuredHeight(child);
			if (left + childWidth > rightVisibleEdge()) {
				left = leftVisibleEdge() + childWidth;
				top += height;
				height = childHeight;
			} else {
				left += childWidth;
				height = Math.max(height, childHeight);
			}

			if (!isChildRemoved(child)) {
				if (realLayoutLeft + childWidth > rightVisibleEdge()) {
					realLayoutLeft = leftVisibleEdge() + childWidth;
					realLayoutTop += height;
					realLayoutHeight = childHeight;
				} else {
					realLayoutLeft += childWidth;
					realLayoutHeight = Math.max(realLayoutHeight, childHeight);
				}
			}
			if (!childVisible(realLayoutLeft, realLayoutTop, realLayoutLeft + childWidth, realLayoutTop + childHeight)) {
				recycler.recycleView(child);
				break;
			} else {
				int right = left + childWidth;
				int bottom = top + childHeight;
				if (isChildRemoved(child)) {
					addDisappearingView(child);
				} else {
					addView(child);
				}
				layoutDecorated(child, left, top, right, bottom);
			}

			currentItemPosition ++;
		}
	}

	private void onRealLayoutChildren(RecyclerView.Recycler recycler) {
		detachAndScrapAttachedViews(recycler);
		layoutChildrenImpl(recycler, -1);
	}

	private void layoutChildrenImpl(RecyclerView.Recycler recycler, int endPosition) {
		int left = recyclerView.getPaddingLeft(), top = recyclerView.getPaddingTop(), right = 0, bottom = 0;
		int itemCount = getItemCount();
		int containerWidth = recyclerView.getMeasuredWidth();
		int currentMaxHeight = 0;
		for (int i = firstChildAdapterPosition; i < itemCount; i ++) {
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
			if (childVisible(left, top, right, bottom) || i <= endPosition) {
				if (isChildRemoved(child)) {
					addDisappearingView(child);
				} else {
					addView(child);
				}
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
		firstChildAdapterPosition = getChildAdapterPosition(0);
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
		firstChildAdapterPosition = getChildAdapterPosition(0);
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
				// end of one line, but not reach the top line yet. recycle the line and
				// move on to next.
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
		return true;
	}

	@Override
	public void scrollToPosition(int position) {
		firstChildAdapterPosition = position;
		requestLayout();
	}

	@Override
	public void smoothScrollToPosition(final RecyclerView recyclerView, final RecyclerView.State state, int position) {
		RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
			@Override
			public PointF computeScrollVectorForPosition(int targetPosition) {
				return new PointF(0, getOffsetOfItemToFirstChild(targetPosition, recyclerRef));
			}
		};
		smoothScroller.setTargetPosition(position);
		startSmoothScroll(smoothScroller);
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

	private int getOffsetOfItemToFirstChild(int adapterPosition, RecyclerView.Recycler recycler) {
		int firstChildAdapterPosition = getChildAdapterPosition(0);
		if (firstChildAdapterPosition == adapterPosition) {
			// first child is target, just make sure it is fully visible.
			return topVisibleEdge() - getDecoratedTop(getChildAt(0));
		}

		if (adapterPosition > firstChildAdapterPosition) {
			int lastChildAdapterPosition = getChildAdapterPosition(getChildCount() - 1);
			// target child in screen, no need to calc.
			if (lastChildAdapterPosition >= adapterPosition) {
				int targetChildIndex = getChildCount() - 1 - (lastChildAdapterPosition - adapterPosition);
				return getDecoratedTop(getChildAt(targetChildIndex)) - topVisibleEdge();
			} else {
				// target child is below screen edge
				int offset = getDecoratedBottom(getChildAt(getMaxHeightIndexInLine(getChildCount() - 1))) - topVisibleEdge();
				int targetAdapterPosition = lastChildAdapterPosition + 1;
				int left = leftVisibleEdge();
				int height = 0;
				while (targetAdapterPosition != adapterPosition) {
					View nextChild = recycler.getViewForPosition(targetAdapterPosition);
					measureChildWithMargins(nextChild, 0, 0);
					if (left + getDecoratedMeasuredWidth(nextChild) > rightVisibleEdge()) {
						// should start a new line
						offset += height;
						left = leftVisibleEdge() + getDecoratedMeasuredWidth(nextChild);
						height = getDecoratedMeasuredHeight(nextChild);
					} else {
						left += getDecoratedMeasuredWidth(nextChild);
						height = Math.max(height, getDecoratedMeasuredHeight(nextChild));
					}
					recycler.recycleView(nextChild);
					targetAdapterPosition ++;
				}
				return offset;
			}
		} else {
			// target is off screen top, Need to start from beginning in dataset
			int targetAdapterPosition = 0, left = leftVisibleEdge(), height = 0;
			int offset = topVisibleEdge() - getDecoratedTop(getChildAt(0));
			// first find out target item location
			while (targetAdapterPosition <= firstChildAdapterPosition) {
				View child = recycler.getViewForPosition(targetAdapterPosition);
				measureChildWithMargins(child, 0, 0);
				if (left + getDecoratedMeasuredWidth(child) > rightVisibleEdge()) {
					left = leftVisibleEdge() + getDecoratedMeasuredWidth(child);
					height = getDecoratedMeasuredHeight(child);
					if (targetAdapterPosition >= targetAdapterPosition) {
						offset += height;
					}
				} else {
					left += getDecoratedMeasuredWidth(child);
					height = Math.max(height, getDecoratedMeasuredHeight(child));
				}
				targetAdapterPosition ++;
			}
			return -offset;
		}
	}

	private boolean isChildRemoved(View child) {
		return ((RecyclerView.LayoutParams)child.getLayoutParams()).isItemRemoved();
	}
}
