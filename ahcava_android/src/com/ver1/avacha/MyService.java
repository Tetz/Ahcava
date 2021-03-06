package com.ver1.avacha;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
	private static BroadcastReceiver m_ScreenOffReceiver;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		registerScreenOffReceiver();
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(m_ScreenOffReceiver);
		m_ScreenOffReceiver = null;
	}

	private void registerScreenOffReceiver() {
		m_ScreenOffReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d("ACTION_SCREEN_OFF", "ACTION_SCREEN_OFF");
				// do something, e.g. send Intent to main app
			}
		};
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(m_ScreenOffReceiver, filter);
	}
}
