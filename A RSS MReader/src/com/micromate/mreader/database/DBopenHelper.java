//BAZA DANYCH
/*
 * Klasa SQLiteOpenHelper - konfigurowanie baz danych i otwieranie polaczen
 *  1. Implementacja cyklu zycia
 *  - onCreate i onUpgrade - FRAMEWORK wywo¸uje te metody kiedy sa potrzebne
 *  Jesli baza danych jeszcze nie istnieje aplikacjia ja tworzy
 */

package com.micromate.mreader.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// SQLiteOpenHelper - sluzy do konfigurowania baz i otwierania polaczen
public class DBopenHelper extends SQLiteOpenHelper{

	  // Database Version                                  
	  private static final int DB_VERSION = 2;

	  // Database Name
	  public static final String DB_NAME = "rssMReader.db";

	  // Table Names
	  public static final String TABLE_RSS_CHANNEL = "rssChannel"; 
	  public static final String TABLE_ARTICLE = "article";
	    
	  // Column names of rssChannel table
	  public static final String COLUMN_ID = "_id";
	  public static final String COLUMN_TITLE = "title";
	  public static final String COLUMN_LINK = "link";
	  public static final String COLUMN_RSS_LINK = "rssLink";
	  public static final String COLUMN_DESCRIPTION = "decription";
				
	  //Column names of article table
	  public static final String ARTICLE_COLUMN_ID = "_id";
	  public static final String ARTICLE_COLUMN_WEBSITE_ID = "website_id";
	  public static final String ARTICLE_COLUMN_TITLE = "title";   
	  public static final String ARTICLE_COLUMN_DESCRIPTION = "description";   		 
	  public static final String ARTICLE_COLUMN_URL = "url";  
	  public static final String ARTICLE_COLUMN_PUBDATE = "pubDate";  
	  public static final String ARTICLE_COLUMN_UNREAD = "unread";  
	  public static final String ARTICLE_COLUMN_DONT_DELETE = "dontDelete"; //don't delete article 
	  
	  private static final String CREATE_RSS_TABLE = 
			  "CREATE TABLE " + TABLE_RSS_CHANNEL + "(" + 
			  COLUMN_ID + " INTEGER PRIMARY KEY," + 
			  COLUMN_TITLE + " TEXT," + 
			  COLUMN_LINK + " TEXT," + 
			  COLUMN_RSS_LINK + " TEXT," + 
			  COLUMN_DESCRIPTION + " TEXT" + ")";
	  

	  // Database creation SQL statement
	  private static final String UTWORZ_TABLICE = 
		  "create table " + TABLE_ARTICLE + "(" + 
	      ARTICLE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
	      ARTICLE_COLUMN_WEBSITE_ID + " INTEGER, " + 		
	      ARTICLE_COLUMN_TITLE + " TEXT," +
	      ARTICLE_COLUMN_DESCRIPTION + " TEXT," +
	      ARTICLE_COLUMN_URL + " TEXT," + 
	      ARTICLE_COLUMN_PUBDATE + " TEXT," +    //SQLite does not have a storage class set aside for storing dates and/or times.
	      ARTICLE_COLUMN_UNREAD + " INTEGER," +  	  //  SQLite does not have a separate Boolean storage class. 
	      ARTICLE_COLUMN_DONT_DELETE + " INTEGER," +  //  Instead, Boolean values are stored as integers 0 (false) and 1 (true).
	      " FOREIGN KEY ("+ARTICLE_COLUMN_WEBSITE_ID+") REFERENCES "+TABLE_RSS_CHANNEL+"("+COLUMN_ID+"));";

	  
	  //W konstruktorze podajemy NAZWE i WERSJE bazy oraz WYWOLUJEMY konstuktor klasy nadrzednej 
	  public DBopenHelper(Context context) {
	    super(context, DB_NAME, null, DB_VERSION);
	  }

	  
	  //PRZESLANIANIE metod cyklu zycia 
	  
	  //na Starcie -
	  @Override
	  public void onCreate(SQLiteDatabase bazaDanych) {
		  
	    bazaDanych.execSQL(UTWORZ_TABLICE);
	    bazaDanych.execSQL(CREATE_RSS_TABLE);
	  
	  }

	  //na Update -
	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    
		Log.w(DBopenHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
	   
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLE);
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_RSS_CHANNEL);
	    
	    onCreate(db);
	  }

	} 