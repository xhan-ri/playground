package org.xiaofeng.playground;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;

/**
 * View that have 2 focus point, one move at top from left to right, one at bottom move from right to
 * left, the color during movement is defined in topColors & bottomColors. When points hit the end, it
 * will reverse to beginning and start over, so is the colors.
 */

public class AnimatedGradientView extends View {
	private @ColorInt int[] topColors;
	private @ColorInt int[] bottomColors;
	private int viewWidth = 0, viewHeight = 0;
	private long duration = 0;
	private boolean animatorCreated = false;
	private ValueAnimator topColorAnimator, bottomColorAnimator, locationAnimator;
	private AnimatorSet animatorSet;
	LinearGradient linearGradient;
	Paint paint, dotPaint;
	int dotRadius = 5;
	private boolean avoidSqueeze = false;
	private boolean showDots = false;
	private ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			@ColorInt int topColor = (int)topColorAnimator.getAnimatedValue();

			@ColorInt int bottomColor = bottomColorAnimator.isRunning() ? (int)bottomColorAnimator.getAnimatedValue() : bottomColors[0];
			int location = (int)locationAnimator.getAnimatedValue();
			linearGradient = new LinearGradient(location, 0, viewWidth - location, viewHeight, topColor, bottomColor, Shader.TileMode.CLAMP);
			paint.setShader(linearGradient);
			invalidate();
		}
	};
	public AnimatedGradientView(Context context) {
		super(context);
	}

	public AnimatedGradientView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AnimatedGradientView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public AnimatedGradientView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public AnimatedGradientView withTopColors(@ColorInt int... topColors) {
		this.topColors = topColors;

		return this;
	}

	public AnimatedGradientView withBottomColors(@ColorInt int... bottomColors) {
		this.bottomColors = bottomColors;
		return this;
	}

	public AnimatedGradientView duration(long duration) {
		this.duration = duration;
		return this;
	}

	public AnimatedGradientView avoidSqueeze() {
		this.avoidSqueeze = true;
		return this;
	}

	public AnimatedGradientView showDots() {
		this.showDots = true;
		return this;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (!animatorCreated) {
			return;
		}
		canvas.save();
		canvas.drawRect(0, 0, viewWidth, viewHeight, paint);
		if (showDots) {
			int location = (int)locationAnimator.getAnimatedValue();
			canvas.drawCircle(location, 0, dotRadius, dotPaint);
			canvas.drawCircle(viewWidth - location - dotRadius, viewHeight, dotRadius, dotPaint);
			canvas.drawLine(location, 0, viewWidth - location - dotRadius, viewHeight, dotPaint);
		}
		canvas.restore();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		viewWidth = w;
		viewHeight = h;
		initAnimator();
	}

	private void initAnimator() {
		if (animatorCreated) {
			topColorAnimator.cancel();
			bottomColorAnimator.cancel();
			locationAnimator.cancel();
			animatorCreated = false;
		}
		ArgbEvaluator colorEvaluator = new ArgbEvaluator();
		topColorAnimator = new ValueAnimator();
		topColorAnimator.setIntValues(topColors);
		topColorAnimator.setEvaluator(colorEvaluator);
		topColorAnimator.setDuration(duration);
		topColorAnimator.setRepeatCount(ValueAnimator.INFINITE);
		topColorAnimator.setRepeatMode(ValueAnimator.REVERSE);

		// bottom colors will delay the start when top first color changed half way to second color.
		bottomColorAnimator = new ValueAnimator();
		bottomColorAnimator.setIntValues(bottomColors);
		bottomColorAnimator.setEvaluator(colorEvaluator);
		long delay = duration / topColors.length / 2;
		bottomColorAnimator.setDuration(duration - delay);
		bottomColorAnimator.setRepeatCount(ValueAnimator.INFINITE);
		bottomColorAnimator.setStartDelay(delay);

		bottomColorAnimator.setRepeatMode(ValueAnimator.REVERSE);


		locationAnimator = new ValueAnimator();
		locationAnimator.setIntValues(0, avoidSqueeze ? viewWidth / 3 : viewWidth);
		locationAnimator.setDuration(duration);
		locationAnimator.setRepeatCount(ValueAnimator.INFINITE);
		locationAnimator.setRepeatMode(ValueAnimator.REVERSE);
		linearGradient = new LinearGradient(0, 0, viewWidth, viewHeight, topColors[0], bottomColors[0], Shader.TileMode.CLAMP);

		topColorAnimator.addUpdateListener(animatorUpdateListener);
		bottomColorAnimator.addUpdateListener(animatorUpdateListener);
		locationAnimator.addUpdateListener(animatorUpdateListener);

		animatorSet = new AnimatorSet();
		animatorSet.playTogether(topColorAnimator, bottomColorAnimator, locationAnimator);
		animatorSet.setDuration(duration);


		paint = new Paint();
		paint.setColor(topColors[0]);
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);
		paint.setShader(linearGradient);

		if (showDots) {
			dotPaint = new Paint();
			dotPaint.setColor(Color.WHITE);
			dotPaint.setAntiAlias(true);
			dotPaint.setStrokeWidth(10);
			dotPaint.setStyle((Paint.Style.STROKE));
		}
		animatorCreated = true;
		animatorSet.start();
	}
}
