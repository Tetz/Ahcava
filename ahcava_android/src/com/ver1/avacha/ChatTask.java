package com.ver1.avacha;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.http.AndroidHttpClient;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class ChatTask {
    
	private TextView left_text;
	private TextView right_text;
	private ImageButton ok_action;
	private String set_data_flag;
	
	/**
	 *  Constructor
	 */
	public ChatTask(ImageButton ok_action, TextView left_text,TextView right_text){
		super(); // Some people like being explicit.
		//this.ok_action = ok_action;
		this.left_text = left_text;
		this.right_text = right_text;
		
	}
	
	/**
	 *  This is invoked on the background thread immediately after the constructor finishes executing.
	 */
	protected String httpConnection(String... params){
			try {    	
			  /**
				*  params[0] ; //get JSON data
		    	*  params[1] ; //UUID
		    	*  params[2] ; //Sex
		    	*  params[3] ; //change_flag;
		    	*  params[4] ; //send_message_flag;
		    	*  params[5] ; //message;
		    	*  params[6] ; //ok_flag;
		        */
				
				//Set Data Flag
				set_data_flag = params[0];
				
	            //AndroidHttpClient
	            AndroidHttpClient client = AndroidHttpClient.newInstance("Android UserAgent");
	        	HttpResponse res = client.execute(new HttpGet("http://avacha.aeriagames.jp/index.php/?" +
	        			"uuid="+params[1] +  "&sex="+params[2]  +  "&change_flag="+params[3] +
	        			"&send_message_flag="+params[4] + "&message="+params[5] + "&ok_flag="+params[6]));
	        	
        	
	        	// Convert HttpResponse Entity Data to String
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(res.getEntity().getContent(), "UTF-8"));
	        	StringBuilder builder = new StringBuilder();
	        	String line = null;
	        	while ((line = reader.readLine()) != null) {
	        		builder.append(line + "\n");
	        	}
	        	client.close();
	        	return builder.toString();
          
		} //END TRY
		catch (Exception e) {
				e.getStackTrace();
	        	return "";
	    }		
	}
	
	/**
	 *  This is invoked on the UI thread after the background computation finishes.
	 */
	protected void setText (String result){
			try{
				// Make OK action INVISIBLE
				ok_action.setVisibility(View.INVISIBLE);
				
				// Get data from JSON , and Set.
		        if(set_data_flag == "true"){					
					// Convert JSON to normal Java Array
					JSONObject rootObject = new JSONObject(result);
					JSONArray rootArray = rootObject.getJSONArray("result");
					//Get JSON Data or Not
					for (int i = 0; i < rootArray.length(); i++) {
						JSONObject jsonObject = rootArray.getJSONObject(i);
						left_text.setText(jsonObject.getString("1"));	
						right_text.setText(jsonObject.getString("2"));				
					}//END Loop
		        }// END IF	
					
			} catch (JSONException e) {
			e.printStackTrace();
			}	 	
		
	}
	
}
