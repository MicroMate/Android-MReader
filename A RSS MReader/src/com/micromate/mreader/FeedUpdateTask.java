package com.micromate.mreader;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.micromate.mreader.database.Article;
import com.micromate.mreader.database.DBoperacje;
import com.micromate.mreader.database.Feed;

public class FeedUpdateTask extends AsyncTask<Void, Void, Boolean> {
	
	private Context context;
	private DBoperacje baza;
	//private FeedRssSaxParser feedRssSaxParser;
	private FeedRomeParser feedRomeParser;
	private ProgressDialog pDialog;
	
	
	private static final String LOG_TAG = "FeedUpdateTask";
	
	public FeedUpdateTask(Context context) {
		this.context = context; 
		baza = new DBoperacje(context);
	}

	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(context);
		pDialog.setMessage("Updating RSS Information ...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
	}
	
	
	@Override
	protected Boolean doInBackground(Void... args) {
		// TODO Auto-generated method stub
		
		boolean newArticle = false;
		
		List<Feed> feed = new ArrayList<Feed>(); 
		feed = baza.readAllRssChannels();
		
		// Parser ROME
		for (Feed f: feed){			
			Log.i(LOG_TAG,"Updating feed: "+f.getRssLink());
	
			feedRomeParser = new FeedRomeParser();
			if (feedRomeParser.getNewArticles(f.getRssLink(), baza.getLatestArticleDateByID(f.get_id())))
			{
				//Adding new Articles to Database			
				List<Article> articles = new ArrayList<Article>();
				articles = feedRomeParser.getArticles();
				//Adding RSS channel articles by channel ID
				for (Article a: articles){
					baza.addArticleByID(a, f.get_id());
				}
				newArticle = true;
				Log.i(LOG_TAG,"Added new Articles");
				
			}
		}	
		
		return newArticle;
		
//		Parser SAX
// 		int exception = 0;  //task zwraca¸Êtyp wyjˆtku
//		
//		for (Feed f: feed){
//		
//			Log.i(LOG_TAG,"Updating feed: "+f.getRssLink());
//		
//			try {
//				URL xmlUrl = new URL(f.getRssLink());
//    
//				SAXParserFactory saxFactory = SAXParserFactory.newInstance();
//				SAXParser parser = saxFactory.newSAXParser();
//				XMLReader reader = parser.getXMLReader();
//				//SAX Parser constructor for update DB (reading only articles)
//				feedRssSaxParser = new FeedRssSaxParser(baza.getLatestArticleDateByID(f.get_id()));
//				reader.setContentHandler(feedRssSaxParser);
//		
//				InputSource inputSource = new InputSource(xmlUrl.openStream());
//				reader.parse(inputSource);	
//
//			} catch (MalformedURLException mue) {	
//				Log.e(LOG_TAG,"newChannelSaxParser - MalformedURLException");
//				mue.printStackTrace();
//				exception = 1;
//			}catch(ParserConfigurationException pce){
//				Log.e(LOG_TAG,"ParserConfigurationException"+pce);
//				exception = 2;
//			}catch(SAXException se){
//				Log.e(LOG_TAG,"SAXException"+se);
//				exception = 3;
//			}catch(IOException e){
//				Log.e(LOG_TAG,"IOException"+e);
//				exception = 4;
//			}
//			
//			
//			//Adding new Articles to Database
//			List<Article> articles = new ArrayList<Article>();	
//			articles = feedRssSaxParser.getArticles();
//			//Adding RSS channel articles by channel ID
//			for (Article a: articles){
//				baza.addArticleByID(a, f.get_id());
//			}		
//			
//		
//		}	
//		return exception;
	
	
	}

	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		Log.i(LOG_TAG,"KONIEC WATKU");
		
		pDialog.dismiss();
		
		/*if no exception*/
		if (result)
			Toast.makeText(context, "Aticles Updated", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(context, "No New Articles", Toast.LENGTH_SHORT).show();
		}
		
	
}
