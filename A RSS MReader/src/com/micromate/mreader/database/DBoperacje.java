package com.micromate.mreader.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBoperacje {
	
	private SQLiteDatabase db; 	
	private DBopenHelper dbOpenHelper;
	
	
	public DBoperacje(Context context) {  
		  dbOpenHelper = new DBopenHelper(context);
	  }
	
	/*
	//Open DataBase
	public void open() throws SQLException {
	    db = dbOpenHelper.getWritableDatabase();
	}
	//Close DataBase
	public void close() {
	    dbOpenHelper.close();
	}
	*/
	
	 /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
	 
    //Adding article WHERE channel_id
    public void addArticleByID(Article article, long channel_id) {
    	db = dbOpenHelper.getWritableDatabase();
    	
        ContentValues values = new ContentValues();
        values.put(DBopenHelper.ARTICLE_COLUMN_WEBSITE_ID, channel_id);      
        values.put(DBopenHelper.ARTICLE_COLUMN_TITLE, article.getTitle());
        values.put(DBopenHelper.ARTICLE_COLUMN_DESCRIPTION, article.getDescription()); 
        values.put(DBopenHelper.ARTICLE_COLUMN_URL, article.getUrl()); 
        values.put(DBopenHelper.ARTICLE_COLUMN_PUBDATE, article.getDate()); 
        values.put(DBopenHelper.ARTICLE_COLUMN_UNREAD, article.getUnread()); 
        values.put(DBopenHelper.ARTICLE_COLUMN_DONT_DELETE, article.getDontDelete()); 
              
        // Inserting Row
        db.insert(DBopenHelper.TABLE_ARTICLE, null, values);
        db.close(); // Closing database connection
    }
    
    
        
    // Reading all Articles rows from database
    public List<Article> getAllArticle() {
    	db = dbOpenHelper.getWritableDatabase();
       
    	List<Article> lista = new ArrayList<Article>();
             
        // Select All Query      
        String selectQuery = "SELECT  * FROM " + DBopenHelper.TABLE_ARTICLE;
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        //Drugi sposob pobierania danych z bazy
        //segregowanie wedlug kolumny CZAS (najkrotszy bedzie pierwszy)
        //Cursor kursor = bazaDanych.query(OpenHelperWynik.NAZWA_TABELI, wszystkieKolumny, null, null, null, null,OpenHelperWynik.NAZWA_KOLUMNY_CZAS); 
        
        //int licznik = 1; // dla numeru pozycji na liscie
        		
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Article article = new Article();
                
                article.setId(Integer.parseInt(cursor.getString(0)));
                article.setChannel_id(Integer.parseInt(cursor.getString(1)));
                article.setTitle(cursor.getString(2));
                article.setDescription(cursor.getString(3));
                article.setUrl(cursor.getString(4));
                article.setDate(cursor.getString(5));
                article.setUnread(cursor.getInt(6));
                article.setDontDelete(cursor.getInt(7));
                  
                //licznik (nr pozycji na liscie - mozna dodac do ziarna)
                //wynik.setNr(licznik++);
                
                // Adding item to list
                lista.add(article);
                
            } while (cursor.moveToNext());
        }
 
        cursor.close();
        // return item list
        db.close(); // Closing database connection
        return lista;
    }
    
    
    // Reading all articles WHERE channel_id ORDER by date  
    public List<Article> getAllArticlesByID(int channel_id) {
    	db = dbOpenHelper.getWritableDatabase();
    	   
    	List<Article> lista = new ArrayList<Article>();
             
        // Select All Query      
        String selectQuery = "SELECT  * FROM " + DBopenHelper.TABLE_ARTICLE +" " +
        					 "WHERE website_id = '"+channel_id+"' " +
        					 "ORDER BY "+DBopenHelper.ARTICLE_COLUMN_PUBDATE + " DESC";
        
        Cursor cursor = db.rawQuery(selectQuery, null);
        		
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Article article = new Article();
                
                article.setId(cursor.getInt(0));
                article.setChannel_id(cursor.getInt(1));
                article.setTitle(cursor.getString(2));
                article.setDescription(cursor.getString(3));
                article.setUrl(cursor.getString(4));
                article.setDate(cursor.getString(5));
                article.setUnread(cursor.getInt(6));
                article.setDontDelete(cursor.getInt(7));
                                    
                // Adding contact to list
                lista.add(article);
                
            } while (cursor.moveToNext());
        }
 
        cursor.close();
        db.close(); // Closing database connection
        return lista;
    }
    
    // Getting Latest date of article
    public String getLatestArticleDate() {
    	   db = dbOpenHelper.getWritableDatabase();
    	   
    	String latestDate = "0000-00-00 00:00:00";
        // Select All Query      
        String selectQuery = "SELECT pubDate FROM " + DBopenHelper.TABLE_ARTICLE+" "+
        					 "ORDER BY pubDate DESC LIMIT 1";
        
        Cursor cursor = db.rawQuery(selectQuery, null);
		
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
           	             
            latestDate = cursor.getString(0);
                
        }
        cursor.close();
        db.close(); // Closing database connection
        return latestDate;
    }
    
    // Getting Latest date of article WHERE channel_id
    public String getLatestArticleDateByID(long channel_id) {
    	   db = dbOpenHelper.getWritableDatabase();
    	   
    	String latestDate = "0000-00-00 00:00:00";
        // Select All Query      
        String selectQuery = "SELECT pubDate FROM " + DBopenHelper.TABLE_ARTICLE+" " +
        					 "WHERE website_id = '"+channel_id+"' " +
        		             "ORDER BY pubDate DESC LIMIT 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
		
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
           	             
            latestDate = cursor.getString(0);
                
        }
        cursor.close();
        db.close(); // Closing database connection
        return latestDate;       
    }
    
    
    public void deleteAll() { //przypisuje nr id = 1 
    	db = dbOpenHelper.getWritableDatabase();
    	//bazaDanych.execSQL("delete * from "+ OpenHelperWynik.NAZWA_TABELI);
        //bazaDanych.delete(OpenHelperWynik.NAZWA_TABELI, "1", null);
        db.delete(DBopenHelper.TABLE_ARTICLE, null, null);
        
        db.close(); // Closing database connection
         
    }
    
    
    //delete Articles WHERE channel id (for deleting all articles of specify feed) 
    public boolean deleteArticles(long feed_id) {
    	db = dbOpenHelper.getWritableDatabase();
        String where = "website_id =" + feed_id;
        boolean result = db.delete(DBopenHelper.TABLE_ARTICLE, where , null) > 0;
        db.close(); // Closing database connection
        return result;
    }
    
    //Delete One Article WHERE article url 
    public boolean deleteArticle(String articleURL) {
    	db = dbOpenHelper.getWritableDatabase();
        String where = DBopenHelper.ARTICLE_COLUMN_URL+" = '"+ articleURL +"'";
        boolean result = db.delete(DBopenHelper.TABLE_ARTICLE, where , null) > 0;
        db.close(); // Closing database connection
        return result;
    }
   
   
    
    /*
     *  RSS Channel table operation
     */
    
    
    //creates a new RSS Channel row
    public long addRssChannel(Feed item) {
    	db = dbOpenHelper.getWritableDatabase();
    	
        ContentValues values = new ContentValues();
        values.put(DBopenHelper.COLUMN_TITLE, item.getTitle());
        values.put(DBopenHelper.COLUMN_LINK, item.getLink()); 
        values.put(DBopenHelper.COLUMN_RSS_LINK, item.getRssLink()); 
        values.put(DBopenHelper.COLUMN_DESCRIPTION, item.getDescription()); 
                
        // Inserting Row
        long channel_id = db.insert(DBopenHelper.TABLE_RSS_CHANNEL, null, values);
        
        db.close(); // Closing database connection
        
        return channel_id;
    }
    
    // Reading all RSS Channels rows from database
    public List<Feed> readAllRssChannels() {
    	db = dbOpenHelper.getWritableDatabase();
       
    	List<Feed> lista = new ArrayList<Feed>();
             
        // Select All Query      
        String selectQuery = "SELECT  * FROM " + DBopenHelper.TABLE_RSS_CHANNEL;
        Cursor kursor = db.rawQuery(selectQuery, null);
             		
        // looping through all rows and adding to list
        if (kursor.moveToFirst()) {
            do {
                Feed item = new Feed();
                
                //pobieranie danych z bazy danych
                item.set_id(Integer.parseInt(kursor.getString(0)));
                item.setTitle(kursor.getString(1));
                item.setLink(kursor.getString(2));
                item.setRssLink(kursor.getString(3));       
                item.setDescription(kursor.getString(4));
      
                //licznik (nr pozycji na liscie - mozna dodac do ziarna)
                //wynik.setNr(licznik++);
                
                // Adding item to list
                lista.add(item);
                
            } while (kursor.moveToNext());
        }
 
        kursor.close();
        // return item list
        db.close(); // Closing database connection
        return lista;
    }
    
    //delete feed WHERE feed id 
    public boolean deleteRssChannel(long _index) {
    	db = dbOpenHelper.getWritableDatabase();
        String where = "_id =" + _index;
        boolean result = db.delete(DBopenHelper.TABLE_RSS_CHANNEL, where , null) > 0;
        db.close(); // Closing database connection
        return result; 
    }
    
    //mark to read the article 
    public void setArticleRead(String link){
    	db = dbOpenHelper.getWritableDatabase();
//    	ContentValues values = new ContentValues();
//      values.put(DBopenHelper.ARTICLE_COLUMN_UNREAD, 1); //set 1 = read
//      String whereClause = DBopenHelper.ARTICLE_COLUMN_URL + " = '"+link+ "'";
//      db.update(DBopenHelper.TABLE_ARTICLE, values, whereClause, null);  
    	//drugi spos—b
        String query = "UPDATE article SET unread = 1 WHERE url = '"+ link +"'";
        db.execSQL(query);
        db.close(); // Closing database connection
          
    }
    
}
