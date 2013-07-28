package com.greylocku.lazer;

import com.greylocku.lazer.models.LazerUser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PlayerArrayAdapter extends ArrayAdapter<LazerUser> {

	private final Context context;
	  private final LazerUser[] values;

	  public PlayerArrayAdapter(Context context, LazerUser[] values) {
	    super(context, R.layout.player_layout, values);
	    this.context = context;
	    this.values = values;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.player_layout, parent, false);
	    
		View colorBox = rowView.findViewById(R.id.player_color);
		colorBox.setBackgroundColor(0xFFFF0000);
		
		TextView textView = (TextView) rowView.findViewById(R.id.player_name);
	    textView.setText(values[position].getName());
		
	    return rowView;
	  }
}
