package com.ver1.avacha;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class RightDefaultDrawingPanel extends SurfaceView implements SurfaceHolder.Callback, Runnable {
	private Thread thread;
	public Bitmap bmp_my_hair;
	private Bitmap bmp_my_face;
	private Bitmap bmp_my_body;
	public Context sub_context;
	//For resizing
	private Bitmap re_bmp_my_hair;
	private Bitmap re_bmp_my_face;
	private Bitmap re_bmp_my_body;
	int int_scale;
	private float mGestureThreshold;
	

	public RightDefaultDrawingPanel(Context context) {
		super(context);
		// passing context
		sub_context = context;

		SurfaceHolder holder = getHolder();

		holder.addCallback(this);
		holder.setFixedSize(getWidth(), getHeight());

		setFocusable(true);

	}

	public RightDefaultDrawingPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		// passing context
		sub_context = context;

		SurfaceHolder holder = getHolder();

		holder.addCallback(this);
		holder.setFixedSize(getWidth(), getHeight());

		//TODO get attribute as strings
		//position = attrs.getAttributeValue(1);
		
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

	public void run() {
		while (thread != null) {
			Canvas canvas = getHolder().lockCanvas();
			if (canvas != null) {
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

	@Override
	public void draw(Canvas canvas) {
		Log.d("draw", "drawing");

		try {
			InputStream change_ims_hair = sub_context.getAssets().open("1208001203_55.png");
			InputStream change_ims_face = sub_context.getAssets().open("1208003612_53.png");
			InputStream change_ims_body = sub_context.getAssets().open("1210001605.png");

			bmp_my_hair = BitmapFactory.decodeStream(change_ims_hair);
			bmp_my_face = BitmapFactory.decodeStream(change_ims_face);
			bmp_my_body = BitmapFactory.decodeStream(change_ims_body);
			
			//Resize Bitmap 
			final float GESTURE_THRESHOLD_DIP = 120.0f;
			final float scale = getResources().getDisplayMetrics().density;	
			mGestureThreshold = (GESTURE_THRESHOLD_DIP * scale + 0.5f);	
			//int_scale = mGestureThreshold / 180;
			
			int hair_width = (int)((mGestureThreshold / 180) * bmp_my_hair.getWidth());
			int hair_height = (int)((mGestureThreshold / 180) * bmp_my_hair.getHeight());
			int face_width = (int)((mGestureThreshold / 180) * bmp_my_face.getWidth());
			int face_height = (int)((mGestureThreshold / 180) * bmp_my_face.getHeight());
			int body_width = (int)((mGestureThreshold / 180) * bmp_my_body.getWidth());
			int body_height = (int)((mGestureThreshold / 180) * bmp_my_body.getHeight());
			
			re_bmp_my_hair = Bitmap.createScaledBitmap(bmp_my_hair, hair_width, hair_height, false);
			re_bmp_my_face = Bitmap.createScaledBitmap(bmp_my_face,face_width, face_height, false);
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
			canvas.drawBitmap(re_bmp_my_body, (int)((mGestureThreshold / 180)) * 10, (int)((mGestureThreshold / 180) * 120), null);
			canvas.drawBitmap(re_bmp_my_face, (int)((mGestureThreshold / 180)) * 17, (int)((mGestureThreshold / 180) * 6), null);
			canvas.drawBitmap(re_bmp_my_hair, (int)((mGestureThreshold / 180)) * 17, (int)((mGestureThreshold / 180) * 2), null);
			
			
			
//			canvas.drawBitmap(bmp_my_body, 10, 90, null);
//			canvas.drawBitmap(bmp_my_face, 60, 40, null);
//			canvas.drawBitmap(bmp_my_hair, 53, 15, null);
		}
	}
	
	
	
}