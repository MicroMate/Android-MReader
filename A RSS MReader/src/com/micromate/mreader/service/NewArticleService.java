package com.micromate.mreader.service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.micromate.mreader.FeedRssSaxParser;
import com.micromate.mreader.MainActivity;
import com.micromate.mreader.R;
import com.micromate.mreader.database.DBoperacje;
import com.micromate.mreader.database.Feed;

public class NewArticleService extends Service{

	private FeedRssSaxParser feedRssSaxParser;
	private DBoperacje baza;
	
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
			
			List<Feed> feed = new ArrayList<Feed>(); 
			feed = baza.readAllRssChannels();
			
			//checking whether an article is released
			for (Feed f: feed){		
			
				try {
					URL xmlUrl = new URL(f.getRssLink());
	        
					SAXParserFactory saxFactory = SAXParserFactory.newInstance();
					SAXParser parser = saxFactory.newSAXParser();
					XMLReader reader = parser.getXMLReader();
					
					//SAX Parser constructor for New Articel Checking (checking whether new articles)
					feedRssSaxParser = new FeedRssSaxParser(baza.getLatestArticleDateByID(f.get_id()),true);
					reader.setContentHandler(feedRssSaxParser);
				
					InputSource inputSource = new InputSource(xmlUrl.openStream());
					reader.parse(inputSource);	
		
			
				}catch(ParserConfigurationException pce){
					Log.e(LOG_TAG,"ParserConfigurationException"+pce);
				}catch(SAXException se){
					Log.e(LOG_TAG,"SAXException"+se);
				}catch(IOException e){
					Log.e(LOG_TAG,"IOException"+e);
				}
			
				// if is new article break loop
				if (feedRssSaxParser.isNewArticle()){
					break;
				}
			}
				
			return feedRssSaxParser.isNewArticle();
			
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			if (result) {
				Toast.makeText(getApplicationContext(), "NEW ARTICLE",Toast.LENGTH_SHORT).show();
				createNewArticleNotification();
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
        	.setContentText("Click here to update")
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
