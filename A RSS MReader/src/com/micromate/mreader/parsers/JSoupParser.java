package com.micromate.mreader.parsers;

import java.io.IOException;

import org.jsoup.Jsoup;

import android.util.Log;

public class JSoupParser {
	
	private static final String LOG_TAG = "JSoupParser";
	
	public String getRSSLinkFromURL(String url) {
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
			Log.e(LOG_TAG,"JSOUP - Unable to resolve host: tu dodac nazwe "+e);
			e.printStackTrace();
		}
		
		return rss_url;
	}
	
}
