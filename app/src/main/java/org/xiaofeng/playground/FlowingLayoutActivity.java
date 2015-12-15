package org.xiaofeng.playground;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlowingLayoutActivity extends FlowingLayoutActivityBase {


	public class TagViewHolder extends RecyclerView.ViewHolder {
		TextView textView;
		public TagViewHolder(View itemView) {
			super(itemView);
			textView = (TextView)itemView;
		}
	}

	public class TagAdapter extends RecyclerView.Adapter<TagViewHolder> {
		List<String> strings;

		public TagAdapter(List<String> strings) {
			this.strings = strings;
		}

		@Override
		public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			TextView textView = createTextView();
			return new TagViewHolder(textView);
		}

		@Override
		public void onBindViewHolder(TagViewHolder holder, int position) {
			holder.textView.setText(strings.get(position));
		}

		@Override
		public int getItemCount() {
			return strings.size();
		}
	}

	RecyclerView recyclerView;
	StaggeredGridLayoutManager layoutManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flowing_layout);
		recyclerView = (RecyclerView)findViewById(R.id.tag_list);
		layoutManager = new StaggeredGridLayoutManager(10, StaggeredGridLayoutManager.HORIZONTAL);
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.setAdapter(new TagAdapter(generateTags()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_flowing_layout, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
