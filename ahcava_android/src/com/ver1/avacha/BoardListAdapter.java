package com.ver1.avacha;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BoardListAdapter extends ArrayAdapter<JSONObject> {

	public JSONObject jsonObject[];

	static class ViewHolder {
		public TextView name;
		public TextView date;
		public TextView comment;
	}

	public BoardListAdapter(Context context,int r_id,JSONObject[] jsonObject) {
		super(context,R.layout.row_message_board ,jsonObject);
		this.jsonObject = jsonObject;
		Log.d("BoardListAdapter", "Constructor");
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d("BoardListAdapter", "getView");
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater;
			inflater = LayoutInflater.from(getContext());
			rowView = inflater.inflate(R.layout.row_message_board, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.name = (TextView) rowView.findViewById(R.id.m_b_name);
			viewHolder.date = (TextView) rowView.findViewById(R.id.m_b_date);
			viewHolder.comment = (TextView) rowView.findViewById(R.id.m_b_comment);
			rowView.setTag(viewHolder);
		}
		 ViewHolder holder = (ViewHolder) rowView.getTag();
		 try {
			holder.name.setText(jsonObject[position].getString("name"));
			holder.date.setText(jsonObject[position].getString("date"));
			holder.comment.setText(jsonObject[position].getString("comment"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return rowView;
	}

}
