package org.xiaofeng.playground;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Created by xiaofeng on 8/28/15.
 */
public class PageAdapter extends RecyclerView.Adapter<PageItemViewHolder> {
	private int start, step;

	public PageAdapter(int start, int step) {
		this.start = start;
		this.step = step;
	}

	@Override
	public PageItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new PageItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));
	}

	@Override
	public void onBindViewHolder(PageItemViewHolder holder, int position) {
		int current = start + step * position;
		holder.title.setText("Item " + position);
		holder.content.setText("Current value = " + current);
	}

	@Override
	public int getItemCount() {
		return 100;
	}
}
