package org.xiaofeng.playground;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class AnimationEndActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_animation_end);
		Animator rotate720 = AnimatorInflater.loadAnimator(this, R.animator.rotate_720);
		rotate720.setTarget(findViewById(R.id.content_image));
		rotate720.start();

		Animator rotate90 = AnimatorInflater.loadAnimator(this, R.animator.rotate_90);
		rotate90.setTarget(findViewById(R.id.content_image2));
		rotate90.start();
	}
}
