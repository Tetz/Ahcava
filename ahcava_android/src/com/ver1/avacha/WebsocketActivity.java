package com.ver1.avacha;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class WebsocketActivity extends Activity {
	private static final String TAG = "WebsocketActivity";
	private Handler mHandler;
	private WebSocketClient mClient;
	public String uuid = "";

	EditText editxt1;
	TextView txt1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_websocket);

		// CREATE UUID
		GetSet getset = new GetSet(getApplicationContext());
		uuid = getset.getUuid();
		Log.d("ws_uuid", uuid);

		// Find resources from xml file
		Button btn1 = (Button) findViewById(R.id.button1);
		editxt1 = (EditText) findViewById(R.id.editText1);
		txt1 = (TextView) findViewById(R.id.textView1);

		// WebSocket
		mHandler = new Handler();

		if ("sdk".equals(Build.PRODUCT)) {
			// Make IPv6 false on VM
			java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
			java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
		}

		try {
			// Don't forget the echo.
			URI uri = new URI("ws://49.212.196.168:9000/echo");

			mClient = new WebSocketClient(uri) {
				@Override
				public void onOpen(ServerHandshake handshake) {
					Log.d("WebsocketActivity", "onOpen");
				}

				@Override
				public void onMessage(final String message) {
					Log.d(TAG, "onMessage");
					Log.d("WebsocketActivity", "Message:" + message);
					mHandler.post(new Runnable() {
						public void run() {
							// Toast.makeText(WebsocketActivity.this, message,
							// Toast.LENGTH_SHORT).show();
							txt1.setText(message);
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

			mClient.connect();

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		// Submit button
		btn1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					//Create JSON Stirng 
					String s = 
					"{\"my_avatar\":[" + 
		            "{\"message\":" + "\"" + editxt1.getText().toString() + "\"}," +  
		            "{\"uuid\":" + "\"" + uuid + "\"}" +
		            "]}";
					mClient.send(s);
				} catch (NotYetConnectedException e) {
					e.printStackTrace();
				} // Removed InterruptedException

			}
		});

	}
}
