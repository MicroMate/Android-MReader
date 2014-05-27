package com.micromate.mreader;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.micromate.mreader.database.Article;
import com.micromate.mreader.database.DBoperacje;

public class ArticlesListFragment extends Fragment {

	private	ListView listView;	
	private List<Article> articles; 
	private ArticlesListAdapter articlesListAdapter;
	private DBoperacje baza;
	int rssChannelID;
	
	
	public ArticlesListFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_articles, container, false);
        		
		listView = (ListView)rootView.findViewById(R.id.articles_list);
		
		articles = new ArrayList<Article>();
		baza = new DBoperacje(getActivity());         
	
		Bundle bundle = this.getArguments();
		rssChannelID = bundle.getInt("CHANNEL_ID", 0);
		
    	/*getting all RSS channels from Data Base*/
		articles = baza.getAllArticlesByID(rssChannelID);		  
		articlesListAdapter = new ArticlesListAdapter(getActivity(), R.layout.fragment_articles, articles);
	    listView.setAdapter(articlesListAdapter);
		
	    return rootView;
	};
		
}
