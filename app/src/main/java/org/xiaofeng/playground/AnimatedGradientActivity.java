package org.xiaofeng.playground;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AnimatedGradientActivity extends AppCompatActivity {

	private static final int[] topColors = new int[] {
			Color.parseColor("#F8F211FF"),
			Color.parseColor("#F8FFC70C"),
			Color.parseColor("#F839DAF4"),
			Color.parseColor("#F8DF39F4"),
			Color.parseColor("#F8A039F4"),
			Color.parseColor("#F8F4E939"),
			Color.parseColor("#F8DC39F4"),
			Color.parseColor("#F8F211FF")
	};

	private static final int[] bottomColors = new int[] {
			Color.parseColor("#F8FFC70C"),
			Color.parseColor("#F8F211FF"),
			Color.parseColor("#F84242E6"),
			Color.parseColor("#F842E6D4"),
			Color.parseColor("#F8DBE642"),
			Color.parseColor("#F84AE642"),
			Color.parseColor("#F8FFC70C")
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_animated_gradient);
		AnimatedGradientView animatedView = (AnimatedGradientView)findViewById(R.id.animated_view);

		animatedView.withTopColors(topColors).withBottomColors(bottomColors).duration(5000);
	}
}
