package com.ver1.avacha;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Tetsuro Takemoto
 *         Ahcava!
 *         This creates HTTP connection using AsyncTask.
 */
public class HttpAsyncTask extends AsyncTask<String, Void, String> {

	AndroidHttpClient client;
	HttpURLConnection urlConnection;

	// doInBackground
	protected String doInBackground(String... params) {
		Log.d("doinback", "doInBackground");
		try {
			String query = URLEncoder.encode(params[1],"UTF-8");
			URL url = new URL(params[0] + "?json=" + query);
			Log.d("URL","URL = " + params[0] + "?json=" + params[1]);
			urlConnection = (HttpURLConnection) url.openConnection();
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			String result = readStream(in);
			return result;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			urlConnection.disconnect();
		}
		return "";
	}

	// onPostExecute (Basically, this method should be overridden.) 
	protected void onPostExecute(String result) {
		String testStr = "";
		try {
			// Convert JSON to normal Java Array
			JSONObject rootObject = new JSONObject(result);
			JSONArray rootArray = rootObject.getJSONArray("result");
			for (int i = 0; i < rootArray.length(); i++) {
				JSONObject jsonObject = rootArray.getJSONObject(i);
				testStr = jsonObject.getString("key");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d("testStr = ", testStr);
	}

	// Reading HTTP input stream
	protected String readStream(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String result, line = reader.readLine();
		result = line;
		while ((line = reader.readLine()) != null) {
			result += line;
		}
		return result;
	}
}