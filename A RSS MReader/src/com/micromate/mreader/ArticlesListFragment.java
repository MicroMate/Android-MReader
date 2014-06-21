package com.micromate.mreader;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.micromate.mreader.database.Article;
import com.micromate.mreader.database.DBoperacje;

public class ArticlesListFragment extends Fragment {

	private	ListView listView;	
	private List<Article> articles; 
	private ArticlesListAdapter articlesListAdapter;
	private DBoperacje baza;
	private int rssChannelID;
	private Bundle bundle;
	private String feedTitle;
	
	
	public ArticlesListFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_articles_list, container, false);
        		
		listView = (ListView)rootView.findViewById(R.id.articles_list);
		
		articles = new ArrayList<Article>();
		baza = new DBoperacje(getActivity());         
	
		
		bundle = this.getArguments();
		rssChannelID = bundle.getInt("FEED_ID", 0);
		feedTitle = bundle.getString("FEED_TITLE", "Title");
			
		//setting title in ActionBar
		getActivity().getActionBar().setTitle(feedTitle);
		getActivity().setTitle(feedTitle); //to remember after close naviDrawer 
		
    	/*getting all RSS channels from Data Base*/
		articles = baza.getAllArticlesByID(rssChannelID);		  
		articlesListAdapter = new ArticlesListAdapter(getActivity(), R.layout.fragment_articles_list, articles);
	    listView.setAdapter(articlesListAdapter);
		
	    
	    
	    /*select on item click*/
		listView.setOnItemClickListener(new OnItemClickListener() {   
	 
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int pos, long id) {
			
				baza.setArticleRead(articles.get(pos).getUrl());
				
				Fragment fragment = new ArticleFragment();
				/**/
				//bundle = new Bundle(); //instnced in fragment FeedListFragment (passing feed link to ArticleFragment)
				bundle.putString("ARTICLE_TITLE", articles.get(pos).getTitle());
				bundle.putString("ARTICLE_DESC", articles.get(pos).getDescription());
				bundle.putString("ARTICLE_LINK", articles.get(pos).getUrl());
				fragment.setArguments(bundle);
				
				FragmentManager fragmentManager = getFragmentManager();
				FragmentTransaction transaction = fragmentManager.beginTransaction();
				transaction.replace(R.id.frame_container, fragment);
				transaction.addToBackStack(null); //
				transaction.commit();					
			}
		});
		
		//on long click open dialog and delete article
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
			    		
			    		/*updating list view*/
			    		articles.clear();
			    		articles.addAll(baza.getAllArticlesByID(rssChannelID));	
			    		articlesListAdapter.notifyDataSetChanged();
			            	   
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
		
}
