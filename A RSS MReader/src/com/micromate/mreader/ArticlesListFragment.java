package com.micromate.mreader;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.micromate.mreader.database.Article;
import com.micromate.mreader.database.DBoperacje;

public class ArticlesListFragment extends Fragment {

	private	ListView listView;	
	private List<Article> articles; 
	private ArticlesListAdapter articlesListAdapter;
	private DBoperacje baza;
	private int rssChannelID;
	private Bundle bundle;

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
		
    	/*getting all RSS channels from Data Base*/
		articles = baza.getAllArticlesByID(rssChannelID);		  
		articlesListAdapter = new ArticlesListAdapter(getActivity(), R.layout.fragment_articles_list, articles);
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
					
				Vibrator vibrator;
				vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(100);
				
				//AlertDialog builder instance
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			    builder
			    	.setTitle(articles.get(pos).getTitle())
			    	.setItems(R.array.feedlist_option_array, new DialogInterface.OnClickListener() {
			    	public void onClick(DialogInterface dialog, int which) {
			    		// The 'which' argument contains the index position of the selected item
			    		
			    		baza.deleteArticle(articles.get(pos).getUrl());
			    		
			    		Toast.makeText(getActivity(), "Article deleted", Toast.LENGTH_SHORT).show();
			    		
			    		//updating Articles list view
//			    		articles.clear();
//			    		articles.addAll(baza.getAllArticlesByID(rssChannelID));	
//			    		articlesListAdapter.notifyDataSetChanged();
			    		updateArticleListView();
			            	   
			    	}
			    });
			    //Create the AlertDialog
			    AlertDialog dialog = builder.create();
			    //Show the AlertDialog
			    dialog.show();
				
				return false;
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
		articles.addAll(baza.getAllArticlesByID(rssChannelID));	
		articlesListAdapter.notifyDataSetChanged();
 	}
		
}
