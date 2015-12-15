package org.xiaofeng.playground;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AnimationSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;

public class LayoutAnimationActivity extends AppCompatActivity {

	View lastView;
	LinearLayout rootContainer;
	int index = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_layout_animation);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		rootContainer = (LinearLayout)findViewById(R.id.root_container);
//		rootContainer.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				String text = "test view " + (index ++);
//				TextView textView = new TextView(LayoutAnimationActivity.this);
//				textView.setId(View.generateViewId());
//				textView.setText(text);
//				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//				layoutParams.setMargins(10, 10, 10, 10);
//				rootContainer.addView(textView);
//				lastView = textView;
//			}
//		});

		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String text = "test view " + (index++);
				TextView textView = new TextView(LayoutAnimationActivity.this);
				textView.setId(View.generateViewId());
				textView.setText(text);
				textView.setAlpha(0f);
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				layoutParams.setMargins(10, 10, 10, 10);
				rootContainer.addView(textView);
				lastView = textView;
			}
		});
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		rootContainer.getLayoutTransition().setAnimateParentHierarchy(false);
		ObjectAnimator appearingAnimator1 = new ObjectAnimator();
		appearingAnimator1.setPropertyName("x");
		appearingAnimator1.setFloatValues(rootContainer.getPaddingLeft(), 200);
		appearingAnimator1.setDuration(1000);

		ObjectAnimator appearingAnimator2 = new ObjectAnimator();
		appearingAnimator2.setPropertyName("x");
		appearingAnimator2.setFloatValues(200, rootContainer.getPaddingLeft());
		appearingAnimator2.setDuration(1000);

		ObjectAnimator appearingAnimator3 = new ObjectAnimator();
		appearingAnimator3.setPropertyName("alpha");
		appearingAnimator3.setFloatValues(0f, 1f);
		appearingAnimator3.setDuration(4000);

		AnimatorSet set = new AnimatorSet();
		set.playTogether(appearingAnimator1, appearingAnimator3);
		set.play(appearingAnimator2).after(appearingAnimator1);

		rootContainer.getLayoutTransition().setAnimator(LayoutTransition.APPEARING, set);
	}

}
