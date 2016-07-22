package org.xiaofeng.playground;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ImageCaptureAndCropActivity extends AppCompatActivity {

	static final int REQUEST_CAMERA_PERMISSION = 100;
	static final int REQUEST_CAPTURE_PICTURE = 101;
	static final int REQUEST_CROP_PICTURE = 102;
	Uri imageUri;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_capture_and_crop);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
			}
		});
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_CAMERA_PERMISSION) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				imageUri = capturePicture();
			}
		}
	}

	private Uri capturePicture() {
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File tmpFile = null;
		try {
			tmpFile = File.createTempFile(
					"Img",  /* prefix */
					".jpg",         /* suffix */
					getExternalFilesDir(Environment.DIRECTORY_PICTURES)      /* directory */
			);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Uri fileUri = Uri.fromFile(tmpFile);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		if (cameraIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(cameraIntent, REQUEST_CAPTURE_PICTURE);
		} else {
			Snackbar.make(findViewById(android.R.id.content), "No camera", Snackbar.LENGTH_SHORT).show();
		}
		return fileUri;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CAPTURE_PICTURE) {
			if (resultCode == Activity.RESULT_OK) {
				if (imageUri != null) {
					displayImage(imageUri);
					cropImage(imageUri);
				} else {
					Snackbar.make(findViewById(android.R.id.content), "Capture not found", Snackbar.LENGTH_SHORT).show();
				}
			}
		} else if (requestCode == REQUEST_CROP_PICTURE) {
			Uri croppedImage = data.getData();
			displayImage(croppedImage);
		}
	}

	private void displayImage(Uri imageLocation) {
		InputStream imageStream = null;
		try {
			imageStream = getContentResolver().openInputStream(imageLocation);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		final Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
		if (bitmap != null) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ImageView imageView = (ImageView)findViewById(R.id.image_result);
					imageView.setImageBitmap(bitmap);
					imageView.postInvalidate();
				}
			});

		}
	}

	private void cropImage(Uri capturedImage) {
		Intent cropIntent = new Intent("com.android.camera.action.CROP");
		cropIntent.setType("image/*");
		cropIntent.setData(capturedImage);
		cropIntent.putExtra("crop", "true");
		cropIntent.putExtra("aspectX", 1);
		cropIntent.putExtra("aspectY", 1);
		cropIntent.putExtra("outputX", 256);
		cropIntent.putExtra("outputY", 256);
		cropIntent.putExtra("return-data", true);

		if (cropIntent.resolveActivity(getPackageManager()) == null) {
			Snackbar.make(findViewById(android.R.id.content), "No crop support", Snackbar.LENGTH_SHORT).show();
			return;
		}
		startActivityForResult(cropIntent, REQUEST_CROP_PICTURE);
	}
}
