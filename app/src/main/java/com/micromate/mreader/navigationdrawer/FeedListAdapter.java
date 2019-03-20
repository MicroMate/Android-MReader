package com.micromate.mreader.navigationdrawer;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.micromate.mreader.R;
import com.micromate.mreader.database.Feed;

public class FeedListAdapter extends BaseAdapter{

	private Context context;
	private List<Feed> rssChannel; 
	
	public FeedListAdapter(Context context, List<Feed> articles) {
		  this.context = context;
		  this.rssChannel = articles;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return rssChannel.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return rssChannel.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// 1. Create inflater 
	    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	    // 2. Get rowView from inflater
	    View rowView = inflater.inflate(R.layout.drawer_feed_list_row, parent, false);

	    // 3. Get the two text view from the rowView
	    TextView titleView = (TextView) rowView.findViewById(R.id.channel_title);
	    TextView linkView = (TextView) rowView.findViewById(R.id.channel_link);
	    TextView unreadQtyView = (TextView) rowView.findViewById(R.id.channel_unreadQty);

	    // 4. Set the text for textView 
	    titleView.setText(rssChannel.get(position).getTitle());
	    linkView.setText(rssChannel.get(position).getLink());
	    unreadQtyView.setText(String.valueOf(rssChannel.get(position).getUnreadQuantity()));

	    if (position == 0 || position == 1)
	    	titleView.setTextColor((context.getResources().getColor(R.color.feed_list_all_favorite)));
	    
	    // 5. retrn rowView
	    return rowView;
	}

}
