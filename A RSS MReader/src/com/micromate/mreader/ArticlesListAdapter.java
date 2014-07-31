package com.micromate.mreader;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.micromate.mreader.database.Article;
import com.micromate.mreader.database.DBoperacje;

public class ArticlesListAdapter extends ArrayAdapter<Article>{

	private Context context;
	private DBoperacje baza;
	
	private List<Article> articles = new ArrayList<Article>();
	
	public ArticlesListAdapter(Context context, int textViewResourceId, List<Article> articles, DBoperacje baza) {
		super(context, textViewResourceId, articles);
		// TODO Auto-generated constructor stub
		  this.context = context;
		  this.articles = articles;
		  this.baza = baza;
	}

	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

	
		// 1. Create inflater 
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 2. Get rowView from inflater
		View rowView = inflater.inflate(R.layout.articles_list_row, parent, false);  
		//LayoutInflater inflator = ((MainActivity) context).getLayoutInflater();
		//rowView = inflator.inflate(R.layout.articles_list_row, null);
	        
	    // 3. Get the two text view from the rowView	        
		TextView labelView = (TextView) rowView.findViewById(R.id.title);
		TextView valueView = (TextView) rowView.findViewById(R.id.date);
		final ToggleButton starButton = (ToggleButton) rowView.findViewById(R.id.button_star);
	    	 
	    // 4. Set the text for textView 
	    labelView.setText(Html.fromHtml(articles.get(position).getTitle()));
	    valueView.setText(articles.get(position).getListDate());
	    
	    //if article was read change title and background colors
	    if(articles.get(position).getUnread() == 1){
	    	labelView.setTextColor(context.getResources().getColor(R.color.article_list_read));
	    	rowView.setBackgroundColor(context.getResources().getColor(R.color.article_list_read_bg));
	    }
	    		    
	    //checking favorite article
		starButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (starButton.isChecked()){
					Log.d("Adapter","Oznaczona pozycja: "+position);
					baza.setArticleFavorite(articles.get(position).getUrl());
				}
				else
					baza.setArticleUnfavorite(articles.get(position).getUrl());
				
				//update Article list view (if not updated article losing of checked state)
				((MainActivity) context).updateArticleListView();
				//update Feed List in navigation drawer (if is not update it will not counting unread favorite articles) 
				((MainActivity) context).updateFeedListView();
			}
		});
      
	    
	    //if favorite article 
	    if(articles.get(position).isFavorite()){
	    	starButton.setChecked(true);
	    }
	    else {
	    	starButton.setChecked(false);
	    }
	    
	    // 5. retrn rowView
	    return rowView;   
	}
}
