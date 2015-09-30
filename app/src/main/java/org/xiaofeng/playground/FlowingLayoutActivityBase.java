package org.xiaofeng.playground;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by xhan on 9/3/15.
 */
public class FlowingLayoutActivityBase extends AppCompatActivity {
	protected static final String STRING_BASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	protected static final int TAG_COUNT = 20;

	protected List<String> generateTags() {
		Random random = new Random();
		List<String> tags = new ArrayList<>(TAG_COUNT);
		for (int i = 0; i < TAG_COUNT; i ++) {
			int len = random.nextInt(10) + 1;
			StringBuilder sb = new StringBuilder(len + 1).append("#");
			for (int j = 0; j < len; j++) {
				sb.append(STRING_BASE.charAt(random.nextInt(STRING_BASE.length())));
			}
			tags.add(sb.toString());
		}
		return tags;
	}

	@NonNull
	protected TextView createTextView() {
		TextView textView = new TextView(this);
		ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(15, 15, 15, 15);
		textView.setLayoutParams(layoutParams);
		textView.setGravity(Gravity.CENTER);
		textView.setPadding(15, 15, 15, 15);
		textView.setTextColor(getResources().getColor(android.R.color.white));
		textView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
		return textView;
	}
}
