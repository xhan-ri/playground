package org.xiaofeng.playground;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by xhan on 2/17/16.
 */
public class MoveRotateTransition extends Transition {
	private static final String LOG_TAG = "MoveRotateTransition";
	private static final String PROP_ROTATION = "org.xiaofeng.playground:RotateTransition:rotation";
	private static final String PROP_X = "org.xiaofeng.playground:RotateTransition:x";
	private static final String PROP_Y = "org.xiaofeng.playground:RotateTransition:y";
	private List<String> transitionNameList = new LinkedList<>();

	public MoveRotateTransition(String... transitionNames) {
		transitionNameList.addAll(Arrays.asList(transitionNames));
	}

	@Override
	public void captureStartValues(TransitionValues transitionValues) {
		if (TextUtils.isEmpty(transitionValues.view.getTransitionName()) || !transitionNameList.contains(transitionValues.view.getTransitionName())) {
			return;
		}
		Log.i(LOG_TAG, ".");
		Log.i(LOG_TAG, "Capturing start value for view = " + transitionValues.view.getTransitionName());
		Log.i(LOG_TAG, "Before capture: " + dumpMap(transitionValues.values));
		if (transitionValues.view.getTransitionName().equals("animated_image2")) {
			captureValues(transitionValues, -45f);
		} else {
			captureValues(transitionValues, transitionValues.view.getRotation());
		}

	}

	@Override
	public void captureEndValues(TransitionValues transitionValues) {
		if (TextUtils.isEmpty(transitionValues.view.getTransitionName()) || !transitionNameList.contains(transitionValues.view.getTransitionName())) {
			return;
		}
		Log.i(LOG_TAG, ".");
		Log.i(LOG_TAG, "Capturing end value for view = " + transitionValues.view.getTransitionName());
		Log.i(LOG_TAG, "Before capture: " + dumpMap(transitionValues.values));
		if (transitionValues.view.getTransitionName().equals("animated_image2")) {
			captureValues(transitionValues, 45f + 360f);
		} else {
			captureValues(transitionValues, transitionValues.view.getRotation());
		}
	}

	private void captureValues(TransitionValues transitionValues, float rotation) {
		final View view = transitionValues.view;
		transitionValues.values.put(getPropKey(PROP_ROTATION, view), rotation);
		transitionValues.values.put(getPropKey(PROP_X, view), view.getX());
		transitionValues.values.put(getPropKey(PROP_Y, view), view.getY());
		Log.i(LOG_TAG, "Captured values: " + dumpMap(transitionValues.values));
	}

	@Override
	public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
		if (startValues == null || endValues == null) {
			return null;
		}
		Log.i(LOG_TAG, "Creating animator for view = " + startValues.view.getTransitionName());
		final View view = endValues.view;
		final float startRotation = (float)startValues.values.get(getPropKey(PROP_ROTATION, view));
		final float endRotation = (float)endValues.values.get(getPropKey(PROP_ROTATION, view));
		final float startX = (float)startValues.values.get(getPropKey(PROP_X, view));
		final float endX = (float)endValues.values.get(getPropKey(PROP_X, view));
		final float startY = (float)startValues.values.get(getPropKey(PROP_Y, view));
		final float endY = (float)endValues.values.get(getPropKey(PROP_Y, view));
		Log.i(LOG_TAG, "start rotation = " + startRotation + ", endRotation = " + endRotation + ", startX = " + startX + ", endX = " + endX + ", startY = " + startY + ", endY = " + endY);
		// no animation needed.
		if (startRotation == endRotation && startX == endX && startY == endY) {
			Log.w(LOG_TAG, "No animation for view");
			return null;
		}

		ValueAnimator rotateAnimator = new ValueAnimator();
		rotateAnimator.setFloatValues(startRotation, endRotation);
		rotateAnimator.setTarget(view);
		rotateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				view.setRotation((float)animation.getAnimatedValue());
			}
		});

		ValueAnimator xAnimator = new ValueAnimator();
		xAnimator.setFloatValues(startX, endX);
		xAnimator.setTarget(view);
		xAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				view.setX((float)animation.getAnimatedValue());
			}
		});

		ValueAnimator yAnimator = new ValueAnimator();
		yAnimator.setFloatValues(startY, endY);
		yAnimator.setTarget(view);
		yAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				view.setY((float)animation.getAnimatedValue());
			}
		});

		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(rotateAnimator, xAnimator, yAnimator);
		return animatorSet;
	}

	private String getPropKey(String prop, View view) {
		return prop + ":" + view.getTransitionName();
	}

	private static String dumpMap(Map<String, Object> map) {
		StringBuilder sb = new StringBuilder();
		for (String key : map.keySet()) {
			sb.append(key).append("=").append(map.get(key)).append(", ");
		}
		return sb.toString();
	}
}
