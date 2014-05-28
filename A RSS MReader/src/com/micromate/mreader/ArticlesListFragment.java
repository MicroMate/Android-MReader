package com.micromate.mreader;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
	
	
	public ArticlesListFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_articles_list, container, false);
        		
		listView = (ListView)rootView.findViewById(R.id.articles_list);
		
		articles = new ArrayList<Article>();
		baza = new DBoperacje(getActivity());         
	
		bundle = this.getArguments();
		rssChannelID = bundle.getInt("CHANNEL_ID", 0);
		
    	/*getting all RSS channels from Data Base*/
		articles = baza.getAllArticlesByID(rssChannelID);		  
		articlesListAdapter = new ArticlesListAdapter(getActivity(), R.layout.fragment_articles_list, articles);
	    listView.setAdapter(articlesListAdapter);
		
	    
	    
	    /*select on item click*/
		listView.setOnItemClickListener(new OnItemClickListener() {   
	 
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int pos, long id) {
			
				
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
	        
	    
	    return rootView;
	};
		
}
