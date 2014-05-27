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

	  //Wersja Bazy Danych                                    
	  private static final int WERSJA_BAZY = 1;

	  // Database Name
	  public static final String NAZWA_BAZY = "rssMReader.db";

	  // Table Names
	  public static final String TABLE_RSS_CHANNEL = "rssChannel"; 
	  public static final String NAZWA_TABELI = "article";
	    
	  // Column names
	  public static final String COLUMN_ID = "_id";
	  public static final String COLUMN_TITLE = "title";
	  public static final String COLUMN_LINK = "link";
	  public static final String COLUMN_RSS_LINK = "rssLink";
	  public static final String COLUMN_DESCRIPTION = "decription";
				
	  //Nazwa kolumn
	  public static final String NAZWA_KOLUMNY_ID = "_id";
	  public static final String NAZWA_KOLUMNY_WEBSITE_ID = "website_id";
	  public static final String NAZWA_KOLUMNY_TITLE = "title";   
	  public static final String NAZWA_KOLUMNY_DESCRIPTION = "description";   		 
	  public static final String NAZWA_KOLUMNY_URL = "url";  
	  public static final String NAZWA_KOLUMNY_PUBDATE = "pubDate";  
	  public static final String NAZWA_KOLUMNY_CATEGORY = "category";  
	  
	  private static final String CREATE_RSS_TABLE = 
			  "CREATE TABLE " + TABLE_RSS_CHANNEL + "(" + 
			  COLUMN_ID + " INTEGER PRIMARY KEY," + 
			  COLUMN_TITLE + " TEXT," + 
			  COLUMN_LINK + " TEXT," + 
			  COLUMN_RSS_LINK + " TEXT," + 
			  COLUMN_DESCRIPTION + " TEXT" + ")";
	  

	  // Database creation sql statement
	  private static final String UTWORZ_TABLICE = 
		  "create table " + NAZWA_TABELI + "(" + 
	      NAZWA_KOLUMNY_ID + " integer primary key autoincrement, " + 
	      NAZWA_KOLUMNY_WEBSITE_ID + " integer, " + 		
	      NAZWA_KOLUMNY_TITLE + " text," +
	      NAZWA_KOLUMNY_DESCRIPTION + " text," +
	      NAZWA_KOLUMNY_URL + " text," + 
	      NAZWA_KOLUMNY_PUBDATE + " text," +
	      NAZWA_KOLUMNY_CATEGORY + " text," +
	      " FOREIGN KEY ("+NAZWA_KOLUMNY_WEBSITE_ID+") REFERENCES "+TABLE_RSS_CHANNEL+"("+COLUMN_ID+"));";

	  
	  //W konstruktorze podajemy NAZWE i WERSJE bazy oraz WYWOLUJEMY konstuktor klasy nadrzednej 
	  public DBopenHelper(Context context) {
	    super(context, NAZWA_BAZY, null, WERSJA_BAZY);
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
	   
	    db.execSQL("DROP TABLE IF EXISTS " + NAZWA_TABELI);
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_RSS_CHANNEL);
	    
	    onCreate(db);
	  }

	} 