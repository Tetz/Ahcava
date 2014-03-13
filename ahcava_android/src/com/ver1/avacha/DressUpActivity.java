package com.ver1.avacha;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;

public class DressUpActivity extends Activity {

	private static String my_hair_img = "1208002510_55.png";
	private static String my_face_img = "1209000712_53.png";
	private static String my_body_img = "1209010401.png";
	private static boolean my_surface_destroy = false;
	private SQLiteDatabase db;
	private Cursor cursor;
	private Cursor cursor_sex;
	public static String sex = "";

	public DrawingPanel my_surface;

	private int my_hair_cnt = 0;
	private int my_face_cnt = 0;
	private int my_body_cnt = 0;

	public static String getMyHairImg() {
		return my_hair_img;
	}

	public static String getMyFaceImg() {
		return my_face_img;
	}

	public static String getMyBodyImg() {
		return my_body_img;
	}

	public static boolean getSurfaceDestroy() {
		return my_surface_destroy;
	}

	public static void setSurfaceDestroy() {
		my_surface_destroy = false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_dress_up);

		// Prepare SQLite
		DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
		db = helper.getWritableDatabase();

		// TODO
		// get my avatar from database
		cursor_sex = DatabaseHelper.select_sex(db);
		if (cursor_sex.moveToFirst()) {
			do {
				sex = cursor_sex.getString(cursor_sex.getColumnIndex("sex"));
			} while (cursor_sex.moveToNext());
		}
		cursor_sex.close();

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
		final Button btn_change_hair = (Button) findViewById(R.id.btn_change_hair);
		final Button btn_change_face = (Button) findViewById(R.id.btn_change_face);
		final Button btn_change_body = (Button) findViewById(R.id.btn_change_body);
		final Button btn_submit = (Button) findViewById(R.id.btn_submit);

		btn_change_hair.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				// Increment Count
				my_hair_cnt++;
				Log.d("my_hair_cnt", Integer.toString(my_hair_cnt));

