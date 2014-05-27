package com.micromate.mreader;

import java.util.ArrayList;
import java.util.List;

import com.micromate.mreader.database.Article;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ArticlesListAdapter extends ArrayAdapter<Article>{

	private Context context;
	
	private List<Article> articles = new ArrayList<Article>();
	
	public ArticlesListAdapter(Context context, int textViewResourceId, List<Article> articles) {
		super(context, textViewResourceId, articles);
		// TODO Auto-generated constructor stub
		  this.context = context;
		  this.articles = articles;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// 1. Create inflater 
	    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	    // 2. Get rowView from inflater
	    View rowView = inflater.inflate(R.layout.articles_list_row, parent, false);

	    // 3. Get the two text view from the rowView
	    TextView labelView = (TextView) rowView.findViewById(R.id.title);
	    TextView valueView = (TextView) rowView.findViewById(R.id.date);

	    // 4. Set the text for textView 
	    labelView.setText(articles.get(position).getTitle());
	    valueView.setText(articles.get(position).getListDate());

	    // 5. retrn rowView
	    return rowView;
	}
	

}
