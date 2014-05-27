/*Adding RSS Channel to Database*/

package com.micromate.mreader;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jsoup.Jsoup;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.micromate.mreader.database.Article;
import com.micromate.mreader.database.DBoperacje;
import com.micromate.mreader.database.Feed;

public class FeedAddRssFragment extends Fragment {
	
	public FeedAddRssFragment(){}
	
	private EditText editText;
	private TextView textView;
	private Button button;
	
	private FeedRssSaxParser rssSaxHandler;
	private DBoperacje baza;
	
	private ProgressDialog pDialog;
	private String rssURL;
	
	private static final String LOG_TAG ="AddFeedFragment";
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_add_feed, container, false);
        
        editText = (EditText)rootView.findViewById(R.id.editText1);
		textView = (TextView)rootView.findViewById(R.id.textView2);
		button = (Button)rootView.findViewById(R.id.button1);
		        
		
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				String url = editText.getText().toString();
				
				if (url.length() > 0) {
					String urlPattern = "^http(s{0,1})://[a-zA-Z0-9_/\\-\\.]+\\.([A-Za-z/]{2,5})[a-zA-Z0-9_/\\&\\?\\=\\-\\.\\~\\%]*";
					if (url.matches(urlPattern)) {
						// valid url
						MyTask myTask = new MyTask();
						myTask.execute(url);
					} else {
						// URL not valid
						textView.setText("Please enter a valid url");
					}
				} else {
					// Please enter url
					textView.setText("Please enter website url");
				}
						
			}
		});
		
         
        return rootView;
        
    }
	

	private String getRSSLinkFromURL(String url) {
		// RSS url
		String rss_url = null;

		try {
			// Using JSoup library to parse the html source code
			org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
			// finding rss links which are having link[type=application/rss+xml]
			org.jsoup.select.Elements links = doc
					.select("link[type=application/rss+xml]");
			
			Log.d("No of RSS links found", " " + links.size());
			
			// check if urls found or not
			if (links.size() > 0) {
				rss_url = links.get(0).attr("href").toString();
			} else {
				// finding rss links which are having link[type=application/rss+xml]
				org.jsoup.select.Elements links1 = doc
						.select("link[type=application/atom+xml]");
				if(links1.size() > 0){
					rss_url = links1.get(0).attr("href").toString();	
				}
			}
			
		} 
		catch (IOException e) {
			Log.e(LOG_TAG,"Unable to resolve host: tu dodac nazwe "+e);
			e.printStackTrace();
		}

		rssURL = rss_url;   // DO POPRAWY !!!
		// returing RSS url
		return rss_url;
	}
	
	
	
	
	private int newChanelSaxParser(String rssURL){ 
		
		Log.i(LOG_TAG,"newChannelSaxParser()");
		
		
		int exceptionNr = 0;
		
		try {
			URL xmlUrl = new URL(rssURL);
    
			SAXParserFactory saxFactory = SAXParserFactory.newInstance();
			SAXParser parser = saxFactory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
        
			rssSaxHandler = new FeedRssSaxParser();
			reader.setContentHandler(rssSaxHandler);
		
			InputSource inputSource = new InputSource(xmlUrl.openStream());
			reader.parse(inputSource);	

		} catch (MalformedURLException mue) {	
				Log.e(LOG_TAG,"newChannelSaxParser - MalformedURLException");
				mue.printStackTrace();
		 
		}catch(ParserConfigurationException pce){
			Log.e(LOG_TAG,"ParserConfigurationException"+pce);
		}catch(SAXException se){
			Log.e(LOG_TAG,"SAXException"+se);
		}catch(IOException e){
			Log.e(LOG_TAG,"IOException"+e);
		}
		
		return exceptionNr;
	}
	
	
	
	private class MyTask extends AsyncTask<String, Void, Integer> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Fetching RSS Information ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		
		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			int exception = 1;
			
			String rssURL = getRSSLinkFromURL(params[0]);
			
			if (rssURL != null)
				exception = newChanelSaxParser(rssURL);
			
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
				baza = new DBoperacje(getActivity());
				Feed rssChannel = new Feed();
				List<Article> articles = new ArrayList<Article>();
				String title;
				long channel_id;
				
				rssChannel = rssSaxHandler.getFeed();
				rssChannel.setRssLink(rssURL);
				channel_id = baza.addRssChannel(rssChannel); //Adding channel to DB and Getting DB channel id
				
				articles = rssSaxHandler.getArticles();
				//Adding RSS channel articles by channel ID
				for (Article a: articles){
					baza.addArticleByID(a, channel_id);
				}
				
				title = rssSaxHandler.getFeed().getTitle();
				textView.setText(title);				
			}
			else
				textView.setText("Unable to resolve host");
			
		}
		
		
		
	}
	
	
}
