package com.micromate.mreader;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.micromate.mreader.database.Article;
import com.micromate.mreader.database.DBoperacje;
import com.micromate.mreader.dialogs.DeleteArticleDialogFragment;

public class ArticlesListFragment extends Fragment {

	private	ListView listView;	
	private List<Article> articles; 
	private ArticlesListAdapter articlesListAdapter;
	private DBoperacje baza;
	private int rssChannelID;
	private Bundle bundle;
	private int position; //position of the feed list (navigation list)

	//on result code
	public static final int CODE_ARTICLE_ACTIVITY = 0;

	public ArticlesListFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_articles_list, container, false);
        		
		listView = (ListView)rootView.findViewById(R.id.articles_list);
		
		articles = new ArrayList<Article>();
		baza = new DBoperacje(getActivity());         
		
		//getting value from MainActivity(navigation drawer list)
		bundle = this.getArguments();
		rssChannelID = bundle.getInt("FEED_ID", 0); 		
		position = bundle.getInt("LIST_POSITION",0);
		
		switch(position) {
		case 0: //all articles
			articles = baza.getAllArticle();
			break;
		case 1:
			articles = baza.getAllFavoriteArticle();
			break;
		
		default: 
			//getting all articles of specified feed from Data Base
			articles = baza.getAllArticlesByID(rssChannelID);	
		}
		
    	/*getting all articles of specified feed from Data Base*/
		//articles = baza.getAllArticlesByID(rssChannelID);		  
		articlesListAdapter = new ArticlesListAdapter(getActivity(), R.layout.fragment_articles_list, articles, baza);
	    listView.setAdapter(articlesListAdapter);
		
	    
	    
	    /*select on Article click*/
		listView.setOnItemClickListener(new OnItemClickListener() {   
	 
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int pos, long id) {
			
				//setting an article as read article
				baza.setArticleRead(articles.get(pos).getUrl());
								
				Intent intent = new Intent(getActivity(), ArticleActivity.class);
				intent.putExtra("ARTICLE_TITLE", articles.get(pos).getTitle());
				intent.putExtra("ARTICLE_DESC", articles.get(pos).getDescription());
				intent.putExtra("ARTICLE_LINK", articles.get(pos).getUrl());
				startActivityForResult(intent, CODE_ARTICLE_ACTIVITY);
			}
		});
		
		//on long Article click open dialog and delete article
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int pos, long arg3) {
				// TODO Auto-generated method stub
					
				String articleURL = articles.get(pos).getUrl();
				String articleTitle = articles.get(pos).getTitle();
				
				//delete Article dialog
				DeleteArticleDialogFragment deleteArticle = new DeleteArticleDialogFragment();
				deleteArticle.setData(articleTitle, articleURL);
				deleteArticle.show(getFragmentManager(), "delete article TAG");		
				
				Log.d("ArticleListFragment", "After Dialog");
				return true;
			}
			
		});
	    
	    return rootView;
	};

	// on activity result - when back button is pressed in child activity (ArticleActivity)
 	@Override
 	public void onActivityResult(int requestCode, int resultCode, Intent data) {
 	
  		// Check which request we're responding to
 	    if (requestCode == CODE_ARTICLE_ACTIVITY) {   //if ArticleActivity  
 	    	
 	    	//refreshing quantity of unread articles on navigation drawer list
 	    	((MainActivity) getActivity()).updateFeedListView(); 
 	    	
 	    	//updating Articles list view
 	    	updateArticleListView();
 	    }
 	}
 	
 	
 	public void updateArticleListView(){	
 		
 		articles.clear();
 		
 		switch(position) {
		case 0: //all articles
			articles.addAll(baza.getAllArticle());
			break;
		case 1:
			articles.addAll(baza.getAllFavoriteArticle());
			break;
		
		default: 
			//getting all articles of specified feed from Data Base
			articles.addAll(baza.getAllArticlesByID(rssChannelID));	
		}
 			
		articlesListAdapter.notifyDataSetChanged();
 	}
		
}
