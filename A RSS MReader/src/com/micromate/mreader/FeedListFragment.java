/*Feed - Rss channel*/

package com.micromate.mreader;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.micromate.mreader.database.Article;
import com.micromate.mreader.database.DBoperacje;
import com.micromate.mreader.database.Feed;

public class FeedListFragment extends Fragment {
	
	public FeedListFragment(){}
   
	private	ListView listView;	
	private List<Feed> rssChannels; 
	private FeedListAdapter feedListAdapter;   
	private DBoperacje baza;

    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 
		View rootView = inflater.inflate(R.layout.fragment_rss_channels, container, false);
		
		listView = (ListView)rootView.findViewById(R.id.channels_list);
		
		//setting title in ActionBar
		getActivity().getActionBar().setTitle("Feeds");
			
		rssChannels = new ArrayList<Feed>();
		
		/*Getting all RSS channels from Data Base*/
		baza = new DBoperacje(getActivity()); 
		rssChannels = baza.readAllRssChannels(); //getting all Feeds
		//
		getArticlesUnreadQty();
	    feedListAdapter = new FeedListAdapter(getActivity(), R.layout.fragment_rss_channels, rssChannels);
	    listView.setAdapter(feedListAdapter);

	    /*select on item click*/
		listView.setOnItemClickListener(new OnItemClickListener() {   
	 
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int pos, long id) {
			
				
				Fragment fragment = new ArticlesListFragment();
				/**/
				Bundle bundle = new Bundle();
				bundle.putInt("FEED_ID", rssChannels.get(pos).get_id()); //for ArticlesListFragment
				bundle.putString("FEED_TITLE", rssChannels.get(pos).getTitle()); //for ArticleFragment
				fragment.setArguments(bundle);
				
				FragmentManager fragmentManager = getFragmentManager();
				//kilka metod wywołanych w jednym wierszu, w klasie ArticleFragment w paru wierszach 
		        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).addToBackStack(null).commit();
						
			}
		});
		
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int pos, long arg3) {
				// TODO Auto-generated method stub
					
				Vibrator vibrator;
				vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(100);
				
				//AlertDialog builder instance
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			    builder
			    	.setTitle(rssChannels.get(pos).getLink())
			    	.setItems(R.array.feedlist_option_array, new DialogInterface.OnClickListener() {
			    	public void onClick(DialogInterface dialog, int which) {
			    		// The 'which' argument contains the index position of the selected item
			    		
			    		baza.deleteRssChannel(rssChannels.get(pos).get_id());
			    		baza.deleteArticles(rssChannels.get(pos).get_id());
			    		
			    		Toast.makeText(getActivity(), "Deleted Feed _id: "+rssChannels.get(pos).get_id(), Toast.LENGTH_SHORT).show();
			    		
			    		/*updating list view*/
			    		rssChannels.clear();
			    		rssChannels.addAll(baza.readAllRssChannels());	
			    		feedListAdapter.notifyDataSetChanged();
			            	   
			    	}
			    });
			    //Create the AlertDialog
			    AlertDialog dialog = builder.create();
			    //Show the AlertDialog
			    dialog.show();
				
				return false;
			}
			
		});
		
		return rootView;
		
	}
	
	private void getArticlesUnreadQty(){
		int unreadQty = 0;
		List<Article> articles;
		
		for (Feed c: rssChannels){
			unreadQty = 0;
			articles = new ArrayList<Article>();
			articles = baza.getAllArticlesByID(c.get_id());
			for(Article a: articles){
				if (a.getUnread()==0)  //0 = unread an article 
					unreadQty++;
			}
			c.setUnreadQuantity(unreadQty);
		}
	}
	
}