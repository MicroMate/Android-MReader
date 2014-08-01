/* Feed - RSS channel*/

package com.micromate.mreader;

import java.util.ArrayList;
import java.util.List;

//import android.app.ActionBar; // If supporting only API level 11 and higher
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.app.Fragment; // If supporting only API level 11 and higher
import android.app.FragmentManager;
import android.app.FragmentTransaction;
//import android.support.v4.app.Fragment; // If supporting API levels lower than 11
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar; // lower then API 11
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.micromate.mreader.database.Article;
import com.micromate.mreader.database.DBoperacje;
import com.micromate.mreader.database.Feed;
import com.micromate.mreader.dialogs.DeleteFeedDialogFragment;
import com.micromate.mreader.navigationdrawer.FeedListAdapter;

//public class MainActivity extends Activity {
public class MainActivity extends ActionBarActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
 
    // nav drawer title
    private CharSequence mDrawerTitle;
 
    // used to store app title
    private CharSequence mTitle;
  
    //navigation drawer adapter
    private FeedListAdapter feedListAdapter;
    // 
    private DBoperacje baza;
    private List<Feed> feeds; //rss chanels
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // retrieve a reference to this activity's ActionBar.
        ActionBar actionBar = getSupportActionBar(); // getting action bar when API levels lower than 11
        
        // action bar progress (refresh feeds)
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
            | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
   
        mTitle = mDrawerTitle = getTitle();
 
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
 
        // create instance of database class
        baza = new DBoperacje(this);
        feeds = new ArrayList<Feed>();
        //adding two first position in navigation drawer
        feeds.add(new Feed("All Articles", "Articles of all added feeds ", null, null));
        feeds.add(new Feed("Favorite Articles ", "Articles checked with the star", null, null));
        // adding all feeds from database
        feeds.addAll(baza.readAllRssChannels());
        
        // method counting unread articles and setting in feed
        countUnreadArticles();         
  
        // setting the navigation drawer list adapter
        feedListAdapter = new FeedListAdapter(getApplicationContext(), feeds);
        
        //mDrawerList.setAdapter(adapter);
        mDrawerList.setAdapter(feedListAdapter);
 
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        
        // Item list long click deleting a feed
        mDrawerList.setOnItemLongClickListener(new SlideMenuLongClickListener());
        
        // if mDraverLayout is not null (is null when screen orientation is landscape - drawer is not using)
        if (mDrawerLayout != null){ //if portrait layout - mDrawerLaout is not null

        	// enabling action bar app icon and behaving it as toggle button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // getActionBar().setHomeButtonEnabled(true);        // If supporting only API level 14 and higher
            getSupportActionBar().setHomeButtonEnabled(true);    // If supporting API levels lower than 14 
        	
            // Listen for Drawer Open and Close
        	mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
        			R.drawable.ic_drawer, //nav menu toggle icon
                	R.string.app_name, // nav drawer open - description for accessibility
                	R.string.app_name // nav drawer close - description for accessibility
        			) {
        		
        		// Callbacks for drawer open and closeevents
        		/** Called when a drawer has settled in a completely closed state. */
        		public void onDrawerClosed(View view) {
        			getActionBar().setTitle(mTitle);
        			
        			// calling onPrepareOptionsMenu() to show action bar icons
        			//invalidateOptionsMenu();
        		}
        		
        		/** Called when a drawer has settled in a completely open state. */
        		public void onDrawerOpened(View drawerView) {                
        			getActionBar().setTitle(mDrawerTitle);
        			
        			// calling onPrepareOptionsMenu() to hide action bar icons
        			//invalidateOptionsMenu();
        			
        		}
        	};
        	
        	// Set the drawer toggle as the DrawerListener
        	mDrawerLayout.setDrawerListener(mDrawerToggle);	
        }
        
        if (savedInstanceState == null) {
            // on first time display view for first nav item
        	if (!feeds.isEmpty()) //when application has not added any feed
        		displayView(0);   //when application is opening display all articles, position 0 = all articles 
        }
    }
 
    /**
     * Handle Navigation Click Events
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }
    
    // long click listener delete feed
    private class SlideMenuLongClickListener implements ListView.OnItemLongClickListener {
  
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long id) {
			// TODO Auto-generated method stub
						
			DeleteFeedDialogFragment dialog = new DeleteFeedDialogFragment();
			dialog.setData(feeds, baza, position);
        	dialog.show(getFragmentManager(), "addFeed");
		
			return true;
		}
    }
    
    
    
    // Adding Action Items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// Inflate the menu items for use in the action bar
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
 
    
    // Handling clicks on action items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    
    	// mDraverLayout is not null (is null when screen orientation is landscape - drawer is not using)
    	if(mDrawerLayout!=null)
    		// toggle nav drawer on selecting action bar app icon/title
    		if (mDrawerToggle.onOptionsItemSelected(item)) {
    			return true;
    		}
    	
    	Intent intent = null;
    	
    	// Handle cliks on the action bar items
        switch (item.getItemId()) {
        case R.id.action_add:
        	//for dialog version
//        	DialogFragment dialog = new AddFeedDialogFragment();
//        	dialog.show(getFragmentManager(), "addFeed");
        	//for activity version
        	intent = new Intent(this,FeedAddRssActivity.class);
        	startActivity(intent);
        	return true;
        case R.id.action_refresh:
        	MenuItem menuItem = item;
         	FeedUpdateTask feedUpdateTask = new FeedUpdateTask(this, menuItem);
        	feedUpdateTask.execute();
        	return true;
        case R.id.action_set2:
        	intent = new Intent(this, SettingActivity.class);
        	startActivity(intent);
            return true;
        case R.id.action_settings:
        	intent = new Intent(this, SettingActivity.class);
        	startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
        
    }
 
    /***
     * Called whenever we call invalidateOptionsMenu()
     */
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//    	if(mDrawerLayout!=null){
//    		// if nav drawer is opened, hide the action items
//    		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//    		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
//    	}
//        return super.onPrepareOptionsMenu(menu);
//    }
 
    
    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    public void displayView(int position){
    	Fragment fragment = new ArticlesListFragment();
    	/**/
		Bundle bundle = new Bundle();
		try {
			setTitle(feeds.get(position).getTitle()); //set selected title on action bar  
			bundle.putInt("FEED_ID", feeds.get(position).get_id()); //for ArticlesListFragment
			bundle.putString("FEED_TITLE", feeds.get(position).getTitle()); //for ArticleFragment
			bundle.putInt("LIST_POSITION", position); //for checking All Articles (pos.0) and Favorite Articles (pos.1)  
		}
		catch(IndexOutOfBoundsException e){
			e.printStackTrace();
		}
		fragment.setArguments(bundle);
		
		FragmentManager fragmentManager = getFragmentManager();  // if supporting only API level 11 and higher
		//FragmentManager fragmentManager = getSupportFragmentManager();  // if supporting API levels lower than 11 
		//go back to top, Pop all back stack states up (clear back stack)
		//fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); 
    	FragmentTransaction transaction = fragmentManager.beginTransaction();
    	transaction.replace(R.id.frame_container, fragment);
    	transaction.commit();	
    	// update selected item, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);
        if(mDrawerLayout!=null)
        	mDrawerLayout.closeDrawer(mDrawerList);
    }
    
   
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
 
    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
 
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerLayout!=null)
        	// Sync the toggle state after onRestoreInstanceState has occurred.
        	mDrawerToggle.syncState();
    }
 
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(mDrawerLayout!=null)
        	// Pass any configuration change to the drawer toggls
        	mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    //udating navigation drawer list - feed list 
    public void updateFeedListView(){
    	countUnreadArticles();
    	feedListAdapter.notifyDataSetChanged();
    }
    
  //method count and set unread articles of every feed
  	public void countUnreadArticles(){
  		int feedUnreadQty = 0;  //unread articles of specified feed
  		int allUnreadQty = 0;
  		int favoriteUnreadQty = 0;
  		
  		List<Article> articles;
  		
  		for (Feed c: feeds){
  			feedUnreadQty = 0;
  			articles = new ArrayList<Article>();
  			articles = baza.getAllArticlesByID(c.get_id());
  			for(Article a: articles){
  				if (a.getUnread()==0) {  //0 = unread an article 
  					feedUnreadQty++;   //counting one feed unread articles
  					if (a.isFavorite())  //counting favorite unread articles 
  						favoriteUnreadQty++;
  				}
  			}
  			allUnreadQty = allUnreadQty + feedUnreadQty; //counting all unread articles
  			
  			c.setUnreadQuantity(feedUnreadQty);  //setting unread articles quantity
  		}
  		
  		feeds.get(0).setUnreadQuantity(allUnreadQty);  //position 0 , all articles
  		feeds.get(1).setUnreadQuantity(favoriteUnreadQty); //position 1 , favorite articles
  		
  	}
  	
  	//this method is called from FeedUpdateTask class in method onPostExecute (after refreshing feeds)
  	public void updateArticleListView(){
  		ArticlesListFragment fragment = (ArticlesListFragment) getFragmentManager().findFragmentById(R.id.frame_container);
  		//if supporting lower then API 11
  		//ArticlesListFragment fragment = (ArticlesListFragment) getSupportFragmentManager().findFragmentById(R.id.frame_container); 
  		   		fragment.updateArticleListView();
  		Log.d("MainActivity", "updateArticleListView");
  	}
}