package org.xiaofeng.playground;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class ImageActivity extends AppCompatActivity {
	private static final String LOG_TAG = "ImageActivity";
	ImageView imageView;
	Bitmap bitmap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		imageView = (ImageView)findViewById(R.id.target_image);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.num_grid, options);
		applyMatrix2(new Matrix());
		setupToolbar();


	}

	private void setupToolbar() {
		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		toolbar.addView(LayoutInflater.from(this).inflate(R.layout.toolbar_image, null));
		toolbar.findViewById(R.id.image_rotate).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Matrix matrix = new Matrix();
				matrix.postRotate(45, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
//				applyMatrix(matrix, false);
				applyMatrix2(matrix);
			}
		});

		toolbar.findViewById(R.id.image_translate).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Matrix matrix = new Matrix();
				matrix.setTranslate(100, 100);
				applyMatrix2(matrix);
			}
		});

		toolbar.findViewById(R.id.image_scale).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Matrix matrix = new Matrix();
				matrix.preScale(2, 2);
//				applyMatrix(matrix, true);
				applyMatrix2(matrix);
			}
		});
	}

	private void applyMatrix(Matrix matrix, boolean filter) {
		Log.i(LOG_TAG, "Original bitmap - width = " + bitmap.getWidth() + ", height = " + bitmap.getHeight());
		Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, filter);
		Log.i(LOG_TAG, "Result bitmap - width = " + result.getWidth() + ", height = " + result.getHeight());
		imageView.setImageBitmap(result);
		bitmap.recycle();
		bitmap = result;
	}

	private void applyMatrix2(Matrix matrix) {
		Bitmap result = Bitmap.createBitmap(getResources().getDisplayMetrics(), bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		Paint paint = new Paint();
		paint.setColor(getResources().getColor(android.R.color.darker_gray));
		canvas.drawBitmap(bitmap, matrix, paint);
		imageView.setImageBitmap(result);
		bitmap.recycle();
		bitmap = result;
	}

}
