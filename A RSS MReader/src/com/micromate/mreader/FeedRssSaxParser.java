package com.micromate.mreader;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.micromate.mreader.database.*;

import android.util.Log;

public class FeedRssSaxParser extends DefaultHandler {
	
	private Feed feed;  //RSS Channel
	
	private Article article;
	private List<Article> articles = new ArrayList<Article>(); 
		
	private StringBuilder builderText; //dla przechwytywania znak—w w metodzie charakters
	
	//flaga dla przechwytywania aktualnej pozycji w dokumencie
	//flagi pozycji musza byc zastosowane gdy dokument posiada znaczniki o tej samej nazwie
	private boolean inItem = false;     
	
	private boolean currentElement = false; //pomiedzy znacznikami moga byc znaki dlatego trzeba stosowac flage
	
	private boolean newFeed = false;
	//private boolean updateArticles = false;

	private String latestArticleDate ="0000-00-00 00:00:00";
	//private List<String> newArticles;
	
	private static final String LOG_TAG = "FeedRssSaxParser"; 
	
	/*Constructors*/
	public FeedRssSaxParser(){ //
		newFeed = true;
		feed = new Feed();
	}
	
	public FeedRssSaxParser(String latestArticleDate){ //
		this.latestArticleDate = latestArticleDate;
		//updateArticles = true;
	}
	
	
	public Feed getFeed() {
		return feed;
		
	}
	
	public List<Article> getArticles() {
		return articles;
	}
	
	//public List<String> getNewArticles() {
	//	return newArticles;
	//}
	
	/**/
	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
		
		builderText = new StringBuilder();
		//articles = new ArrayList<Article>();			
		
		//latestArticleDate = baza.getLatestArticleDate();
		//newArticles = new ArrayList<String>();
		
		Log.i(LOG_TAG, "startDOCUMENT");
	}
	
	
	/**/
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		super.startElement(uri, localName, qName, attributes);
		
		 currentElement = true;
		
		 if(localName.equalsIgnoreCase("item")) { 
			 	inItem = true;						//
	            article = new Article();			//
	            Log.i(LOG_TAG, "startElement - if in Item");
	     }
	}

	
	/**/
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
	
		//if (inItem == true)
		if(currentElement) {
			builderText.append(ch, start, length);     //
        	//Log.i(LOG_TAG, "characters - if currentElemnet");
		}
	}

	
	/**/
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
		
		currentElement = false;
		
		if(!inItem){
			if(newFeed) //if adding new RSS Channel
				if(localName.equalsIgnoreCase("title")) 
					feed.setTitle(builderText.toString().trim());
				else if(localName.equalsIgnoreCase("link"))
					feed.setLink(builderText.toString().trim());
				else if(localName.equalsIgnoreCase("description"))
					feed.setDescription(builderText.toString().trim());
		}
		
		else { 
	       
			if(localName.equalsIgnoreCase("title")) {
				article.setTitle(builderText.toString().trim());
				Log.i(LOG_TAG, "endElement - if TITLE");
			}	
			else if(localName.equalsIgnoreCase("link"))
				article.setUrl(builderText.toString().trim());
			else if(localName.equalsIgnoreCase("description"))
				article.setDescription(builderText.toString().trim());
			else if(localName.equals("pubDate"))
				article.setPubDate(builderText.toString());
			else if(localName.equals("category"))
				article.setCategory(builderText.toString());
		
			else if(localName.equalsIgnoreCase("item")) {  
				
				//
				if (latestArticleDate.compareTo(article.getDate()) < 0) { 
					Log.i(LOG_TAG, "Adding new article to list");
					articles.add(article);
					inItem = false;
				}
				else
					throw new SAXException(); //if no new article
				
			}	
		}
		
		builderText.setLength(0); 
	} 
	
	
}