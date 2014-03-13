package com.ver1.avacha;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ChatLogActivity extends ListActivity {
	public String uuid = "";

	// Preparation For WebSocket
	private WebSocketClient mClient;
	private Handler mHandler;
	private static final String TAG = "WebsocketActivity";

	// Declares Arrays For Name And Date
	private String[] user_Name;
	private String[] user_Date;
	Handler guiThreadHandler;

	// Chat User List
	private ArrayList<FriendStatus> list = null;
	private FriendsListAdapter adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_log);
		// Get UUID
		GetSet getset = new GetSet(getApplicationContext());
		uuid = getset.getUuid();
		// Create new one
		user_Name = new String[6];
		user_Date = new String[6];
		// WebSocket
		mHandler = new Handler();
		// Set Content
		guiThreadHandler = new Handler();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Send Message
		Button btn = (Button) findViewById(R.id.back);
		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Back to RandomChatActivity
				Intent intent = new Intent(ChatLogActivity.this, RandomChatActivity.class);
				startActivity(intent);
			}
		});

		// Create Ahcava_lib Instance
		Ahcava_lib ahcavalib = new Ahcava_lib();
		// Network Error Dialog
		ahcavalib.Network_Error_Dialog(this);
		// Switch to a production server or development server.
		URI uri;
		String which_handler = "log";
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
				Log.d("ChatLogAcitivity", "onOpen");
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
					json_obj.put("uuid", uuid);
					json_obj.put("check", "n");
					json_obj.put("position", "none");
					json_obj.put("matching", "");
					json_obj.put("chat_user_log", "");
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
							String user_1;
							String user_2;
							String user_3;
							String user_4;
							String user_5;

							if (jsonObject.has("user_1")) {
								user_1 = jsonObject.getString("user_1");
								JSONObject userObject_1 = new JSONObject(user_1);
								user_Name[1] = userObject_1.getString("name");
								user_Date[1] = userObject_1.getString("date");
								if (user_Name[1].length() == 0) {
									user_Name[1] = "";
									user_Date[1] = "";
								}

							}
							if (jsonObject.has("user_2")) {
								user_2 = jsonObject.getString("user_2");
								JSONObject userObject_2 = new JSONObject(user_2);
								user_Name[2] = userObject_2.getString("name");
								user_Date[2] = userObject_2.getString("date");
								if (user_Name[2].length() == 0) {
									user_Name[2] = "";
									user_Date[2] = "";
								}

							}
							if (jsonObject.has("user_3")) {
								user_3 = jsonObject.getString("user_3");
								JSONObject userObject_3 = new JSONObject(user_3);
								user_Name[3] = userObject_3.getString("name");
								user_Date[3] = userObject_3.getString("date");
								if (user_Name[3].length() == 0) {
									user_Name[3] = "";
									user_Date[3] = "";
								}

							}
							if (jsonObject.has("user_4")) {
								user_4 = jsonObject.getString("user_4");
								JSONObject userObject_4 = new JSONObject(user_4);
								user_Name[4] = userObject_4.getString("name");
								user_Date[4] = userObject_4.getString("date");
								if (user_Name[4].length() == 0) {
									user_Name[4] = "";
									user_Date[4] = "";
								}

							}
							if (jsonObject.has("user_5")) {
								user_5 = jsonObject.getString("user_5");
								JSONObject userObject_5 = new JSONObject(user_5);
								user_Name[5] = userObject_5.getString("name");
								user_Date[5] = userObject_5.getString("date");
								if (user_Name[5].length() == 0) {
									user_Name[5] = "";
									user_Date[5] = "";
								}

							}

							if (jsonObject.has("matching")) {
								String matching = jsonObject.getString("matching");
								if (matching.equals("complete")) {
									// Back to RandomChatActivity
									Intent intent = new Intent(ChatLogActivity.this, RandomChatActivity.class);
									startActivity(intent);
								}
							}

							// Display User Log
							guiThreadHandler.post(new Runnable() {
								public void run() {
									createData();
									adapter = new FriendsListAdapter(getApplicationContext(), R.layout.friend_list_row, list);
									setListAdapter(adapter);
								}
							});

						} catch (JSONException e) {
							Log.d("json_users", "JSONException");
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

	@Override
	protected void onStop() {
		super.onStop();
		mClient.close();
	}

	private void createData() {
		this.list = new ArrayList<FriendStatus>();
		FriendStatus item1 = new FriendStatus();
		item1.setName(user_Name[1]);
		item1.setText(user_Date[1]);
		Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.friends_list_2_1c);
		// item1.setPng(bitmap1);
		list.add(item1);

		FriendStatus item2 = new FriendStatus();
		item2.setName(user_Name[2]);
		item2.setText(user_Date[2]);
		Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.friends_list_2_2c);
		// item2.setPng(bitmap2);
		list.add(item2);

		FriendStatus item3 = new FriendStatus();
		item3.setName(user_Name[3]);
		item3.setText(user_Date[3]);
		Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.friends_list_2_3c);
		// item3.setPng(bitmap3);
		list.add(item3);

		FriendStatus item4 = new FriendStatus();
		item4.setName(user_Name[4]);
		item4.setText(user_Date[4]);
		Bitmap bitmap4 = BitmapFactory.decodeResource(getResources(), R.drawable.friends_list_2_4c);
		// item4.setPng(bitmap4);
		list.add(item4);

		FriendStatus item5 = new FriendStatus();
		item5.setName(user_Name[5]);
		item5.setText(user_Date[5]);
		Bitmap bitmap5 = BitmapFactory.decodeResource(getResources(), R.drawable.friends_list_2_5c);
		// item5.setPng(bitmap5);
		list.add(item5);

		// FriendStatus item6 = new FriendStatus();
		// item6.setName("");
		// item6.setText("");
		// Bitmap bitmap6 = BitmapFactory.decodeResource(getResources(), R.drawable.friends_list_2_6c);
		// item6.setPng(bitmap6);
		// list.add(item6);
	}

	protected void onListItemClick(ListView listView, View v, int position, long id) {
		// super.onListItemClick(listView, v, position, id);
		Log.v("arrayadapter", position + "がクリックされた");
		// Create JSON String
		// String s = "{\"my_avatar\":[" + "{\"uuid\":\"" + uuid + "\"," + "\"check\":" + "\"y\"," + "\"position\":" + "\"" + position + "\","
		// + "\"matching\":" + "\"" + "complete" + "\"," + "\"chat_user_log\":" + "\"\"}" + // Remove
		// "]}";
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
			json_obj.put("check", "y");
			json_obj.put("position", position);
			json_obj.put("matching", "");
			json_obj.put("complete", "");
			json_obj.put("chat_user_log", "");
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

}
