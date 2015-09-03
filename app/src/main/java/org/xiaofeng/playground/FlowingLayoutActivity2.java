package org.xiaofeng.playground;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.wefika.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlowingLayoutActivity2 extends FlowingLayoutActivityBase {

	private static final String STRING_BASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final int TAG_COUNT = 100;
	FlowLayout flowLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flowing_layout2);
		flowLayout = (FlowLayout)findViewById(R.id.flow_layout);
		List<String> tags = generateTags();
		for (String tag : tags) {
			TextView textView = createTextView();
			FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(textView.getLayoutParams());
			layoutParams.rightMargin = 20;
			layoutParams.bottomMargin = 20;
			textView.setLayoutParams(layoutParams);
			textView.setText(tag);
			flowLayout.addView(textView);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_flowing_layout_activity2, menu);
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
