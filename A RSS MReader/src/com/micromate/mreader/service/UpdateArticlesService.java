/*
 * This Service Updating Articles using ROME parser
 */
package com.micromate.mreader.service;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.micromate.mreader.DeleteOldArticles;
import com.micromate.mreader.MainActivity;
import com.micromate.mreader.R;
import com.micromate.mreader.SettingActivity;
import com.micromate.mreader.database.Article;
import com.micromate.mreader.database.DBoperacje;
import com.micromate.mreader.database.Feed;
import com.micromate.mreader.parsers.FeedRomeParser;

public class UpdateArticlesService extends Service {
	private FeedRomeParser feedRomeParser;
	private DBoperacje baza;
	private DeleteOldArticles deleteOldArticles;
	
	private static final String	LOG_TAG = "MyService";

	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i(LOG_TAG,"Service Created.");
		
		baza = new DBoperacje(this);
	}
	
	/*Called by the system every time a client explicitly start the service calling startService(intent)*/
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i(LOG_TAG,"Service Started.");
		//Toast.makeText(getApplicationContext(), "Service Started",Toast.LENGTH_SHORT).show();
		
		if (baza==null) {
			baza = new DBoperacje(this);
		}
				
		MyTask myTask = new MyTask();
		myTask.execute();
		
		return Service.START_NOT_STICKY;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(LOG_TAG,"Service Destroyed.");
	}

	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		//Log.i(LOG_TAG,"Service Bind.");
		
		return null;
	}
	
	
	/*New Article Checking Task*/ 
	private class MyTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			boolean newArticle = false;
			
			List<Feed> feed = new ArrayList<Feed>(); 
			feed = baza.readAllRssChannels();
			
			// Parser ROME - Updating all articles:
			for (Feed f: feed){			
				Log.i(LOG_TAG,"Updating feed: "+f.getRssLink());
		
				feedRomeParser = new FeedRomeParser();
				if (feedRomeParser.getNewArticles(f.getRssLink(), baza.getNewestArticleDateByID(f.get_id())))
				{
					//Adding new Articles to Database			
					List<Article> articles = new ArrayList<Article>();
					articles = feedRomeParser.getArticles();
					//Adding RSS channel articles by channel ID
					for (Article a: articles){
						baza.addArticleByID(a, f.get_id());
					}
					newArticle = true;
				}
			}	
			return newArticle;	//if new article return true	
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			boolean notiEnable = sharedPreferences.getBoolean(SettingActivity.KEY_NOTIFICATIONS_ENABLED, false);		
			boolean isEnabledDelete = sharedPreferences.getBoolean(SettingActivity.KEY_DELETE_ENABLED, false);	
			
			if (result && notiEnable) { //if result/new article = true create notification
				createNewArticleNotification();
				
				//deleting the oldest articles except of the favorite articles
				if (isEnabledDelete){
					deleteOldArticles = new DeleteOldArticles(getApplicationContext(), baza);
					deleteOldArticles.startDeleting();
			
				}
			}
		}
			
	}
	
	//Notification
	private void createNewArticleNotification() {
		    
		// Prepare intent which is triggered if the notification is selected
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("NEW_ARTICLE", true);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0); 
		//PendingIntent - intencja w toku (w oczekiwaniu)		
				
		Notification noti = new Notification.Builder(this)
        	.setContentTitle("New Article")
        	.setContentText("Click here to read new article")
        	.setSmallIcon(R.drawable.ic_mreader)
        	//.setLargeIcon(aBitmap)
        	.setContentIntent(pIntent)
        	.build();  //requires min API level 16
		
		// hide the notification after its selected
		noti.flags |= Notification.FLAG_AUTO_CANCEL;
		
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		//Show Notification in the status bar
		notificationManager.notify(0, noti);
		
	}
}
