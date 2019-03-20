/* 
 * Reading feeds RSS and Atom versions using ROME parser.
 * ROME is a set of RSS and Atom Utilities for Java.
 */
		
package com.micromate.mreader.parsers;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.util.Log;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndContent;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.XmlReader;
import com.micromate.mreader.database.Article;
import com.micromate.mreader.database.Feed;


public class FeedRomeParser {
	
	private Feed feed;  //RSS Channel
	private Article article;
	private List<Article> articles;
	private static final String LOG_TAG = "FeedRomeParser";

	public Feed getFeed() {
		return feed;
	}
	public void setFeed(Feed feed) {
		this.feed = feed;
	}
	public Article getArticle() {
		return article;
	}
	public void setArticle(Article article) {
		this.article = article;
	}
	public List<Article> getArticles() {
		return articles;
	}
	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}

	/*
	 * Following method reads two objects: Feed Data and Articles List Data of the feed.
	 */
	public int getAddingFeed(String url) {
		
		feed = new Feed();
		articles = new ArrayList<Article>();
						
		URL feedUrl;
		try {
			Log.d("DEBUG", "Entered:" + url);
			
			feedUrl = new URL(url); //create URL instance
			
			/* Read a syndication feed using Rome (All it takes the following 2 lines of code: */
			SyndFeedInput input = new SyndFeedInput();  			 //create input SyndFeedInput instance
			SyndFeed syndFeed = input.build(new XmlReader(feedUrl)); //build method returns feed SyndFeed instance
		
			feed.setTitle(syndFeed.getTitle());						// Feed Title
			feed.setDescription(syndFeed.getDescription());			// Feed Description
			feed.setLink(syndFeed.getLink());						// Feed Web Link
			//feed.setRssLink(syndFeed.getUri()); 					// Feed url, only Atom, link added in FeedAddTask
//			Log.i("AndroidRss","FEED LINK: "+ syndFeed.getLink());         
//			Log.i("AndroidRss","FEED RSS_URI: "+ syndFeed.getUri());     
			
			@SuppressWarnings("unchecked")
			List<SyndEntry> entries = syndFeed.getEntries();
						
			Iterator<SyndEntry> iterator = entries.listIterator();
			while (iterator.hasNext()) {
				article = new Article();
				
				SyndEntry entry = iterator.next();
				
				article.setFeed_title(syndFeed.getTitle());         //Feed Title
				article.setTitle(entry.getTitle());					//Article Title
				article.setUrl(entry.getUri());						//Article Web Link
				article.setPublishedDate(entry.getPublishedDate());	//Article Published Date
				//
				@SuppressWarnings("unchecked")
				List<SyndContent> contents = entry.getContents();		//Article Content
				if (!contents.isEmpty()){
					String value = contents.get(0).getValue();
					article.setDescription(value);
				}
				articles.add(article);				
				
			}
			return 0;		
		
		} 
		catch (Exception e) {
			e.printStackTrace();
			return 1;
		}		
	}
	
	/*
	 * Previous function (getAddingFeed) gets 2 objects Feed and Articles. Function return exception type (it can return more detailed exception).
	 * Following function gets one object Articles, return boolean true when appear new article.   
	 */
	public boolean getNewArticles(String url, String latestArticleDate) {
		
		articles = new ArrayList<Article>();
		boolean newArticle = false;
						
		URL feedUrl;
		try {
			Log.d("DEBUG", "Entered:" + url);
			
			feedUrl = new URL(url); //create URL instance
			
			/* Read a syndication feed using Rome (All it takes the following 2 lines of code: */
			SyndFeedInput input = new SyndFeedInput();  			 //create input SyndFeedInput instance
			SyndFeed syndFeed = input.build(new XmlReader(feedUrl)); //build method returns feed SyndFeed instance
			
			@SuppressWarnings("unchecked")
			List<SyndEntry> entries = syndFeed.getEntries();
						
			Iterator<SyndEntry> iterator = entries.listIterator();
			while (iterator.hasNext()) {
				article = new Article();
				
				SyndEntry ent = iterator.next();
				
			    Log.i(LOG_TAG, "Article latest date: "+latestArticleDate);
			      
				if (latestArticleDate.compareTo(parseToString(ent.getPublishedDate()))>=0){
				//if (latestDate.compareTo(ent.getPublishedDate()) >= 0){
					Log.i(LOG_TAG, "No new article, latest published article: "+parseToString(ent.getPublishedDate()));
					break;
				}
				
				article.setFeed_title(syndFeed.getTitle());         //Feed Title
				article.setPublishedDate(ent.getPublishedDate());	//Article Published Date
				article.setTitle(ent.getTitle());					//Article Title
				article.setUrl(ent.getUri());						//Article Web Link
				
				//
				@SuppressWarnings("unchecked")
				List<SyndContent> contents = ent.getContents();		//Article Content
				String value = contents.get(0).getValue();
				article.setDescription(value);
				
				Log.i(LOG_TAG, "Adding new article to list, date: "+article.getDate());
				articles.add(article);	
				newArticle = true;
				
			}
		
		} 
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return newArticle;		

	}
	
	
	//Parse a date from string to date format 
//	private Date parseToDate(String inputDate) {
//		
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.UK);
//		Date date = null;
//	    
//		try {
//			date = formatter.parse(inputDate);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			Log.w(LOG_TAG, "Problem with date parse from string to date format ");
//		}	  		
//		return date;
//	}
	
	//Parse a date from date to string format 
	public String parseToString(Date inputDate) {
		
		//my date format
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.UK);		
	    String strDate = "0000-00-00 00:00:00";
		
	    try {
			strDate =  dateFormat.format(inputDate);
		} catch (Exception e) {
			e.printStackTrace();
			Log.w(LOG_TAG,"Problem with date parse from date to string format");
		}	  		
		
	    return strDate;
	}
	
}
