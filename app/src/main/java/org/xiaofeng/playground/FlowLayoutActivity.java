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
	private static final int TEST_ITEM_SIZE = 200;
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
		public void onBindViewHolder(FlowItemViewHolder holder, final int position) {
			holder.itemText.setText(items.get(position));
			holder.itemText.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String itemText = items.get(position);
					Toast.makeText(FlowLayoutActivity.this, itemText, Toast.LENGTH_SHORT).show();
				}
			});
		}

		@Override
		public int getItemCount() {
			return items.size();
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
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
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
				outRect.left = 5;
				outRect.top = 5;
				outRect.right = 5;
				outRect.bottom = 5;
			}
		});
		recyclerView.setLayoutManager(
				new FlowLayoutManager());
//				new LinearLayoutManager(this));
	}

	private static List<String> generateTestStrings() {
		ArrayList<String> strings = new ArrayList<>(TEST_ITEM_SIZE);
		for (int i = 0; i < TEST_ITEM_SIZE; i ++) {
			strings.add(generateTestString());
		}
		return strings;
	}

	private static String generateTestString() {
		StringBuilder sb = new StringBuilder();
		int lines = random.nextInt(MAX_LINES) + 1;
		boolean first = true;
		for (int i = 0; i < lines; i ++) {
			if (first) {
				first = false;
			} else {
				sb.append(System.lineSeparator());
			}
			sb.append(generateTestStringLine());
		}
		return sb.toString();
	}

	private static String generateTestStringLine() {
		StringBuilder sb = new StringBuilder();
		int len = 0;
		while (len <= MIN_LEN) {
			len = random.nextInt(MAX_LEN) + 1;
		}
		while (sb.length() < len) {
			int index = random.nextInt(STRING_BASE_LEN);
			sb.append(STRING_BASE.charAt(index));
		}
		return sb.toString();
	}

}
