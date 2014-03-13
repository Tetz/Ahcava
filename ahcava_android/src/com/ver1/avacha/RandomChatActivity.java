package com.ver1.avacha;

import java.net.URI;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class RandomChatActivity extends FragmentActivity {

	// SQLite
	DatabaseHelper helper;

	// Layout
	ScrollView scrollView;
	ScrollView my_scrollView;
	LinearLayout linearLayout;
	LinearLayout my_linearLayout;
	RelativeLayout main_relative;
	LinearLayout left_dummy_linear;
	Handler guiThreadHandler;
	private LinearLayout.LayoutParams lp;
	private static Context context;
	LinearLayout.LayoutParams left_dummy_param;
	private RelativeLayout.LayoutParams my_scroll_param;
	private RelativeLayout.LayoutParams scroll_param;
	private int cnt_txt = 0;
	private int cnt_txt_2 = 0;
	private int add_height;
	private int max_addview_number;

	// Private Variables Declaration
	private TextView left_text = null;
	private TextView right_text = null;
	private TextView good_count = null;
	private String her_name_txt;
	private String g_cnt;
	private boolean killMe = false;
	private ImageButton her_ok_action;
	private String msg_txt = "";
	private ArrayList<String> r_txt_list;
	private ArrayList<String> l_txt_list;
	private float mGestureThreshold;
	private Button btn;
	private String uuid = "";

	// Preparation For WebSocket
	private WebSocketClient mClient;
	private Handler mHandler;
	private static final String TAG = "WebsocketActivity";
	EditText editxt1;
	TextView txt1;
	
	// TODO Dialog
	private int mStackLevel = 0;

	// Public Variables Declaration
	public String params[] = new String[10];
	public String login_flag = "0";
	public String action = "";
	public String ok_flag = "false";
	public static String sex = "";
	public String from_uuid = "";
	public String change_user = "false";
	public ImageButton ok_action = null;
	public ImageButton imagebutton = null;

	// MySurfaceView
	private static String my_hair_img = "1208002510_55.png";
	private static String my_face_img = "1209000712_53.png";
	private static String my_body_img = "1209010401.png";
	private static String her_hair_img = "1210ef07_73_trimmed.png";
	private static String her_face_img = "1210ef07_73_trimmed.png";
	private static String her_body_img = "1210ef07_73_trimmed.png";

	private static boolean my_surface_destroy = false;
	private SQLiteDatabase db;
	private Cursor cursor_img;
	private Cursor cursor_sex;
	private Cursor cursor_messages;
	private Cursor cursor_my_messages;
	private Cursor cursor_good_count;

	public MyChatDrawingPanel chat_my_surface;
	public HerChatDrawingPanel chat_her_surface;

	// Support Fragment Manager
	private android.support.v4.app.FragmentManager fm;

	// Change and Reset User
	private boolean reset_flag = false;

	public static String getMyHairImg() {
		return my_hair_img;
	}

	public static String getMyFaceImg() {
		return my_face_img;
	}

	public static String getMyBodyImg() {
		return my_body_img;
	}

	public static String getHerHairImg() {
		return her_hair_img;
	}

	public static String getHerFaceImg() {
		return her_face_img;
	}

	public static String getHerBodyImg() {
		return her_body_img;
	}

	public static boolean getSurfaceDestroy() {
		return my_surface_destroy;
	}

	public static void setSurfaceDestroy() {
		my_surface_destroy = false;
	}

	private void resetHerAvatar() {
		her_hair_img = "1210ef07_73_trimmed.png";
		her_face_img = "1210ef07_73_trimmed.png";
		her_body_img = "1210ef07_73_trimmed.png";
	}

	public static float convertDpToPixel(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	public static float convertPixelsToDp(float px, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}

	/**
	 * OK Action
	 * Visible or Invisible
	 */
	private Handler handler = new Handler();
	private Runnable runnable = new Runnable() {
		public void run() {
			// exit
			if (killMe) {
				return;
			}
			// Post data to UI thread
			Log.d("lifecycle", "handler loop test");
			// the runnable starts every 1000ms
			handler.postDelayed(this, 1000);
		}
	};

	private Runnable action_timer = new Runnable() {
		public void run() {
			ok_action.setVisibility(View.INVISIBLE);
		}
	};

	private Runnable her_action_timer = new Runnable() {
		public void run() {
			her_ok_action.setVisibility(View.INVISIBLE);
		}
	};

	/**
	 * onCreate()
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("lifecycle", "onCreate()");
		// get Context
		context = getApplicationContext();
		// Select a layout file
		setContentView(R.layout.activity_random_chat);

		// For UI thread
		guiThreadHandler = new Handler();

		int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
		// Find resources from xml file
		txt1 = (TextView) findViewById(R.id.textView1);
		// Set a default id
		ok_action = (ImageButton) findViewById(R.id.my_ok_action);
		her_ok_action = (ImageButton) findViewById(R.id.her_ok_action);
		left_text = (TextView) findViewById(R.id.her_message);
		right_text = (TextView) findViewById(R.id.my_message);
		good_count = (TextView) findViewById(R.id.good_count);

		// The purpose below is to set messages dynamically into ScrollView.
		scrollView = (ScrollView) findViewById(R.id.right_messages_scroll);
		my_scrollView = (ScrollView) findViewById(R.id.left_messages_scroll);
		linearLayout = (LinearLayout) findViewById(R.id.right_messages_linear);
		my_linearLayout = (LinearLayout) findViewById(R.id.left_messages_linear);
		main_relative = (RelativeLayout) findViewById(R.id.main_relative);
		// Get UUID
		GetSet getset = new GetSet(this);
		uuid = getset.getUuid();
		// Parse
		Parse.initialize(this, "zHaC9bMjdv5IRwpnOn5Y9EAd5iGun5Muv8zMJDgQ", "5YmM7YLRI4QijnlALTfC87a0CG6UMxE2gywgGClX");
		PushService.setDefaultPushCallback(this, SelectAvatarActivity.class);
		ParseInstallation.getCurrentInstallation().saveInBackground();
		ParseAnalytics.trackAppOpened(getIntent());
		// When users indicate they are Giants fans, we subscribe them to that channel.
		PushService.subscribe(this, "user_" + uuid, SelectAvatarActivity.class);

		// Button
		btn = (Button) findViewById(R.id.send);

		// Calculate the px from the dp
		final float GESTURE_THRESHOLD_DIP = 140.0f;
		final float scale = context.getResources().getDisplayMetrics().density;
		mGestureThreshold = (GESTURE_THRESHOLD_DIP * scale + 0.5f);
		lp = new LinearLayout.LayoutParams((int) mGestureThreshold, WC);
		lp.setMargins(0, 10, 0, 0);
		Log.d("pixel", "pixel = " + mGestureThreshold);

		// ScrollView Space
		// Prepare the param
		my_scroll_param = (RelativeLayout.LayoutParams) my_scrollView.getLayoutParams();
		scroll_param = (RelativeLayout.LayoutParams) scrollView.getLayoutParams();

		// Show Good Point
		helper = new DatabaseHelper(getApplicationContext());
		db = helper.getWritableDatabase();
		cursor_good_count = DatabaseHelper.select_good_count(db);
		if (cursor_good_count.moveToFirst()) {
			do {
				g_cnt = cursor_good_count.getString(cursor_good_count.getColumnIndex("good_count"));
			} while (cursor_good_count.moveToNext());
		}
		cursor_good_count.close();
		good_count.setText(g_cnt);
		Log.d("g_cnt", "onCreate: g_cnt = " + g_cnt);

		// set TextView -------------------------------
		// Prepare SQLite
		helper = new DatabaseHelper(getApplicationContext());
		db = helper.getWritableDatabase();
		// Set Messages Into ArrayList
		cursor_my_messages = DatabaseHelper.select_my_messages(db);
		if (cursor_my_messages.moveToFirst()) {
			int i = 0;
			l_txt_list = new ArrayList<String>();
			do {
				String text = cursor_my_messages.getString(cursor_my_messages.getColumnIndex("my_message"));
				l_txt_list.add(text);
				Log.d("watch_cursor", "l_txt_list.get(" + i + ") = " + l_txt_list.get(i));
				i++;
			} while (cursor_my_messages.moveToNext());
		}
		cursor_my_messages.close();
		// Reset Views
		my_linearLayout.removeAllViews();

		// ---Calculate ScrollView Height ------
		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		Display disp = wm.getDefaultDisplay();
		Point size = new Point();
		disp.getSize(size);
		add_height = left_text.getLineHeight() + 10 + 6 + 3 + 3;
		float dp_cal_h = convertPixelsToDp(size.y, context) - (290) - 140; //
		float pixel_h_textview = (float) (add_height);
		float db_h_textview = convertPixelsToDp(pixel_h_textview, context);
		max_addview_number = (int) (dp_cal_h / db_h_textview);
		Log.d("cal_scroll_height", "max_addview_number" + max_addview_number);
		// ---End Calculate ScrollView Height ------

		// Set the height of LinearLayout
		if (l_txt_list != null) {
			if (l_txt_list.size() - 1 < max_addview_number) {
				cnt_txt = l_txt_list.size() - 1;
			} else if (l_txt_list.size() - 1 >= max_addview_number) {
				cnt_txt = max_addview_number;
			}
		}
		my_scroll_param.height = add_height * cnt_txt;
		my_scrollView.setLayoutParams(my_scroll_param);

		if (l_txt_list != null && !l_txt_list.isEmpty()) {
			// Display My Messages Log
			for (int cnt = l_txt_list.size() - 1; cnt > 0; cnt--) {
				TextView ttvw = new TextView(context);
				// Add view to a vertical LinearLayout at bottom (programatically)
				// Set Listener
				ttvw.setId(cnt);
				// Set Messages
				ttvw.setBackgroundResource(R.drawable.textview_border);
				ttvw.setText(l_txt_list.get(cnt));
				// setTextColor For Xperia Z
				ttvw.setTextColor(0xFF000000);
				// Display images on ScrollView
				if (l_txt_list.get(cnt).length() > 0) {
					my_linearLayout.addView(ttvw, lp);
				}
			}
			left_text.setText(l_txt_list.get(0));
		}
		DatabaseHelper.Close(db);
		// END set TextView -----------------------------------------
		// -------------- Show Right TextView After User Back ------------------------------------------------
		// Prepare SQLite
		helper = new DatabaseHelper(getApplicationContext());
		db = helper.getWritableDatabase();

		// New Text
		r_txt_list = new ArrayList<String>();

		// Get Messages From SQLite
		cursor_messages = DatabaseHelper.select_messages(db);
		if (cursor_messages.moveToFirst()) {
			int i = 0;
			String text;
			do {
				text = cursor_messages.getString(cursor_messages.getColumnIndex("message"));
				r_txt_list.add(text);
				Log.d("watch_cursor", "r_txt_list.get(" + i + ") = " + r_txt_list.get(i));
				i++;
			} while (cursor_messages.moveToNext());
		}
		// Close the database & cursor
		cursor_messages.close();
		DatabaseHelper.Close(db);

		// Clear Layout
		linearLayout.removeAllViews();

		cnt_txt = 0;
		// Set the height of LinearLayout
		if (r_txt_list != null) {
			if (r_txt_list.size() - 1 < max_addview_number) {
				cnt_txt = r_txt_list.size() - 1;
			} else if (r_txt_list.size() - 1 >= max_addview_number) {
				cnt_txt = max_addview_number;
			}
		}
		scroll_param.height = add_height * cnt_txt;
		scrollView.setLayoutParams(scroll_param);

		if (r_txt_list != null && !r_txt_list.isEmpty()) {
			for (int cnt = r_txt_list.size() - 1; cnt > 0; cnt--) {
				TextView ttvw = new TextView(context);
				// Set Listener
				ttvw.setId(cnt);
				// Set Messages
				ttvw.setBackgroundResource(R.drawable.textview_border);
				ttvw.setText(r_txt_list.get(cnt));
				// setTextColor For Xperia Z
				ttvw.setTextColor(0xFF000000);
				// Display images on ScrollView
				if (r_txt_list.get(cnt).length() > 0) {
					linearLayout.addView(ttvw, lp);
				}
			}
			right_text.setText(r_txt_list.get(0));
		}
		// ---------------------------------------------------------------------------------------------------
		// Focus down left scrollView
		my_scrollView.postDelayed(new Runnable() {
			public void run() {
				my_scrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}
		}, 100);

		// Focus down right scrollView
		scrollView.postDelayed(new Runnable() {
			public void run() {
				scrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}
		}, 100);

		// Get parameters from previous Activity
		sex = "man";

		// Get UUID
		uuid = getset.getUuid();

		// WebSocket ----------------------------------------------------------------------------------------------------
		mHandler = new Handler();

		// Connection-------------------------------------------------------------------------------------------------------

		// Display my Avatar
		// Prepare SQLite
		helper = new DatabaseHelper(getApplicationContext());
		db = helper.getWritableDatabase();

		// get my avatar from database
		cursor_sex = DatabaseHelper.select_sex(db);
		if (cursor_sex.moveToFirst()) {
			do {
				sex = cursor_sex.getString(cursor_sex.getColumnIndex("sex"));
			} while (cursor_sex.moveToNext());
		}
		cursor_sex.close();
		Log.d("sex", sex);

		// GET Image Names From SQLite
		cursor_img = DatabaseHelper.select_img(db);
		if (cursor_img.moveToFirst()) {
			do {
				my_hair_img = cursor_img.getString(cursor_img.getColumnIndex("my_hair"));
				my_face_img = cursor_img.getString(cursor_img.getColumnIndex("my_face"));
				my_body_img = cursor_img.getString(cursor_img.getColumnIndex("my_body"));
			} while (cursor_img.moveToNext());
		}
		cursor_img.close();

		// Close the database
		DatabaseHelper.Close(db);

		// My Surface View
		chat_my_surface = (MyChatDrawingPanel) findViewById(R.id.chat_my_surface);
		chat_my_surface.setZOrderOnTop(true);
		SurfaceHolder chat_my_surface_holder = chat_my_surface.getHolder();
		chat_my_surface_holder.setFormat(PixelFormat.TRANSPARENT);

		// From This To DressUpActivity
		chat_my_surface.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(RandomChatActivity.this, PaletteActivity.class);
				startActivity(intent);
			}
		});

		// Her Surface View
		chat_her_surface = (HerChatDrawingPanel) findViewById(R.id.chat_her_surface);
		chat_her_surface.setZOrderOnTop(true);
		SurfaceHolder chat_her_surface_holder = chat_her_surface.getHolder();
		chat_her_surface_holder.setFormat(PixelFormat.TRANSPARENT);

		// From This To Horizontal ListView
		fm = getSupportFragmentManager();
		chat_her_surface.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CustomDialogFragment c_dia = new CustomDialogFragment(context);
				c_dia.show(fm, "mboard");
			}
		});
		// HER GOOD INVISIBLE
		her_ok_action.setVisibility(View.INVISIBLE);
	}// END onCreate

	/**
	 * onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();

		// make OK GOOD
		// OK Action
		Button good_button = (Button) findViewById(R.id.good_button);
		good_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					/**
					 * Format For Ahcava is like ...
					 * { result : ["key_1":"aaaaa", "key_2":"bbbbb", "key_3":"ccccc" ] }
					 * JSONObject -> ( JSONArray -> (JSONObject ) )
					 */
					String export = "";
					JSONObject export_json = new JSONObject();
					JSONObject json_obj = new JSONObject();
					JSONArray json_array = new JSONArray();
					// Add Values to JsonObject
					json_obj.put("uuid", uuid);
					json_obj.put("my_name", "");
					json_obj.put("her_name", "");
					json_obj.put("action", "good");
					json_obj.put("change_user", "none");
					json_obj.put("good_count", "");
					json_obj.put("my_hair_img", my_hair_img);
					json_obj.put("my_face_img", my_face_img);
					json_obj.put("my_body_img", my_body_img);
					// Add JsonObject to JsonArray
					json_array.put(json_obj);
					// Add JsonArray to JsonObject
					export_json.put("my_avatar", json_array);
					export = export_json.toString();
					Log.d("json_format", export);
					mClient.send(export);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				// My Action
				ImageButton ok_action = (ImageButton) findViewById(R.id.my_ok_action);
				ok_action.setVisibility(View.VISIBLE);
				handler.postDelayed(action_timer, 1000);
			}
		});

		// Change User
		Button change_btn = (Button) findViewById(R.id.change_button);
		change_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (reset_flag == false) {
					try {
						/**
						 * Format For Ahcava is like ...
						 * { result : ["key_1":"aaaaa", "key_2":"bbbbb", "key_3":"ccccc" ] }
						 * JSONObject -> ( JSONArray -> (JSONObject ) )
						 */
						String export = "";
						JSONObject export_json = new JSONObject();
						JSONObject json_obj = new JSONObject();
						JSONArray json_array = new JSONArray();
						// Add Values to JsonObject
						json_obj.put("message", "");
						json_obj.put("uuid", uuid);
						json_obj.put("my_name", "");
						json_obj.put("her_name", "");
						json_obj.put("action", "none");
						json_obj.put("change_user", "random");
						json_obj.put("good_count", "");
						json_obj.put("my_hair_img", my_hair_img);
						json_obj.put("my_face_img", my_face_img);
						json_obj.put("my_body_img", my_body_img);
						// Add JsonObject to JsonArray
						json_array.put(json_obj);
						// Add JsonArray to JsonObject
						export_json.put("my_avatar", json_array);
						export = export_json.toString();
						Log.d("json_format", export);
						mClient.send(export);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					// turn on the flag
					reset_flag = true;
				} else if (reset_flag == true) {
					try {
						/**
						 * Format For Ahcava is like ...
						 * { result : ["key_1":"aaaaa", "key_2":"bbbbb", "key_3":"ccccc" ] }
						 * JSONObject -> ( JSONArray -> (JSONObject ) )
						 */
						String export = "";
						JSONObject export_json = new JSONObject();
						JSONObject json_obj = new JSONObject();
						JSONArray json_array = new JSONArray();
						// Add Values to JsonObject
						json_obj.put("message", "");
						json_obj.put("uuid", uuid);
						json_obj.put("my_name", "");
						json_obj.put("her_name", "");
						json_obj.put("action", "none");
						json_obj.put("change_user", "reset");
						json_obj.put("good_count", "");
						json_obj.put("my_hair_img", my_hair_img);
						json_obj.put("my_face_img", my_face_img);
						json_obj.put("my_body_img", my_body_img);
						// Add JsonObject to JsonArray
						json_array.put(json_obj);
						// Add JsonArray to JsonObject
						export_json.put("my_avatar", json_array);
						export = export_json.toString();
						Log.d("json_format", export);
						mClient.send(export);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					// turn off the flag
					reset_flag = false;
					// clear her avatar
					resetHerAvatar();
					// Set Her Name on the Action Bar
					getActionBar().setTitle(getString(R.string.looking_for));
				}
				if (reset_flag) {
					Log.d("reset_flag", "true");
				}
				if (reset_flag == false) {
					Log.d("reset_flag", "false");
				}

			}
		});

		// Send Message
		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				EditText edit = (EditText) findViewById(R.id.random_chat_message);
				try {
					/**
					 * Format For Ahcava is like ...
					 * { result : ["key_1":"aaaaa", "key_2":"bbbbb", "key_3":"ccccc" ] }
					 * JSONObject -> ( JSONArray -> (JSONObject ) )
					 */
					String export = "";
					String my_message = edit.getText().toString();
					JSONObject export_json = new JSONObject();
					JSONObject json_obj = new JSONObject();
					JSONArray json_array = new JSONArray();
					// Add Values to JsonObject
					json_obj.put("message", my_message);
					json_obj.put("uuid", uuid);
					json_obj.put("my_name", "");
					json_obj.put("her_name", "");
					json_obj.put("action", "none");
					json_obj.put("change_user", "none");
					json_obj.put("good_count", "");
					json_obj.put("my_hair_img", my_hair_img);
					json_obj.put("my_face_img", my_face_img);
					json_obj.put("my_body_img", my_body_img);
					// Add JsonObject to JsonArray
					json_array.put(json_obj);
					// Add JsonArray to JsonObject
					export_json.put("my_avatar", json_array);
					export = export_json.toString();
					Log.d("json_format", export);
					mClient.send(export);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				try {
					String my_message = edit.getText().toString();
					left_text.setText(my_message);
					// Prepare SQLite
					helper = new DatabaseHelper(getApplicationContext());
					db = helper.getWritableDatabase();

					// remove full-width space("　") and half-width space(" ")
					String my_message_without_space = my_message.replaceAll("[　 ]", "");

					// INSERT My Messages INTO SQLite
					if (my_message.length() > 0 && my_message_without_space.length() > 0) {
						DatabaseHelper.insert_my_message(db, my_message);
					}
					// set TextView -------------------------------

					// Set Messages Into ArrayList
					cursor_my_messages = DatabaseHelper.select_my_messages(db);
					if (cursor_my_messages.moveToFirst()) {
						int i = 0;
						l_txt_list = new ArrayList<String>();
						do {
							String text = cursor_my_messages.getString(cursor_my_messages.getColumnIndex("my_message"));
							l_txt_list.add(text);
							Log.d("watch_cursor", "l_txt_list.get(" + i + ") = " + l_txt_list.get(i));
							i++;
						} while (cursor_my_messages.moveToNext());
					}
					cursor_my_messages.close();
					// Reset Views
					my_linearLayout.removeAllViews();

					// Set the height of LinearLayout
					if (l_txt_list != null) {
						if (l_txt_list.size() - 1 < max_addview_number) {
							cnt_txt = l_txt_list.size() - 1;
						} else if (l_txt_list.size() - 1 >= max_addview_number) {
							cnt_txt = max_addview_number;
						}
					}
					my_scroll_param.height = add_height * cnt_txt;
					if (my_scroll_param.height == 0) {
						my_scroll_param.height = 0;
					}
					my_scrollView.setLayoutParams(my_scroll_param);

					if (l_txt_list != null) {
						// Display My Messages Log
						for (int cnt = l_txt_list.size() - 1; cnt > 0; cnt--) {
							TextView ttvw = new TextView(context);
							// Add view to a vertical LinearLayout at bottom (programatically)
							// Set Listener
							ttvw.setId(cnt);
							// Set Messages
							ttvw.setBackgroundResource(R.drawable.textview_border);
							ttvw.setText(l_txt_list.get(cnt));
							// setTextColor For Xperia Z
							ttvw.setTextColor(0xFF000000);
							// Display images on ScrollView
							if (l_txt_list.get(cnt).length() > 0) {
								my_linearLayout.addView(ttvw, lp);
							}
						}
					}
					// Close the database
					DatabaseHelper.Close(db);
					// END set TextView -----------------------------------------

					// Focus down left scrollView
					my_scrollView.post(new Runnable() {
						public void run() {
							my_scrollView.fullScroll(ScrollView.FOCUS_DOWN);
						}
					});

					// Reset EditText
					edit.setText("");
				} catch (NotYetConnectedException e) {
					e.printStackTrace();
				} // Removed InterruptedException

				// Keyboard Configration
				InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});
	}
	/**
	 *  This removes any currently shown dialog.
	 */
	void showDialog() {
	    mStackLevel++;

	    // DialogFragment.show() will take care of adding the fragment
	    // in a transaction.  We also want to remove any currently showing
	    // dialog, so make our own transaction and take care of that here.
	    android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	    android.support.v4.app.Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);

	    // Create and show the dialog.
	    CustomDialogFragment newFragment = CustomDialogFragment.newInstance(mStackLevel);
	    newFragment.show(ft, "dialog");
	}	
	

	/**
	 * Menu Items
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_random_chat, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.chat_log:
			toChatLog();
			return true;
		case R.id.change_name:
			toChangeName();
			return true;
		case R.id.official_web:
			toOfficialWeb();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// For going to another activity on clicked Item
	private void toChatLog() {
		Intent intent = new Intent(RandomChatActivity.this, ChatLogActivity.class);
		startActivity(intent);
	}

	private void toChangeName() {
		Intent intent = new Intent(RandomChatActivity.this, NameActivity.class);
		startActivity(intent);
	}

	private void toOfficialWeb() {
		Uri official_web = Uri.parse("http://s.aeriagames.jp/ahcava");
		Intent i = new Intent(Intent.ACTION_VIEW, official_web);
		startActivity(i);
	}

	/**
	 * onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Log.d("lifecycle", "onResume()");
		// Create Ahcava_lib Instance
		Ahcava_lib ahcavalib = new Ahcava_lib();
		// Network Error Dialog
		ahcavalib.Network_Error_Dialog(context);
		// Switch to a production server or development server.
		URI uri;
		String which_handler = "chat";
		uri = ahcavalib.Call_handler(which_handler);

		/**
		 * Establish WebSocket Connection
		 */
		// Use IPv4
		ahcavalib.Ipv_conf();

		// Create an instance of websocket client.
		mClient = new WebSocketClient(uri) {
			@Override
			public void onOpen(ServerHandshake handshake) {
				Log.d("WebsocketActivity", "onOpen");
				// Create JSON String
				try {
					/**
					 * Format For Ahcava is like ...
					 * { result : ["key_1":"aaaaa", "key_2":"bbbbb", "key_3":"ccccc" ] }
					 * JSONObject -> ( JSONArray -> (JSONObject ) )
					 */
					String export = "";
					JSONObject export_json = new JSONObject();
					JSONObject json_obj = new JSONObject();
					JSONArray json_array = new JSONArray();
					// Add Values to JsonObject
					json_obj.put("message", "");
					json_obj.put("uuid", uuid);
					json_obj.put("my_name", "");
					json_obj.put("her_name", "");
					json_obj.put("action", "none");
					json_obj.put("change_user", "onOpen");
					json_obj.put("good_count", "");
					json_obj.put("my_hair_img", my_hair_img);
					json_obj.put("my_face_img", my_face_img);
					json_obj.put("my_body_img", my_body_img);
					// Add JsonObject to JsonArray
					json_array.put(json_obj);
					// Add JsonArray to JsonObject
					export_json.put("my_avatar", json_array);
					export = export_json.toString();
					Log.d("json_format", export);
					mClient.send(export);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onMessage(final String message) {
				Log.d(TAG, "onMessage");
				Log.d("WebsocketActivity", "Message:" + message);
				mHandler.post(new Runnable() {
					public void run() {
						try {
							JSONObject rootObject = new JSONObject(message);
							JSONArray rootArray = rootObject.getJSONArray("my_avatar");
							// Get JSON Data or Not
							JSONObject jsonObject = rootArray.getJSONObject(0);
							// Skip null
							msg_txt = jsonObject.getString("message");
							if (jsonObject.has("to_uuid")) {
								from_uuid = jsonObject.getString("to_uuid");
							}
							if (jsonObject.has("change_user")) {
								change_user = jsonObject.getString("change_user");
							}
							Log.d("json", "1" + msg_txt);
							if (msg_txt.length() > 0) {
								Log.d("json", "2" + msg_txt);
								right_text.setText(msg_txt);
							}
							action = jsonObject.getString("action");
							g_cnt = jsonObject.getString("good_count");
							Log.d("g_cnt", "onMessage: g_cnt = " + g_cnt);
							if (g_cnt.equals("null")) {
								g_cnt = "0";
							}
							if (action.equals("good")) {
								good_count.setText(g_cnt);
							}

							her_name_txt = jsonObject.getString("her_name");

							// Her avatar image names
							her_hair_img = jsonObject.getString("my_hair_img");
							her_face_img = jsonObject.getString("my_face_img");
							her_body_img = jsonObject.getString("my_body_img");

							Log.d("json", her_hair_img);
							Log.d("json", her_face_img);
							Log.d("json", her_body_img);
							// Prepare SQLite
							helper = new DatabaseHelper(getApplicationContext());
							db = helper.getWritableDatabase();

							// Good Action
							if (action.equals("good")) {
								// My Action
								her_ok_action.setVisibility(View.VISIBLE);
								handler.postDelayed(her_action_timer, 1000);
								Log.d("her_ok_if", "her_ok_if");
								action = "none";
								// UPDATE GOOD COUNT
								ContentValues args = new ContentValues();
								args.put("good_count", g_cnt);
								db.update("count", args, null, null);
							}

							// INSERT a message INTO SQLite
							if (from_uuid.length() > 0 && msg_txt.length() > 0) {
								Log.d("insert_message", "from_uuid = " + from_uuid + ", msg_txt = " + msg_txt);
								DatabaseHelper.insert_message(db, from_uuid, msg_txt);
							}
							// New Text
							r_txt_list = new ArrayList<String>();
							// Get Messages From SQLite
							cursor_messages = DatabaseHelper.select_messages(db);
							if (cursor_messages.moveToFirst()) {
								int i = 0;
								do {
									String text = cursor_messages.getString(cursor_messages.getColumnIndex("message"));
									r_txt_list.add(text);
									Log.d("watch_cursor", "r_txt_list.get(" + i + ") = " + r_txt_list.get(i));
									i++;
								} while (cursor_messages.moveToNext());
							}
							// Close the database
							DatabaseHelper.Close(db);
							// Set Her Name on the Action Bar
							if (change_user.equals("reset")) {
								getActionBar().setTitle(getString(R.string.looking_for));
							} else {
								getActionBar().setTitle(her_name_txt);
							}
							// Reset Views
							linearLayout.removeAllViews();

							// Set the height of LinearLayout
							if (r_txt_list != null) {
								cnt_txt_2 = 0;
								if (r_txt_list.size() - 1 < max_addview_number) {
									cnt_txt_2 = r_txt_list.size() - 1;
								} else if (r_txt_list.size() - 1 >= max_addview_number) {
									cnt_txt_2 = max_addview_number;
								}
							}
							scroll_param.height = add_height * cnt_txt_2;
							scrollView.setLayoutParams(scroll_param);

							if (r_txt_list != null) {
								for (int cnt = r_txt_list.size() - 1; cnt > 0; cnt--) {
									TextView ttvw = new TextView(context);
									// Set Listener
									ttvw.setId(cnt);
									// Set Messages
									ttvw.setBackgroundResource(R.drawable.textview_border);
									ttvw.setText(r_txt_list.get(cnt));
									// setTextColor For Xperia Z
									ttvw.setTextColor(0xFF000000);

									// Display images on ScrollView
									if (r_txt_list.get(cnt).length() > 0) {
										linearLayout.addView(ttvw, lp);
									}
								}
							}
							// Focus down right scrollView
							scrollView.post(new Runnable() {
								public void run() {
									scrollView.fullScroll(ScrollView.FOCUS_DOWN);
								}
							});

						} catch (JSONException e) {
							Log.d("json", e.getMessage());
						}
					}
				});
			}

			@Override
			public void onError(Exception ex) {
				Log.d(TAG, "onError");
			}

			@Override
			public void onClose(int code, String reason, boolean remote) {
				Log.d(TAG, "onClose");
			}
		};
		// End of WebSocket

		// Establish WebSocket Connection
		mClient.connect();
		// run Loop
		killMe = false;
		// Handler
		handler.postDelayed(runnable, 1000);
	}

	// onPause()
	@Override
	protected void onPause() {
		super.onPause();
		Log.d("lifecycle", "onPause()");
		// Close the websocket connection
		mClient.close();
	}

	// onStop()
	@Override
	protected void onStop() {
		super.onStop();
		Log.d("lifecycle", "onStop()");
		// Close the websocket connection
		mClient.close();
		// Close the database
		DatabaseHelper.Close(db);
		// Kill myself
		killRunnable();
	}

	// Kill myself
	private void killRunnable() {
		killMe = true;
	}

	// Disable back button
	@Override
	public void onBackPressed() {
	}

}// END RandomChatActivity