package org.xiaofeng.playground;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class AnimatedGradientBackgroundActivity extends AppCompatActivity {

//	AnimatedTextGradientView animatedView;
	AnimatedShaderTextView animatedShaderTextView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_animated_gradient_background);
		animatedShaderTextView = (AnimatedShaderTextView)findViewById(R.id.animated_shader_text);
		animatedShaderTextView.setText("This is test");
		animatedShaderTextView.setTextSize(60);
		animatedShaderTextView.setTextColor(Color.BLACK);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			animatedShaderTextView.animateShader();
		}
	}
}
