package com.ver1.avacha;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class MyChatDrawingPanel extends SurfaceView implements SurfaceHolder.Callback, Runnable {
	private Thread thread;
	public Bitmap bmp_my_hair;
	private Bitmap bmp_my_face;
	private Bitmap bmp_my_body;
	public Context sub_context;
	// For resizing
	private Bitmap re_bmp_my_hair;
	private Bitmap re_bmp_my_face;
	private Bitmap re_bmp_my_body;
	int int_scale;
	private float mGestureThreshold;

	public MyChatDrawingPanel(Context context) {
		super(context);
		// passing context
		sub_context = context;
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		holder.setFixedSize(getWidth(), getHeight());
		setFocusable(true);
	}

	public MyChatDrawingPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		// passing context
		sub_context = context;
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		holder.setFixedSize(getWidth(), getHeight());
		setFocusable(true);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
		thread = new Thread(this);
		thread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		thread = null;
	}

	// public void run() {
	// while (thread != null) {
	// Canvas canvas = getHolder().lockCanvas();
	// if (canvas != null) {
	// draw(canvas);
	// getHolder().unlockCanvasAndPost(canvas);
	// }
	// try {
	// Thread.sleep(200);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	public void run() {
		while (thread != null) {
			Canvas canvas = getHolder().lockCanvas();
			if (canvas != null) {
				// something to do
				if (true) {
					draw(canvas);
					getHolder().unlockCanvasAndPost(canvas);
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		Log.d("draw", "drawing");

		try {
			InputStream change_ims_hair = sub_context.getAssets().open(RandomChatActivity.getMyHairImg());
			InputStream change_ims_face = sub_context.getAssets().open(RandomChatActivity.getMyFaceImg());
			InputStream change_ims_body = sub_context.getAssets().open(RandomChatActivity.getMyBodyImg());

			bmp_my_hair = BitmapFactory.decodeStream(change_ims_hair);
			bmp_my_face = BitmapFactory.decodeStream(change_ims_face);
			bmp_my_body = BitmapFactory.decodeStream(change_ims_body);

			// Resize Bitmap
			final float GESTURE_THRESHOLD_DIP = 120.0f;
			final float scale = getResources().getDisplayMetrics().density;
			mGestureThreshold = (GESTURE_THRESHOLD_DIP * scale + 0.5f);

			int hair_width = (int) ((mGestureThreshold / 180) * bmp_my_hair.getWidth());
			int hair_height = (int) ((mGestureThreshold / 180) * bmp_my_hair.getHeight());
			int face_width = (int) ((mGestureThreshold / 180) * bmp_my_face.getWidth());
			int face_height = (int) ((mGestureThreshold / 180) * bmp_my_face.getHeight());
			int body_width = (int) ((mGestureThreshold / 180) * bmp_my_body.getWidth());
			int body_height = (int) ((mGestureThreshold / 180) * bmp_my_body.getHeight());

			re_bmp_my_hair = Bitmap.createScaledBitmap(bmp_my_hair, hair_width, hair_height, false);
			re_bmp_my_face = Bitmap.createScaledBitmap(bmp_my_face, face_width, face_height, false);
			re_bmp_my_body = Bitmap.createScaledBitmap(bmp_my_body, body_width, body_height, false);

		} catch (IOException ex) {
			return;
		}
		// DressUpActivity.setSurfaceDestroy();
		Log.d("getSurfaceDestroy", "in If Loop");

		if (canvas != null) {
			// Clear Canvas
			canvas.drawColor(0, PorterDuff.Mode.CLEAR);

			// Draw Bitmap
			Matrix matrix = new Matrix();
			matrix.setScale(-1.0f, 1.0f, 0, 0);
			matrix.postTranslate((int) ((mGestureThreshold / 180) * 150), (int) ((mGestureThreshold / 180) * 120));
			canvas.drawBitmap(re_bmp_my_body, matrix, null);
			matrix.setScale(-1.0f, 1.0f, 0, 0);
			matrix.postTranslate((int) ((mGestureThreshold / 180) * 140), (int) ((mGestureThreshold / 180) * 6));
			canvas.drawBitmap(re_bmp_my_face, matrix, null);
			matrix.setScale(-1.0f, 1.0f, 0, 0);
			matrix.postTranslate((int) ((mGestureThreshold / 180) * 140), (int) ((mGestureThreshold / 180) * 2));
			canvas.drawBitmap(re_bmp_my_hair, matrix, null);
		}
		//
		// canvas.drawBitmap(bmp_my_body, 130, 123, null);
		// canvas.drawBitmap(bmp_my_face, 37, 6, null);
		// canvas.drawBitmap(bmp_my_hair, 54, 2, null);
		//
		// }
		//
		// // canvas.drawBitmap(bmp_my_body, 30, 90, null);
		// // canvas.drawBitmap(bmp_my_face, 80, 40, null);
		// // canvas.drawBitmap(bmp_my_hair, 73, 15, null);
		//
		// // Matrix matrix = new Matrix();
		// // matrix.setScale(-1.0f, 1.0f, 0, 0);
		// // matrix.postTranslate(150, 90);
		// // canvas.drawBitmap(bmp_my_body, matrix, null);
		// // matrix.setScale(-1.0f, 1.0f, 0, 0);
		// // matrix.postTranslate(100, 40);
		// // canvas.drawBitmap(bmp_my_face, matrix, null);
		// // matrix.setScale(-1.0f, 1.0f, 0, 0);
		// // matrix.postTranslate(110, 15);
		// // canvas.drawBitmap(bmp_my_hair, matrix, null);

	}
}