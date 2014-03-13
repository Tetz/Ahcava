package com.ver1.avacha;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class PaletteActivity extends Activity implements OnClickListener {

	ScrollView scrollView;
	LinearLayout linearLayout;
	private float mGestureThreshold;

	// Decralation for SQLite
	private SQLiteDatabase db;
	private Cursor cursor;
	private Cursor cursor_sex;
	public static String sex = "";
	public int sex_type;

	// Decralation for Switching Image Names
	private static String img = "1208002510_55.png";
	private static Bitmap bmp;
	private static Bitmap bmp_scaled;
	private LinearLayout.LayoutParams lp;

	// Decralation for SurfaceView
	private static String my_hair_img = "1208002510_55.png";
	private static String my_face_img = "1209000712_53.png";
	private static String my_body_img = "1209010401.png";
	public DrawingPanel my_surface;
	public int[] change_parts = { 0, 0, 0 };

	public static String getMyHairImg() {
		return my_hair_img;
	}

	public static String getMyFaceImg() {
		return my_face_img;
	}

	public static String getMyBodyImg() {
		return my_body_img;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_palette);

		// Prepare SQLite
		DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
		db = helper.getWritableDatabase();

		// get my avatar from database
		cursor_sex = DatabaseHelper.select_sex(db);
		if (cursor_sex.moveToFirst()) {
			do {
				sex = cursor_sex.getString(cursor_sex.getColumnIndex("sex"));
			} while (cursor_sex.moveToNext());
		}
		cursor_sex.close();
		Log.d("sex", "PaletteActivity:sex = " + sex);
		// TODO
		// get my avatar from database
		cursor = DatabaseHelper.select_img(db);
		if (cursor.moveToFirst()) {
			do {
				my_hair_img = cursor.getString(cursor.getColumnIndex("my_hair"));
				my_face_img = cursor.getString(cursor.getColumnIndex("my_face"));
				my_body_img = cursor.getString(cursor.getColumnIndex("my_body"));
			} while (cursor.moveToNext());
		}
		cursor.close();
		Log.d("mine", my_hair_img);
		Log.d("mine", my_face_img);
		Log.d("mine", my_body_img);

		// Custom Surface View
		my_surface = (DrawingPanel) findViewById(R.id.msurface);
		my_surface.setZOrderOnTop(true);
		SurfaceHolder my_surface_holder = my_surface.getHolder();
		my_surface_holder.setFormat(PixelFormat.TRANSPARENT);

		// Dress up and Submit
		final Button btn_submit = (Button) findViewById(R.id.btn_submit);
		btn_submit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				DatabaseHelper.insert(db, my_hair_img, my_face_img, my_body_img);
				Intent intent = new Intent(PaletteActivity.this, RandomChatActivity.class);
				startActivity(intent);
			}
		});

		//Resize Bitmap 
		final float GESTURE_THRESHOLD_DIP = 120.0f;
		final float scale = getResources().getDisplayMetrics().density;	
		mGestureThreshold = (GESTURE_THRESHOLD_DIP * scale + 0.5f);	
		
		// The purpose below is to set images dynamically into ScrollView.				
		linearLayout = (LinearLayout) findViewById(R.id.avatar_parts_linear);
		lp = new LinearLayout.LayoutParams((int)((mGestureThreshold / 180) * 200), (int)((mGestureThreshold / 180) * 200));
		lp.setMargins(0, 0, 0, 0);

		// Call Methods
		this.insertHairName();

		// TODO click hair icon
		ImageView hair_icon = (ImageView) findViewById(R.id.hair_icon);
		hair_icon.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				insertHairName();
			}
		});
		// TODO click face icon
		ImageView face_icon = (ImageView) findViewById(R.id.face_icon);
		face_icon.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				insertFaceName();
			}
		});

		// TODO click body icon
		ImageView body_icon = (ImageView) findViewById(R.id.body_icon);
		body_icon.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				insertBodyName();
			}
		});
	}

	/**
	 * The method sets the hair image names into the variable.
	 */
	public void insertHairName() {
		// Reset Views
		linearLayout.removeAllViews();
		ImageName img_name = new ImageName();
		// Male:0 , Female:1
		sex_type = (sex.equals("man")) ? 0 : 1;
		for (int cnt = 0; cnt < img_name.imageNamesHair[sex_type].length; cnt++) {
			ImageView parts_img = new ImageView(this);

			// TODO SET change flag
			change_parts[0] = 1;
			change_parts[1] = 0;
			change_parts[2] = 0;

			// Set Names
			img = img_name.imageNamesHair[sex_type][cnt];
			// Set Listener
			parts_img.setId(cnt);
			parts_img.setOnClickListener(this);

			try {
				InputStream is_hair = getAssets().open(img);
				bmp = BitmapFactory.decodeStream(is_hair);
				bmp_scaled = Bitmap.createScaledBitmap(bmp, (int)((mGestureThreshold / 180) * 190), (int)((mGestureThreshold / 180)  * 250), false);
			} catch (IOException ex) {
				return;
			}

			// Display images on ScrollView
			parts_img.setImageBitmap(bmp_scaled);
			linearLayout.addView(parts_img, lp);

		}
	}

	// TODO Face Method
	/**
	 * The method sets the face image names into the variable.
	 */
	public void insertFaceName() {
		// Reset Views
		linearLayout.removeAllViews();
		ImageName img_name = new ImageName();
		// Male and Female use the same.
		int sex_type = 0;
		for (int cnt = 0; cnt < img_name.imageNamesFace[sex_type].length; cnt++) {
			ImageView parts_img = new ImageView(this);

			// TODO SET change flag
			change_parts[0] = 0;
			change_parts[1] = 1;
			change_parts[2] = 0;

			// Set Names
			img = img_name.imageNamesFace[sex_type][cnt];

			// Set Listener
			parts_img.setId(cnt);
			parts_img.setOnClickListener(this);

			try {
				InputStream is_hair = getAssets().open(img);
				bmp = BitmapFactory.decodeStream(is_hair);
				bmp_scaled = Bitmap.createScaledBitmap(bmp, (int)((mGestureThreshold / 180)  * 190), (int)((mGestureThreshold / 180)  * 250), false);
			} catch (IOException ex) {
				return;
			}

			// Display images on ScrollView
			parts_img.setImageBitmap(bmp_scaled);
			linearLayout.addView(parts_img, lp);

		}

	}

	// TODO Body Method
	/**
	 * The method sets the body image names into the variable.
	 */
	public void insertBodyName() {
		// Reset Views
		linearLayout.removeAllViews();
		ImageName img_name = new ImageName();
		// Male:0 , Female:1
		int sex_type = (sex.equals("man")) ? 0 : 1;
		for (int cnt = 0; cnt < img_name.imageNamesBody[sex_type].length; cnt++) {
			ImageView parts_img = new ImageView(this);

			// TODO SET change flag
			change_parts[0] = 0;
			change_parts[1] = 0;
			change_parts[2] = 1;

			// Set Names
			img = img_name.imageNamesBody[sex_type][cnt];

			// Set Listener
			parts_img.setId(cnt);
			parts_img.setOnClickListener(this);

			try {
				InputStream is_hair = getAssets().open(img);
				bmp = BitmapFactory.decodeStream(is_hair);
				bmp_scaled = Bitmap.createScaledBitmap(bmp, (int)((mGestureThreshold / 180)  * 170), (int)((mGestureThreshold / 180)  * 170), false);
			} catch (IOException ex) {
				return;
			}

			// Display images on ScrollView
			// lp.setMargins(10, 40, 40, 10);
			parts_img.setImageBitmap(bmp_scaled);
			linearLayout.addView(parts_img, lp);

		}

	}

	public void onClick(View v) {
		int ID = v.getId();
		Log.d("imgview_id", Integer.toString(ID));
		if (change_parts[0] == 1) {
			my_hair_img = ImageName.imageNamesHair[sex_type][ID];
		}
		if (change_parts[1] == 1) {//Male & Female
			my_face_img = ImageName.imageNamesFace[0][ID];
		}
		if (change_parts[2] == 1) {
			my_body_img = ImageName.imageNamesBody[sex_type][ID];
		}
	}

}
