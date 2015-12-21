package org.xiaofeng.playground;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FlowLayoutActivity extends AppCompatActivity {

	private RecyclerView recyclerView;
	private static final int MIN_LEN = 3;
	private static final int MAX_LEN = 10;
	private static final int MAX_LINES = 1;
	private static final int TEST_ITEM_SIZE = 400;
	private static final Random random = new Random();
	private static final String STRING_BASE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final int STRING_BASE_LEN = STRING_BASE.length();

	class FlowItemViewHolder extends RecyclerView.ViewHolder {
		public TextView itemText;
		public FlowItemViewHolder(View itemView) {
			super(itemView);
			itemText = (TextView)itemView.findViewById(R.id.flow_item_text);
		}
	}

	class FlowLayoutAdapter extends RecyclerView.Adapter<FlowItemViewHolder> {
		List<String> items;

		public FlowLayoutAdapter(List<String> items) {
			this.items = items;
		}

		@Override
		public FlowItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			return new FlowItemViewHolder(inflater.inflate(R.layout.flow_item, parent, false));
		}

		@Override
		public void onBindViewHolder(final FlowItemViewHolder holder, final int position) {
			holder.itemText.setText(items.get(position));
			holder.itemText.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int childPosition = recyclerView.getChildAdapterPosition(v);
//					remove5Items(childPosition);
					toastChildInfo(childPosition);
				}

				private void remove5Items(int childPosition) {
					int firstPosition = childPosition - 2;
					firstPosition = Math.max(firstPosition, 0);
					int lastPosition = childPosition + 2;
					lastPosition = Math.min(lastPosition, getItemCount() - 1);
					int index = lastPosition;
					while (index >= firstPosition) {
						items.remove(index);
						index --;
					}
					notifyItemRangeRemoved(firstPosition, lastPosition - firstPosition + 1);
				}

				private void toastChildInfo(int childPosition) {
					String text = items.get(childPosition);
					Toast.makeText(holder.itemView.getContext(), text + " (position = " + childPosition + ")", Toast.LENGTH_SHORT).show();
				}
			});
		}

		@Override
		public int getItemCount() {
			return items.size();
		}

		public void insertAt(int index, String val) {
			items.add(index, val);
			notifyItemInserted(index);
		}
	}

	FlowLayoutAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flow_layout);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				int middleChildIndex = recyclerView.getChildCount() / 2;
				int adapterIndexToInsert = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(middleChildIndex));
				String newString = generateTestString(adapter.getItemCount() % STRING_BASE_LEN);
				adapter.insertAt(adapterIndexToInsert, newString);
			}
		});
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
		adapter = new FlowLayoutAdapter(generateTestStrings());

		recyclerView.setAdapter(adapter);
		recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
			@Override
			public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
				super.getItemOffsets(outRect, view, parent, state);
				outRect.left = 10;
				outRect.top = 10;
				outRect.right = 10;
				outRect.bottom = 10;
			}
		});
		recyclerView.setLayoutManager(
				new FlowLayoutManager().setAlignment(FlowLayoutManager.Alignment.RIGHT));
	}

	private static List<String> generateTestStrings() {
		ArrayList<String> strings = new ArrayList<>(TEST_ITEM_SIZE);
		for (int i = 0; i < TEST_ITEM_SIZE; i ++) {
			strings.add(generateTestString(i % STRING_BASE.length()));
		}
		return strings;
	}

	private static String generateTestString(int index) {
		StringBuilder sb = new StringBuilder();
		int lines = random.nextInt(MAX_LINES) + 1;
		boolean first = true;
		for (int i = 0; i < lines; i ++) {
			if (first) {
				first = false;
			} else {
				sb.append(System.lineSeparator());
			}
			sb.append(generateTestStringLine(index));
		}
		return sb.toString();
	}

	private static String generateTestStringLine(int index) {
		StringBuilder sb = new StringBuilder();
		int len = 0;
		while (len <= MIN_LEN) {
			len = random.nextInt(MAX_LEN) + 1;
		}
		while (sb.length() < len) {
			sb.append(STRING_BASE.charAt(index));
		}
		return sb.toString();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_flow_layoutmanager, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.scroll_to_middle) {
			recyclerView.scrollToPosition(TEST_ITEM_SIZE / 2);
		} else if (id == R.id.smooth_scroll_to_middle) {
			recyclerView.smoothScrollToPosition(TEST_ITEM_SIZE / 2);
		}
		return super.onOptionsItemSelected(item);
	}
}
