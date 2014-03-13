package com.ver1.avacha;

import java.net.URI;
import java.nio.channels.NotYetConnectedException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NameActivity extends Activity {
	public String uuid = "";
	private TextView current_my_name = null;
	private String my_name_txt = "";
	private URI uri;

	// Preparation For WebSocket
	private WebSocketClient mClient;
	private Handler mHandler;
	private static final String TAG = "WebsocketActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_name);
		// Get UUID
		GetSet getset = new GetSet(getApplicationContext());
		uuid = getset.getUuid();
		// Find View By ID
		current_my_name = (TextView) findViewById(R.id.current_my_name);
		// WebSocket
		mHandler = new Handler();
	} // End of onCreate()

	// onStart()
	protected void onStart() {
		super.onStart();
		// Send Message
		Button btn = (Button) findViewById(R.id.send_name_button);
		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				EditText edit = (EditText) findViewById(R.id.edit_name);
				try {
					/**
					 * Format For Ahcava is like ...
					 * { result : ["key_1":"aaaaa", "key_2":"bbbbb", "key_3":"ccccc" ] }
					 * JSONObject -> ( JSONArray -> (JSONObject ) )
					 */
					String export = "";
					String name = edit.getText().toString();
					JSONObject export_json = new JSONObject();
					JSONObject json_obj = new JSONObject();
					JSONArray json_array = new JSONArray();
					try {
						// Add Values to JsonObject
						json_obj.put("name", name);
						json_obj.put("return_name", "none");
						json_obj.put("uuid", uuid);
						// Add JsonObject to JsonArray
						json_array.put(json_obj);
						// Add JsonArray to JsonObject
						export_json.put("my_avatar", json_array);
						export = export_json.toString();
						Log.d("json_format", export);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					// Don't send blank
					if (name.length() > 0) {
						mClient.send(export);
						edit.setText("");
					}
				} catch (NotYetConnectedException e) {
					e.printStackTrace();
				} // Removed InterruptedException

				// Back to RandomChatActivity
				Intent intent = new Intent(NameActivity.this, RandomChatActivity.class);
				startActivity(intent);
			}
		});

	}// End of onStart()

	@Override
	protected void onResume() {
		super.onResume();
		// get context
		Context context = getApplicationContext();

		// Create Ahcava_lib Instance
		Ahcava_lib ahcavalib = new Ahcava_lib();
		// Network Error Dialog
		ahcavalib.Network_Error_Dialog(context);
		// Switch to a production server or development server.
		String which_handler = "name";
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
					/**
					 * Format For Ahcava is like ...
					 * { result : ["key_1":"aaaaa", "key_2":"bbbbb", "key_3":"ccccc" ] }
					 * JSONObject -> ( JSONArray -> (JSONObject ) )
					 */
					String export = "";
					JSONObject export_json = new JSONObject();
					JSONObject json_obj = new JSONObject();
					JSONArray json_array = new JSONArray();
					try {
						// Add Values to JsonObject
						json_obj.put("name", "");
						json_obj.put("return_name", "return");
						json_obj.put("uuid", uuid);
						// Add JsonObject to JsonArray
						json_array.put(json_obj);
						// Add JsonArray to JsonObject
						export_json.put("my_avatar", json_array);
						export = export_json.toString();
						Log.d("json_format", export);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					// Get my name at once
					mClient.send(export);
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

							// Display Name
							my_name_txt = jsonObject.getString("name");
							current_my_name.setText(my_name_txt);

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
		// Establish WebScoket Connection
		mClient.connect();
	}
	@Override
	protected void onPause() {
		super.onPause();
		mClient.close();
	}
}
