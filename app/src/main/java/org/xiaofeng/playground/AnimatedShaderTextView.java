package org.xiaofeng.playground;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by xhan on 7/25/16.
 */

public class AnimatedShaderTextView extends TextView {
	ValueAnimator valueAnimator;
	Matrix matrix;
	public AnimatedShaderTextView(Context context) {
		super(context);
	}

	public AnimatedShaderTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AnimatedShaderTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public AnimatedShaderTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public void animateShader() {
		if (valueAnimator != null) {
			if (valueAnimator.isRunning()) {
				valueAnimator.end();
			}
		}

		float offset = Double.valueOf(getHeight() * 1.5f * Math.sqrt(2d)).floatValue();
		float start = -offset, end = getWidth();
		valueAnimator = new ValueAnimator();
		valueAnimator.setFloatValues(start, end);
		valueAnimator.setRepeatMode( ValueAnimator.RESTART);
		valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
		final @ColorInt int currentColor = getCurrentTextColor();
		final int revertColor = getRevertColor(currentColor);
		matrix = new Matrix();
		matrix.setRotate(45);
		valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float currentValue = (float)valueAnimator.getAnimatedValue();
				Shader textShader = new LinearGradient(currentValue, 0, currentValue + getHeight() * 1.5f, 0, new int[] {currentColor, revertColor, revertColor, currentColor}, new float[] {0f, .4f, .6f, 1f}, Shader.TileMode.CLAMP);
				textShader.setLocalMatrix(matrix);
				getPaint().setShader(textShader);
				invalidate();
			}
		});
		valueAnimator.setDuration(5000);
		valueAnimator.start();
	}

	private static int getRevertColor(@ColorInt int color) {
		return getRevertColor(color, 1);
	}

	private static int getRevertColor(@ColorInt int color, float transparency) {
		return Color.argb(Float.valueOf(Color.alpha(color) * transparency).intValue(), 255 - Color.red(color), 255 - Color.green(color), 255 - Color.blue(color));

	}
}
