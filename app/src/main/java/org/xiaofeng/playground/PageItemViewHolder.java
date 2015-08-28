package org.xiaofeng.playground;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by xiaofeng on 8/28/15.
 */
public class PageItemViewHolder extends RecyclerView.ViewHolder {
	public TextView title, content;

	public PageItemViewHolder(View itemView) {
		super(itemView);
		title = (TextView) itemView.findViewById(R.id.item_title);
		content = (TextView) itemView.findViewById(R.id.item_content);
	}
}
