package com.ver1.avacha;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


@SuppressWarnings("rawtypes")
public class FriendsListAdapter extends ArrayAdapter	{

private ArrayList items;
private LayoutInflater inflater;

	@SuppressWarnings("unchecked")
	public FriendsListAdapter (Context context, int textViewResourceId, ArrayList items) {
		super(context, textViewResourceId,items);
		this.items = items;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    View view = convertView;
		if (view == null) {
	        view = inflater.inflate(R.layout.friend_list_row, null);
			view.setBackgroundResource(R.drawable.list_padding);
		}
		
		if (position % 2 == 0){
			view.setBackgroundColor(0xFFcee5ad);
		} else {
			view.setBackgroundColor(0xFFf0ffda);
		}
		
		
		Log.d("tag",view.toString());
		FriendStatus item = (FriendStatus)items.get(position);
		if (item != null) {
			TextView name = (TextView)view.findViewById(R.id.toptext);
			name.setTypeface(Typeface.DEFAULT_BOLD);
			
				if (item.getPng() != null){
			//	    ImageView image = (ImageView)view.findViewById(R.id.icon);
			//	    image.setImageBitmap(item.getPng());
				}
		TextView text = (TextView)view.findViewById(R.id.bottomtext);
			if (name != null) {
				 name.setText(item.getName());
			}
			if (text != null) {
				text.setText(item.getText());
			}
	    }
	return view;
    }
} 