				if (sex.equals("man")) {// Male
					// Reset Count
					if (my_hair_cnt > 10) {
						my_hair_cnt = 0;
					}
					switch (my_hair_cnt) {
					case 1:
						my_hair_img = "1208002811_55.png";
						break;
					case 2:
						my_hair_img = "1208002902_55.png";
						break;
					case 3:
						my_hair_img = "1208002403_55.png";
						break;
					case 4:
						my_hair_img = "1208002510_55.png";
						break;
					case 5:
						my_hair_img = "1208002710_55.png";
						break;
					case 6:
						my_hair_img = "1208002609_55.png";
						break;
					case 7:
						my_hair_img = "1208002606_55.png";
						break;
					case 8:
						my_hair_img = "1208002202_55.png";
						break;
					case 9:
						my_hair_img = "1208002502_55.png";
						break;
					case 10:
						my_hair_img = "1208002305_55.png";
						break;

					default:
						my_hair_img = "1208002510_55.png";
						break;
					}
				} else if (sex.equals("woman")) { // Female
					// Reset Count
					if (my_hair_cnt > 10) {
						my_hair_cnt = 0;
					}
					switch (my_hair_cnt) {
					case 1:
						my_hair_img = "1208001703_55.png";
						break;
					case 2:
						my_hair_img = "1208000900_55.png";
						break;
					case 3:
						my_hair_img = "1208001003_55.png";
						break;
					case 4:
						my_hair_img = "1208001101_55.png";
						break;
					case 5:
						my_hair_img = "1208001303_55.png";
						break;
					case 6:
						my_hair_img = "1208001610_55.png";
						break;
					case 7:
						my_hair_img = "1208001603_55.png";
						break;
					case 8:
						my_hair_img = "1208001602_55.png";
						break;
					case 9:
						my_hair_img = "1208002003_55.png";
						break;
					case 10:
						my_hair_img = "1208001908_55.png";
						break;

					default:
						my_hair_img = "1208001301_55.png";
						break;
					}
				}
				Log.d("my_hair_img", my_hair_img);

			}
		});

		btn_change_face.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Reset Count
				if (my_face_cnt > 44) {
					my_face_cnt = 0;
				}
				// Increment Count
				my_face_cnt++;
				Log.d("my_hair_cnt", Integer.toString(my_face_cnt));

				switch (my_face_cnt) {
				case 1:
					my_face_img = "1208003112_49.png";
					break;
				case 2:
					my_face_img = "1208003112_53.png";
					break;
				case 3:
					my_face_img = "1208003212_52.png";
					break;
				case 4:
					my_face_img = "1208003212_53.png";
					break;
				case 5:
					my_face_img = "1208003312_50.png";
					break;
				case 6:
					my_face_img = "1208003312_49.png";
					break;
				case 7:
					my_face_img = "1208003312_51.png";
					break;
				case 8:
					my_face_img = "1208003312_52.png";
					break;
				case 9:
					my_face_img = "1208003312_53.png";
					break;
				case 10:
					my_face_img = "1208003412_49.png";
					break;
				case 11:
					my_face_img = "1208003012_54.png";
					break;
				case 12:
					my_face_img = "1208003012_52.png";
					break;
				case 13:
					my_face_img = "1208003212_49.png";
					break;
				case 14:
					my_face_img = "1208003212_54.png";
					break;
				case 15:
					my_face_img = "1208003312_49.png";
					break;
				case 16:
					my_face_img = "1208003112_54.png";
					break;
				case 17:
					my_face_img = "1208003412_49.png";
					break;
				case 18:
					my_face_img = "1208003212_53.png";
					break;
				case 19:
					my_face_img = "1208003312_51.png";
					break;
				case 20:
					my_face_img = "1208003412_51.png";
					break;
				case 21:
					my_face_img = "1208003412_52.png";
					break;
				case 22:
					my_face_img = "1208003412_50.png";
					break;
				case 23:
					my_face_img = "1208003412_54.png";
					break;
				case 24:
					my_face_img = "1208003512_49.png";
					break;
				case 25:
					my_face_img = "1208003512_49.png";
					break;
				case 26:
					my_face_img = "1208003512_52.png";
					break;
				case 27:
					my_face_img = "1208003512_51.png";
					break;
				case 28:
					my_face_img = "1208003512_54.png";
					break;
				case 29:
					my_face_img = "1208003612_51.png";
					break;
				case 30:
					my_face_img = "1208003612_52.png";
					break;
				case 31:
					my_face_img = "1208003712_49.png";
					break;
				case 32:
					my_face_img = "1208003712_51.png";
					break;
				case 33:
					my_face_img = "1209000512_53.png";
					break;
				case 34:
					my_face_img = "1209000612_50.png";
					break;
				case 35:
					my_face_img = "1209000512_52.png";
					break;
				case 36:
					my_face_img = "1209000612_52.png";
					break;
				case 37:
					my_face_img = "1209000612_49.png";
					break;
				case 38:
					my_face_img = "1209000612_53.png";
					break;
				case 39:
					my_face_img = "1209000712_53.png";
					break;
				case 40:
					my_face_img = "1209000712_49.png";
					break;
				case 41:
					my_face_img = "1209000712_52.png";
					break;
				case 42:
					my_face_img = "1208003312_50.png";
					break;
				case 43:
					my_face_img = "1209000812_50.png";
					break;

				default:
					my_face_img = "1208003312_51.png";
					break;
				}

				Log.d("my_face_img", my_face_img);

			}
		});

		btn_change_body.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				// Increment Count
				my_body_cnt++;
				Log.d("my_body_cnt", Integer.toString(my_body_cnt));
				if (sex.equals("man")) {
					// Reset Count
					if (my_body_cnt > 27) {
						my_body_cnt = 0;
					}
					switch (my_body_cnt) {
					case 1:
						my_body_img = "1209010104.png";
						break;
					case 2:
						my_body_img = "1209010405.png";
						break;
					case 3:
						my_body_img = "1209010406.png";
						break;
					case 4:
						my_body_img = "1209010503.png";
						break;
					case 5:
						my_body_img = "1210000100.png";
						break;
					case 6:
						my_body_img = "1210000101.png";
						break;
					case 7:
						my_body_img = "1210000106.png";
						break;
					case 8:
						my_body_img = "1210000201.png";
						break;
					case 9:
						my_body_img = "1210000206.png";
						break;
					case 10:
						my_body_img = "1210000211.png";
						break;
					case 11:
						my_body_img = "1210000301.png";
						break;
					case 12:
						my_body_img = "1210000303.png";
						break;
					case 13:
						my_body_img = "1210001700.png";
						break;
					case 14:
						my_body_img = "1210001704.png";
						break;
					case 15:
						my_body_img = "1210001706.png";
						break;
					case 16:
						my_body_img = "1210001903.png";
						break;
					case 17:
						my_body_img = "1210001901.png";
						break;
					case 18:
						my_body_img = "1209010500.png";
						break;
					case 19:
						my_body_img = "1210001906.png";
						break;
					case 20:
						my_body_img = "1209010502.png";
						break;
					case 21:
						my_body_img = "1209010103.png";
						break;
					case 22:
						my_body_img = "1209010101.png";
						break;
					case 23:
						my_body_img = "1209010009.png";
						break;
					case 24:
						my_body_img = "1209010004.png";
						break;
					case 25:
						my_body_img = "1209010006.png";
						break;
					case 26:
						my_body_img = "1209009908.png";
						break;
					case 27:
						my_body_img = "1209009906.png";
						break;
					case 28:
						my_body_img = "1209009905.png";
						break;

					default:
						my_body_img = "1209010401.png";
						break;
					}
					// TODO
					Log.d("my_body_img", my_body_img);
				} else if (sex.equals("woman")) {
					// Reset Count
					if (my_body_cnt > 45) {
						my_body_cnt = 0;
					}
					switch (my_body_cnt) {
					case 1:
						my_body_img = "1209010602.png";
						break;
					case 2:
						my_body_img = "1210000906.png";
						break;
					case 3:
						my_body_img = "1210001101.png";
						break;
					case 4:
						my_body_img = "1210001409.png";
						break;
					case 5:
						my_body_img = "1209010700.png";
						break;
					case 6:
						my_body_img = "1209010710.png";
						break;
					case 7:
						my_body_img = "1210001111.png";
						break;
					case 8:
						my_body_img = "1209010701.png";
						break;
					case 9:
						my_body_img = "1210001410.png";
						break;
					case 10:
						my_body_img = "1209007411.png";
						break;
					case 11:
						my_body_img = "1209007507.png";
						break;
					case 12:
						my_body_img = "1209007505.png";
						break;
					case 13:
						my_body_img = "1209010610.png";
						break;
					case 14:
						my_body_img = "1210000511.png";
						break;
					case 15:
						my_body_img = "1210000508.png";
						break;
					case 16:
						my_body_img = "1210000507.png";
						break;
					case 17:
						my_body_img = "1210000703.png";
						break;
					case 18:
						my_body_img = "1210000704.png";
						break;
					case 19:
						my_body_img = "1210000804.png";
						break;
					case 20:
						my_body_img = "1210000806.png";
						break;
					case 21:
						my_body_img = "1210000605.png";
						break;
					case 22:
						my_body_img = "1209010605.png";
						break;
					case 23:
						my_body_img = "1210001211.png";
						break;
					case 24:
						my_body_img = "1210001011.png";
						break;
					case 25:
						my_body_img = "1210001209.png";
						break;
					case 26:
						my_body_img = "1210001009.png";
						break;
					case 27:
						my_body_img = "1210001204.png";
						break;
					case 28:
						my_body_img = "1210001005.png";
						break;
					case 29:
						my_body_img = "1210000700.png";
						break;
					case 30:
						my_body_img = "1209010806.png";
						break;

					case 31:
						my_body_img = "1210001100.png";
						break;

					case 32:
						my_body_img = "1210000904.png";
						break;

					case 33:
						my_body_img = "1210000808.png";
						break;

					case 34:
						my_body_img = "1210000611.png";
						break;

					case 35:
						my_body_img = "1210000606.png";
						break;

					case 36:
						my_body_img = "1209010801.png";
						break;

					case 37:
						my_body_img = "1209010309.png";
						break;

					case 38:
						my_body_img = "1209010301.png";
						break;

					case 39:
						my_body_img = "1209010800.png";
						break;

					case 40:
						my_body_img = "1209010210.png";
						break;

					case 41:
						my_body_img = "1209010303.png";
						break;

					case 42:
						my_body_img = "1209010209.png";
						break;

					case 43:
						my_body_img = "1209010203.png";
						break;

					case 44:
						my_body_img = "1210001408.png";
						break;

					default:
						my_body_img = "1210000910.png";
						break;
					}

				}

			}
		});

		btn_submit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				DatabaseHelper.insert(db, my_hair_img, my_face_img, my_body_img);
				Intent intent = new Intent(DressUpActivity.this, RandomChatActivity.class);
				startActivity(intent);
			}
		});

	}// END of onCreate()

	// onStop()
	@Override
	protected void onStop() {
		super.onStop();
		DatabaseHelper.Close(db);
	}

	// Disable back button
	@Override
	public void onBackPressed() {
	}

}
