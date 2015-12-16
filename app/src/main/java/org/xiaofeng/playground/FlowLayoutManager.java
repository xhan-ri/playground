package org.xiaofeng.playground;

import android.support.v7.widget.RecyclerView;

/**
 * Created by xiaofeng on 12/15/15.
 */
public class FlowLayoutManager extends RecyclerView.LayoutManager {
	@Override
	public RecyclerView.LayoutParams generateDefaultLayoutParams() {
		return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
	}
}
