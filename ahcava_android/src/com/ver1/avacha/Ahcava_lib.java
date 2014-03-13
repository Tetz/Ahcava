package com.ver1.avacha;

import java.net.URI;
import java.net.URISyntaxException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

/**
 * 
 * This class has reusable methods. Don't repeat yourself :D
 * 
 * @author Tetsuro Takemoto
 * 
 */
public class Ahcava_lib {

	// TODO Get URI for connecting web servers
	public URI get_uri_http() {
		URI uri = null;
		if (BuildConfig.DEBUG) {
			// Development Server
			try {
				uri = new URI("http://ahcava-dev.aeriagames.jp:8999/");
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else {
			// Production Server
			try {
				uri = new URI("http://ahcava.aeriagames.jp:8999/");
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return uri;
	}

	// This is for switching servers automatically.
	public URI Call_handler(String which_handler) {
		URI uri = null;
		if (BuildConfig.DEBUG) {
			// Development Server
			try {
				uri = new URI("ws://ahcava-dev.aeriagames.jp:9000/" + which_handler);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else {
			// Production Server
			try {
				uri = new URI("ws://ahcava.aeriagames.jp:9000/" + which_handler);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return uri;
	}

	// Show an error dialog if the application is disconnected to the internet.
	public void Network_Error_Dialog(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null) {
			if (!info.isConnected()) {
				Toast.makeText(context, "通信エラーが発生しました。電波の良い所で再度試してください。", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(context, "通信エラーが発生しました。電波の良い所で再度試してください。", Toast.LENGTH_SHORT).show();
		}
	}

	// Use IPv4
	public void Ipv_conf() {
		if ("sdk".equals(Build.PRODUCT)) {
			// Make IPv6 false on VM
			java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
			java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
		}
	}
}
