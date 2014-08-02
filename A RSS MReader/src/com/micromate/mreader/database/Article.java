package com.micromate.mreader.database;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;


public class Article {

	private long id;
	private long channel_id;
	private String feed_title;
	private String title ="title";
	private String description ="title";
	private String url;
	private String date;
	private Integer unread = 0;  //SQLite does not have a Boolean type.   0 = unread, 1 = read article 
	private boolean favorite = false; //	 false = article can be deleted,  true = don't delete article (marked with star), 
	
//	public Article(String title, String description, String url, String date) {
//		this.title = title;
//		this.description = description;
//		this.url = url;
//		this.date = date;
//	}

	public Article() {
		// TODO Auto-generated constructor stub
	}

	//Seters and Geters
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public long getChannel_id() {
		return channel_id;
	}

	public void setChannel_id(long channel_id) {
		this.channel_id = channel_id;
	}

	public String getFeed_title() {
		return feed_title;
	}

	public void setFeed_title(String feed_title) {
		this.feed_title = feed_title;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDate() {
		return date;
	}
	
	public void setDate(String date){
		this.date = date;
	}
	
	public Integer getUnread() {
		return unread;
	}

	public void setUnread(Integer unread) {
		this.unread = unread;
	}
	
	//for sqlite
	public Integer getIntFavorite() {
		if (favorite)
			return 1;
		else
			return 0;
	}
	//for sqlite
	public void setIntFavorite(Integer favorite) {
		if (favorite == 1)		
			this.favorite = true;
		else
			this.favorite = false;
	}	

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	public void setPubDate(String pubDate) {
		//wzor daty z pliku xml
		SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ZZZZZ",Locale.UK);
		//moj wzor daty
		SimpleDateFormat mojformater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.UK);
		
		Date date = null;
	    String strDate = "0000-00-00 00:00:00";
		try {
			date = formatter.parse(pubDate);
			strDate =  mojformater.format(date);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.w("ArticleDateParser",
				    "Wyst�pi� problem z konwersj� pubDate: " + e.toString());
		}	  
		
		this.date = strDate;
	}
	

	//W�asny Format Daty i Godziny, dla wyswietlania na liscie
	public String getListDate() {
		//wzor daty z pliku xml
		//SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ZZZZZ",Locale.UK);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.UK);
		//moj wzor daty
		SimpleDateFormat mojformater = new SimpleDateFormat("dd-MM-yy   HH:mm:ss",Locale.UK);
	    
		Date date = null;
	    String strDate = "0000-00-00 00:00:00";
		try {
			date = formatter.parse(this.date);
			strDate =  mojformater.format(date);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.w("ArticleDateParser",
				    "Wyst�pi� problem z konwersj� daty: " + e.toString());
		}	  
		
		return strDate;
	}
	
	//
	public void setPublishedDate(Date inputDate) {
		//wzor daty z pliku xml
		//SimpleDateFormat formatter = new SimpleDateFormat("E MMM dd hh:mm:ss Z yyyy",Locale.UK);
		//moj wzor daty
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.UK);
		
		//Date date = null;
	    String strDate = "0000-00-00 00:00:00";
		try {
			//date = formatter.parse(pubDate);
			strDate =  dateFormat.format(inputDate);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.w("ArticleDateParser",
				    "Wyst�pi� problem z konwersj� pubDate: " + e.toString());
		}	  
		
		this.date = strDate;
	}
	
	
	/*
	public Date getPublishedDate() {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.UK);
		Date date = null;
	    
		try {
			date = formatter.parse(this.date);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.w("ArticleDateParser",
				    "Wyst�pi� problem z konwersj� daty: " + e.toString());
		}	  
		
		return date;
	}
*/
}
