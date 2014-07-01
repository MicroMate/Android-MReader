package com.micromate.mreader.navigationdrawer;

import java.util.ArrayList;
import java.util.List;

import com.micromate.mreader.R;
import com.micromate.mreader.R.id;
import com.micromate.mreader.R.layout;
import com.micromate.mreader.database.Feed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FeedListAdapter extends ArrayAdapter<Feed>{

	private Context context;
	
	private List<Feed> rssChannel = new ArrayList<Feed>();
	
	public FeedListAdapter(Context context, int textViewResourceId, List<Feed> articles) {
		super(context, textViewResourceId, articles);
		// TODO Auto-generated constructor stub
		  this.context = context;
		  this.rssChannel = articles;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// 1. Create inflater 
	    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	    // 2. Get rowView from inflater
	    View rowView = inflater.inflate(R.layout.channels_list_row, parent, false);

	    // 3. Get the two text view from the rowView
	    TextView titleView = (TextView) rowView.findViewById(R.id.channel_title);
	    TextView linkView = (TextView) rowView.findViewById(R.id.channel_link);
	    TextView unreadQtyView = (TextView) rowView.findViewById(R.id.channel_unreadQty);

	    // 4. Set the text for textView 
	    titleView.setText(rssChannel.get(position).getTitle());
	    linkView.setText(rssChannel.get(position).getLink());
	    unreadQtyView.setText(String.valueOf(rssChannel.get(position).getUnreadQuantity()));

	    // 5. retrn rowView
	    return rowView;
	}
	

}
