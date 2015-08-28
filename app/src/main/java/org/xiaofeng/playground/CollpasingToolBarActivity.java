package org.xiaofeng.playground;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.List;

public class CollpasingToolBarActivity extends AppCompatActivity {

	public static class ViewPagerAdapter extends FragmentStatePagerAdapter {
		private static final List<Pair<Integer, Integer>> startStepList = new LinkedList<Pair<Integer, Integer>>() {{
			add(new Pair<Integer, Integer>(7, 5));
			add(new Pair<Integer, Integer>(11, 3));
			add(new Pair<Integer, Integer>(6, 4));
		}};

		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Pair<Integer, Integer> startStep = startStepList.get(position);
			return PageFragment.newInstance(startStep.first, startStep.second);
		}

		@Override
		public int getCount() {
			return startStepList.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Pair<Integer, Integer> startStep = startStepList.get(position);
			return "start = " + startStep.first + ", step = " + startStep.second;
		}
	}

	Toolbar toolbar;
	ViewPager viewPager;
	TabLayout tabLayout;
//	RecyclerView recyclerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collpasing_tool_bar);
		toolbar = (Toolbar)findViewById(R.id.toolbar);
		View toolbarView = LayoutInflater.from(this).inflate(R.layout.toolbar, null);
		toolbar.addView(toolbarView);
		toolbar.setBackgroundColor(getResources().getColor(android.R.color.white));
		setSupportActionBar(toolbar);

		viewPager = (ViewPager)findViewById(R.id.pager);
		viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
		tabLayout = (TabLayout)findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(viewPager);
	}
}
