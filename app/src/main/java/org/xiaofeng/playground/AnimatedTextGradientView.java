package org.xiaofeng.playground;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * View painted with text with animated gradient background
 */

public class AnimatedTextGradientView extends View {
	private @ColorInt int startColor, midColor, endColor;
	private String displayText;
	private Paint backgroundPaint, textPaint;
	LinearGradient textShader;
	Rect textRect = null;
	Drawable backgroundDrawable = new ColorDrawable(Color.BLUE);
	Paint.FontMetrics textFontMetrics;

	static @ColorInt int[] DEFAULT_GRADIENT = new int[] {Color.RED, Color.GREEN, Color.RED};
	public AnimatedTextGradientView(Context context) {
		super(context);
	}

	public AnimatedTextGradientView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AnimatedTextGradientView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public AnimatedTextGradientView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		backgroundDrawable.draw(canvas);
		if (textRect != null) {
			canvas.drawText(displayText, (getMeasuredWidth() - textRect.width()) / 2, Math.abs(textFontMetrics.ascent), textPaint);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (!TextUtils.isEmpty(displayText) && textPaint != null) {
			measureText();
			setMeasuredDimension(textRect.width() + getPaddingLeft() + getPaddingRight(), Float.valueOf(Math.abs(textFontMetrics.top) + Math.abs(textFontMetrics.bottom) + getPaddingTop() + getPaddingBottom()).intValue());
			textShader = new LinearGradient(0, 0, getMeasuredHeight(), getMeasuredHeight(), new int[] {startColor, midColor, endColor}, new float[] {.2f, .5f, .8f}, Shader.TileMode.CLAMP);
			Matrix localMatrix = new Matrix();
			localMatrix.setRotate(45);
			textShader.setLocalMatrix(localMatrix);
			textPaint.setShader(textShader);
		}
	}

	private void measureText() {
		textRect = new Rect();
		textPaint.getTextBounds(displayText, 0, displayText.length(), textRect);
		textFontMetrics = textPaint.getFontMetrics();
	}

	public AnimatedTextGradientView withText(String text) {
		displayText = text;
		if (textPaint == null) {
			createDefaultPaint();
		}

		if (isAttachedToWindow()) {
			requestLayout();
		}
		return this;
	}

	public AnimatedTextGradientView withTextGradient(@ColorInt int startColor, @ColorInt int midColor, @ColorInt int endColor) {
		this.startColor = startColor;
		this.midColor = midColor;
		this.endColor = endColor;
		this.textPaint = new Paint();
		this.textPaint.setAntiAlias(true);
		this.textPaint.setStyle(Paint.Style.FILL);
		textPaint.setColor(startColor);

		if (isAttachedToWindow()) {
			requestLayout();
		}
		return this;
	}

	public AnimatedTextGradientView withTextSize(int textSize, boolean bold) {
		if (textPaint == null) {
			createDefaultPaint();
		}
		textPaint.setTextSize(textSize);
		textPaint.setTypeface(bold ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
		if (isAttachedToWindow()) {
			requestLayout();
		}
		return this;
	}
	private void createDefaultPaint() {
		withTextGradient(DEFAULT_GRADIENT[0], DEFAULT_GRADIENT[1], DEFAULT_GRADIENT[2]);
		textFontMetrics = textPaint.getFontMetrics();
	}
}
