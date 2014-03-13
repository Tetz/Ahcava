package com.ver1.avacha;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

public class CustomDialogFragment extends DialogFragment {

	// Declare variables
	private ListView list_view;
	private static Context con;
	private String[] params;
	public int mNum;
	private String uuid;
	private String mboard_id;
	private String export = "";
	private String urlStr;
	private HttpAsyncTask task;
	private HttpAsyncTask task_onclick;
	private Dialog dialog;
	private String comment;

	public CustomDialogFragment(Context context) {
		con = context;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Create Dialog
		dialog = new Dialog(getActivity());
		// Display title
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		// full screen
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		dialog.setContentView(R.layout.dialog_custom);
		// Transparent background
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		// Finding view by id
		list_view = (ListView) dialog.findViewById(R.id.list_message_board);
		// Set URL into params[0].
		Ahcava_lib a_lib = new Ahcava_lib();
		urlStr = a_lib.get_uri_http().toString();

		// HTTP Connection
		task = new HttpAsyncTask() {
			@Override
			protected void onPostExecute(String result) {
				try {
					// Convert JSON to normal Java Array
					JSONObject rootObject = new JSONObject(result);
					JSONArray rootArray = rootObject.getJSONArray("result");
					JSONObject[] values = new JSONObject[rootArray.length()];
					// Set json object into array.
					for (int i = 0; i < rootArray.length(); i++) {
						JSONObject jsonObject = rootArray.getJSONObject(i);
						values[i] = jsonObject;
					}
					BoardListAdapter adapter = new BoardListAdapter(con, 0, values);
					list_view.setAdapter(adapter);
					Log.d("onPostExecute", "Not Exception");
				} catch (JSONException e) {
					Log.d("onPostExecute", "Exception");
					e.printStackTrace();
				}
			}
		};

		try {
			// Get UUID
			GetSet getset = new GetSet(con);
			uuid = getset.getUuid();
			/**
			 * Format For Ahcava is like ...
			 * { result : ["key_1":"aaaaa", "key_2":"bbbbb", "key_3":"ccccc" ] }
			 * JSONObject -> ( JSONArray -> (JSONObject ) )
			 */
			JSONObject export_json = new JSONObject();
			JSONObject json_obj = new JSONObject();
			JSONArray json_array = new JSONArray();
			// Add Values to JsonObject
			json_obj.put("uuid", uuid);
			json_obj.put("action", "reload");
			// Add JsonObject to JsonArray
			json_array.put(json_obj);
			// Add JsonArray to JsonObject
			export_json.put("my_avatar", json_array);
			export = export_json.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		/**
		 * Reserved parameters
		 * params[0] <= url
		 * params[1] <= key
		 * params[2] <= id
		 * params[3] <=
		 * params[4] <=
		 */
		params = new String[10];
		params[0] = urlStr;
		params[1] = export;
		task.execute(params);

		// OK button litener
		dialog.findViewById(R.id.positive_button).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				task_onclick = new HttpAsyncTask() {
					@Override
					protected void onPostExecute(String result) {
						try {
							// Convert JSON to normal Java Array
							JSONObject rootObject = new JSONObject(result);
							JSONArray rootArray = rootObject.getJSONArray("result");
							JSONObject[] values = new JSONObject[rootArray.length()];
							// Set json object into array
							for (int i = 0; i < rootArray.length(); i++) {
								JSONObject jsonObject = rootArray.getJSONObject(i);
								values[i] = jsonObject;
							}
							BoardListAdapter adapter = new BoardListAdapter(con, 0, values);
							list_view.setAdapter(adapter);
							Log.d("onPostExecute", "Not Exception");
						} catch (JSONException e) {
							Log.d("onPostExecute", "Exception");
							e.printStackTrace();
						}
					}
				};

				EditText editText = (EditText) dialog.findViewById(R.id.board_input_comment);
				comment = editText.getText().toString();
				try {
					// Get UUID
					GetSet getset = new GetSet(con);
					uuid = getset.getUuid();
					/**
					 * Format For Ahcava is like ...
					 * { result : ["key_1":"aaaaa", "key_2":"bbbbb", "key_3":"ccccc" ] }
					 * JSONObject -> ( JSONArray -> (JSONObject ) )
					 */
					JSONObject export_json = new JSONObject();
					JSONObject json_obj = new JSONObject();
					JSONArray json_array = new JSONArray();
					// Add Values to JsonObject
					json_obj.put("uuid", uuid);
					json_obj.put("action", "get");
					if (comment.length() > 0) {
						json_obj.put("comment", comment);
					}
					json_obj.put("mboard_id", "999");
					// Add JsonObject to JsonArray
					json_array.put(json_obj);
					// Add JsonArray to JsonObject
					export_json.put("my_avatar", json_array);
					export = export_json.toString();
					Log.d("export_json", export);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				params[0] = urlStr;
				params[1] = export;
				task_onclick.execute(params);

				// Reset EditText
				editText.setText("");

				// Keyboard Configration
				InputMethodManager inputManager = (InputMethodManager) con.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(dialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

			}
		});

		// Close button listener
		dialog.findViewById(R.id.close_button).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});
		return dialog;
	}

	/**
	 * Create a new instance of MyDialogFragment, providing "num"
	 * as an argument.
	 */
	static CustomDialogFragment newInstance(int num) {
		CustomDialogFragment f = new CustomDialogFragment(con);
		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);
		return f;
	}
}
