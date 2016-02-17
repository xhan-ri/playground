package org.xiaofeng.playground;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;

public class RotateMoveActivity extends AppCompatActivity {
	private static final String LOG_TAG = "RotateMoveActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rotate_move);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				View targetView = findViewById(R.id.target_image);

				ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RotateMoveActivity.this, new Pair[]{new Pair<>(targetView, "animated_image"), new Pair<>(findViewById(R.id.target_image2), "animated_image2")});

				Bundle optionBundle = options.toBundle();
				printBundle(optionBundle);
				startActivity(new Intent(RotateMoveActivity.this, AnimationEndActivity.class), optionBundle);
			}
		});
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		final Animator animator = AnimatorInflater.loadAnimator(RotateMoveActivity.this, R.animator.rotate_move);
		View targetView = findViewById(R.id.target_image);
		animator.setTarget(targetView);
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				animator.start();
			}
		});
	}

	private void printBundle(Bundle bundle) {
		for (String key : bundle.keySet()) {
			Log.i(LOG_TAG, key + "=" + bundle.get(key));
		}
	}

}
