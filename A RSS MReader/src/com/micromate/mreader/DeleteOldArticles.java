package com.micromate.mreader;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.micromate.mreader.database.Article;
import com.micromate.mreader.database.DBoperacje;
import com.micromate.mreader.database.Feed;

public class DeleteOldArticles {
	
	private Context context;
	private DBoperacje baza;
	
	private static final String LOG_TAG = "DeleteOldArticles class";
	
	public DeleteOldArticles(Context context, DBoperacje baza) {
		this.context = context;
		this.baza = baza;

	}
	
	
	// Auto delete the oldest articles. 
	// Quantity of articles will be kept is specified by the user in application settings
	public void startDeleting(){
		
		List<Feed> feeds;
		List<Article> articles;
		int notMarkedArticles; //not favorite articles
		int articlesToDelete; //q-ty of articles to delete
		int articlesToKept; //q-ty of articles will be kept except favorite articles
		String articleURL;
		
		// getting feeds
		feeds = new ArrayList<Feed>(); 
		feeds = baza.readAllRssChannels();
		
		//  retrieve preferences values
		SharedPreferences sharedPreferences;
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		articlesToKept = Integer.valueOf(sharedPreferences.getString(SettingActivity.KEY_DELETE_QTY_KEPT, "20"));	
		
		for (Feed f: feeds){
			articles = new ArrayList<Article>();
  			articles = baza.getAllArticlesByID(f.get_id());
  			notMarkedArticles = 0;
  			for(Article a: articles){
  				if (a.getIntFavorite() != 1){   // if is not favorite article can be deleted  (article is not star marked)
  					notMarkedArticles++;			
  				}
  			}
  			// deleting articles
  			articlesToDelete = notMarkedArticles - articlesToKept;
  			
  			Log.d(LOG_TAG, "articles not checked: " +notMarkedArticles+" limit: "+articlesToKept);
  				
  			while (articlesToDelete > 0){
  				Log.d(LOG_TAG, "deleting article");
  				articleURL = baza.getOldestArticleUrlByID(f.get_id());
  				baza.deleteArticle(articleURL);
  				articlesToDelete--;
  			}	
  				
		}
			
	}
	
	
}
