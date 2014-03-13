package com.ver1.avacha;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.PushService;

// Parse require some packages 

public class SelectAvatarActivity extends Activity {
	public RightDefaultDrawingPanel default_right_surface;
	public LeftDefaultDrawingPanel default_left_surface;
	private static Context context;
	private SQLiteDatabase db;
	// Default Image Names
	public static String my_hair_img = "1208001203_55.png";
	public static String my_face_img = "1208003612_53.png";
	public static String my_body_img = "1210001605.png";

	// Preparation For WebSocket
	private WebSocketClient mClient;
	private Handler mHandler;
	private static final String TAG = "WebsocketActivity";

	// preference
	private SharedPreferences preference;
	private Editor editor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_avatar);
		// WebSocket
		mHandler = new Handler();
		// get Context
		context = MyApplication.getAppContext();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// default_right_surface
		default_right_surface = (RightDefaultDrawingPanel) findViewById(R.id.default_right_surface);
		default_right_surface.setZOrderOnTop(true);
		SurfaceHolder default_right_surface_holder = default_right_surface.getHolder();
		default_right_surface_holder.setFormat(PixelFormat.TRANSPARENT);

		// default_left_surface
		default_left_surface = (LeftDefaultDrawingPanel) findViewById(R.id.default_left_surface);
		default_left_surface.setZOrderOnTop(true);
		SurfaceHolder default_left_surface_holder = default_left_surface.getHolder();
		default_left_surface_holder.setFormat(PixelFormat.TRANSPARENT);

		// Create Ahcava_lib Instance
		Ahcava_lib ahcavalib = new Ahcava_lib();
		// Network Error Dialog
		ahcavalib.Network_Error_Dialog(context);
		// Switch to a production server or development server.
		URI uri;
		String which_handler = "version";
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
				try {
					// Create JSON String
					String s = "{\"my_avatar\":[" + "{\"version\":\"\"" + "}" + "]}";
					// Get my name at once
					mClient.send(s);
				} catch (NotYetConnectedException e) {
					e.printStackTrace();
				} // Removed InterruptedException
			}

			@Override
			public void onMessage(final String message) {
				Log.d(TAG, "onMessage");
				Log.d("WebsocketActivity", "Message:" + message);
				mHandler.post(new Runnable() {
					public void run() {
						// Toast.makeText(WebsocketActivity.this, message,
						// Toast.LENGTH_SHORT).show();
						try {
							JSONObject rootObject = new JSONObject(message);
							JSONArray rootArray = rootObject.getJSONArray("my_avatar");
							JSONObject jsonObject = rootArray.getJSONObject(0);
							// Get the latest version from the server
							String version_s = jsonObject.getString("version");
							// Get the this app's version name
							String version_c = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;

							// version management
							if (version_c.equals(version_s)) {
								// set preference
								preference = getSharedPreferences("Preference Name", MODE_PRIVATE);
								editor = preference.edit();

								if (preference.getBoolean("Launched", false) == false) {
									// run only on the first time
									editor.putBoolean("Launched", true);
									editor.commit();

									// Prepare SQLite
									DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
									db = helper.getWritableDatabase();

									// Male Avatar
									default_left_surface.setOnClickListener(new View.OnClickListener() {
										public void onClick(View v) {
											// INSERT SEX TO SQLite
											String sex = "man";
											DatabaseHelper.insert_login_status(db, sex);
											DatabaseHelper.Close(db);
											// Next Activity
											Intent intent = new Intent(SelectAvatarActivity.this, RandomChatActivity.class);
											startActivity(intent);
										}
									});

									// Female Avatar
									default_right_surface.setOnClickListener(new View.OnClickListener() {
										public void onClick(View v) {
											// INSERT SEX TO SQLite
											String sex = "woman";
											DatabaseHelper.insert_login_status(db, sex);
											DatabaseHelper.insert(db, my_hair_img, my_face_img, my_body_img);
											DatabaseHelper.Close(db);
											// Next Activity
											Intent intent = new Intent(SelectAvatarActivity.this, RandomChatActivity.class);
											startActivity(intent);
										}
									});
								} else {
									// Next Activity
									Intent intent = new Intent(SelectAvatarActivity.this, RandomChatActivity.class);
									startActivity(intent);
								}
							} else { // Display customo dialog
								UpdateDialogFragment update = new UpdateDialogFragment(context);
								update.show(getFragmentManager(), version_c);
							}

						} catch (JSONException e) {
							Log.d("json", e.getMessage());
						} catch (NameNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
		// Establish WebScoket Connection
		mClient.connect();
	}

	// onStop()
	@Override
	protected void onPause() {
		super.onPause();
		mClient.close();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mClient.close();
	}

	// Disable back button
	@Override
	public void onBackPressed() {
	}

}
