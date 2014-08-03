package com.micromate.mreader;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.micromate.mreader.database.Article;
import com.micromate.mreader.database.DBoperacje;
import com.micromate.mreader.database.Feed;
import com.micromate.mreader.parsers.FeedRomeParser;
import com.micromate.mreader.parsers.JSoupParser;

public class FeedAddRssActivity extends Activity {

	private EditText editText;
	private TextView textView;
	private Button button;
	
	//private FeedRssSaxParser feedRssSaxParser;
	private FeedRomeParser feedRomeParser;
	private JSoupParser jSoupParser;
	private DBoperacje baza;
	
	private ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_feed);

		//Enable the app icon as an Up button (Home button)   
		getActionBar().setDisplayHomeAsUpEnabled(true);

		
	    editText = (EditText)findViewById(R.id.editText1);
	    textView = (TextView)findViewById(R.id.textView2);
	    button = (Button)findViewById(R.id.button1);
		
	    //setting cursor on the end of text
	    editText.setSelection(editText.length());
			
	    button.setOnClickListener(new View.OnClickListener() {
				
	    	@Override
	    	public void onClick(View arg0) {
	    		// TODO Auto-generated method stub
				
	    		//hiding keyboard 
	    	    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	    	    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	    		
	    		String url = editText.getText().toString();
					
	    		if (url.length() > 0) {
	    			String urlPattern = "^https?://.*$";     //on url start: http:// or https// 
	    			if (url.matches(urlPattern)) {
	    				// valid url
	    				MyTask myTask = new MyTask();
	    				myTask.execute(url);
	    			} else {
	    				// URL not valid
	    				//textView.setText("Please enter a valid url");
	    				Toast.makeText(getApplicationContext(), "Please enter a valid url", Toast.LENGTH_SHORT).show();
	    			}
	    		} 
	    		else {  //empty editText
	    			// Please enter url
	    			//textView.setText("Please enter website url");
	    			Toast.makeText(getApplicationContext(), "Please enter website url", Toast.LENGTH_SHORT).show();
	    		}
							
	    	}
	    });

	}
	
	private class MyTask extends AsyncTask<String, Void, Integer> {
		
		private String rssURL;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(FeedAddRssActivity.this);
			pDialog.setMessage("Fetching RSS Information ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		
		@Override
		protected Integer doInBackground(String... inputURL) {
			// TODO Auto-generated method stub
			
			int exception = 1;
			
			//ROME Parser
			feedRomeParser = new FeedRomeParser();
		   
			exception = feedRomeParser.getAddingFeed(inputURL[0]);
		    rssURL = inputURL[0]; //for textView
		    
		    if (exception!=0) {  //if input URL is not RSS/Atom URL 
		    	jSoupParser = new JSoupParser();
				rssURL = jSoupParser.getRSSLinkFromURL(inputURL[0]);  //get feed URL from webpage Link 
				if (rssURL != null)
			  	exception = feedRomeParser.getAddingFeed(rssURL);
			}
			
			return exception;
		}

		@Override
		protected void onPostExecute(Integer exception) {
			// TODO Auto-generated method stub
			super.onPostExecute(exception);
			
			pDialog.dismiss();
			
			/*if no exception*/
			if (exception == 0){
				
				/*Adding new RSS Channel and Articles to Database*/
				baza = new DBoperacje(getApplicationContext());
				Feed rssChannel = new Feed();
				List<Article> articles = new ArrayList<Article>();
				String title;
				long channel_id;
				
				rssChannel = feedRomeParser.getFeed();
				rssChannel.setRssLink(rssURL); //adding feed url
				//Log.i(LOG_TAG,"FEED RSS_URL: "+ rssURL);     
				channel_id = baza.addRssChannel(rssChannel); //Adding channel to DB and Getting DB channel id
				
				articles = feedRomeParser.getArticles();
				//Adding RSS channel articles by channel ID
				for (Article a: articles){
					baza.addArticleByID(a, channel_id);
				}
				
				title = feedRomeParser.getFeed().getTitle();
				textView.setText("New Feed Added\nTitle: "+title+"\nFeed link: "+rssURL);				
			}
			else
				//textView.setText("Unable to resolve host");
				Toast.makeText(getApplicationContext(), "Unable to resolve host", Toast.LENGTH_SHORT).show();		
		}		
	}
	
	//SAX Parser
//	private int newChanelSaxParser(String rssURL){ 
//		
//		Log.i(LOG_TAG,"newChannelSaxParser()");
//		
//		
//		int exceptionNr = 0;
//		
//		try {
//			URL xmlUrl = new URL(rssURL);
//    
//			SAXParserFactory saxFactory = SAXParserFactory.newInstance();
//			SAXParser parser = saxFactory.newSAXParser();
//			XMLReader reader = parser.getXMLReader();
//        
//			rssSaxHandler = new FeedRssSaxParser();
//			reader.setContentHandler(rssSaxHandler);
//		
//			InputSource inputSource = new InputSource(xmlUrl.openStream());
//			reader.parse(inputSource);	
//
//		} catch (MalformedURLException mue) {	
//				Log.e(LOG_TAG,"newChannelSaxParser - MalformedURLException");
//				mue.printStackTrace();
//		 
//		}catch(ParserConfigurationException pce){
//			Log.e(LOG_TAG,"ParserConfigurationException"+pce);
//		}catch(SAXException se){
//			Log.e(LOG_TAG,"SAXException"+se);
//		}catch(IOException e){
//			Log.e(LOG_TAG,"IOException"+e);
//		}
//		
//		return exceptionNr;
//	}
	
}