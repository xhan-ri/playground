package org.xiaofeng.playground;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

public class AnimationEndActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
		getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
		getWindow().setSharedElementEnterTransition(new MoveRotateTransition("animated_image", "animated_image2"));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_animation_end);
//		getWindow().setSharedElementsUseOverlay(true);

//		Animator rotate720 = AnimatorInflater.loadAnimator(this, R.animator.rotate_720);
//		rotate720.setTarget(findViewById(R.id.content_image));
//		rotate720.start();
//
//		Animator rotate90 = AnimatorInflater.loadAnimator(this, R.animator.rotate_90);
//		rotate90.setTarget(findViewById(R.id.content_image2));
//		rotate90.start();
	}
}
