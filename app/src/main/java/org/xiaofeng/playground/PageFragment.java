package org.xiaofeng.playground;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class PageFragment extends Fragment {
	private static final String EXTRA_START = "start";
	private static final String EXTRA_STEP = "step";

	private int start, step;
	private RecyclerView recyclerView;
	public static PageFragment newInstance(int start, int step) {
		PageFragment fragment = new PageFragment();
		Bundle arguments = new Bundle();
		arguments.putInt(EXTRA_START, start);
		arguments.putInt(EXTRA_STEP, step);
		fragment.setArguments(arguments);
		return fragment;
	}

	public PageFragment() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init(savedInstanceState != null ? savedInstanceState : getArguments());
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(EXTRA_START, start);
		outState.putInt(EXTRA_STEP, step);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_page, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		recyclerView = (RecyclerView)view.findViewById(R.id.list);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		recyclerView.setAdapter(new PageAdapter(start, step));
	}

	private void init(Bundle params) {
		if (params == null) {
			return;
		}
		start = params.getInt(EXTRA_START, 0);
		step = params.getInt(EXTRA_STEP, 1);
	}
}
