/* Feed - RSS channel*/

package com.micromate.mreader;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
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
//import android.support.v4.widget.SearchViewCompatIcs.MySearchView;
import com.micromate.mreader.navigationdrawer.FeedListAdapter;

public class MainActivity extends Activity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
 
    // nav drawer title
    private CharSequence mDrawerTitle;
 
    // used to store app title
    private CharSequence mTitle;
 
    // slide menu items
    //private String[] navMenuTitles;
    //private TypedArray navMenuIcons;
 
   // private ArrayList<NavDrawerItem> navDrawerItems;
   // private NavDrawerListAdapter adapter;
    private FeedListAdapter feedListAdapter;
    // my
    private DBoperacje baza;
    private List<Feed> feeds; //rss chanels
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //action bar progress (refresh feeds)
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
            | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
   
        mTitle = mDrawerTitle = getTitle();
 
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
 
        // my
        baza = new DBoperacje(this);
        feeds = new ArrayList<Feed>();
        feeds = baza.readAllRssChannels();
        countUnreadArticles();         
  
        // setting the nav drawer list adapter
        //adapter = new NavDrawerListAdapter(getApplicationContext(),navDrawerItems);
        feedListAdapter = new FeedListAdapter(getApplicationContext(), feeds);
        
        //mDrawerList.setAdapter(adapter);
        mDrawerList.setAdapter(feedListAdapter);
 
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        //item long click delete feed
        mDrawerList.setOnItemLongClickListener(new SlideMenuLongClickListener());
                
        if (mDrawerLayout != null){ //if portrait layout mDrawerLaout is not null

        	 // enabling action bar app icon and behaving it as toggle button
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);         //call requires API level 14 min 
        	
        	mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
        			R.drawable.ic_drawer, //nav menu toggle icon
                	R.string.app_name, // nav drawer open - description for accessibility
                	R.string.app_name // nav drawer close - description for accessibility
        			) {
        		public void onDrawerClosed(View view) {
        			getActionBar().setTitle(mTitle);
        			// calling onPrepareOptionsMenu() to show action bar icons
        			//invalidateOptionsMenu();
        		}
 
        		public void onDrawerOpened(View drawerView) {
                
        			getActionBar().setTitle(mDrawerTitle);
                	
        			// calling onPrepareOptionsMenu() to hide action bar icons
        			//invalidateOptionsMenu();
        			
        		}
        	};
        
        	mDrawerLayout.setDrawerListener(mDrawerToggle);	
        }
        
        if (savedInstanceState == null) {
            // on first time display view for first nav item
        	if (!feeds.isEmpty()) //when application has not added any feed
        		displayView(0);   //przy starcie wyswietlana pierwsza pozycja z listy feed, ZMIENIC BY ZAPAMIETYWA�O STAN
        }
    }
 
    /**
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
    
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        //return true;  //brak przycisk�w na action bar
        return super.onCreateOptionsMenu(menu);
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
    	if(mDrawerLayout!=null)
    		if (mDrawerToggle.onOptionsItemSelected(item)) {
    			return true;
    		}
    	
    	Intent intent = null;
    	
    	// Handle action bar actions click
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
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	if(mDrawerLayout!=null){
    		// if nav drawer is opened, hide the action items
    		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
    		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
    	}
        return super.onPrepareOptionsMenu(menu);
    }
 
    
    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position){
    	Fragment fragment = new ArticlesListFragment();
    	/**/
		Bundle bundle = new Bundle();
		try {
			setTitle(feeds.get(position).getTitle()); //set selected title on action bar  
			bundle.putInt("FEED_ID", feeds.get(position).get_id()); //for ArticlesListFragment
			bundle.putString("FEED_TITLE", feeds.get(position).getTitle()); //for ArticleFragment
		}
		catch(IndexOutOfBoundsException e){
			e.printStackTrace();
		}
		fragment.setArguments(bundle);
		
		FragmentManager fragmentManager = getFragmentManager();
		//go back to top, Pop all back stack states up (clear back stack)
		fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); 
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
  		int unreadQty = 0;
  		List<Article> articles;
  		
  		for (Feed c: feeds){
  			unreadQty = 0;
  			articles = new ArrayList<Article>();
  			articles = baza.getAllArticlesByID(c.get_id());
  			for(Article a: articles){
  				if (a.getUnread()==0)  //0 = unread an article 
  					unreadQty++;
  			}
  			c.setUnreadQuantity(unreadQty);  //setting unread articles quantity
  		}
  	}
  	
  	//this method is called from FeedUpdateTask class in method onPostExecute (after refreshing feeds)
  	public void updateArticleListView(){
  		ArticlesListFragment fragment = (ArticlesListFragment) getFragmentManager().findFragmentById(R.id.frame_container);
  		fragment.updateArticleListView();
  		Log.d("MainActivity", "updateArticleListView");
  	}
}