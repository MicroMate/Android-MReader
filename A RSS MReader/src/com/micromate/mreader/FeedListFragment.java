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
		
		rssChannels = new ArrayList<Feed>();
		
		/*Getting all RSS channels from Data Base*/
		baza = new DBoperacje(getActivity());  	// W klasie pochodnej od FRAGMENT context ma˝na przekazaç uzywajàc getActivity(), zwraca CONTEXT activity        
		rssChannels = baza.readAllRssChannels(); //getting all Feeds	    
	    feedListAdapter = new FeedListAdapter(getActivity(), R.layout.fragment_rss_channels, rssChannels);
	    listView.setAdapter(feedListAdapter);

	    /*select on item click*/
		listView.setOnItemClickListener(new OnItemClickListener() {   
	 
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int pos, long id) {
			
				
				Fragment fragment = new ArticlesListFragment();
				/**/
				Bundle bundle = new Bundle();
				bundle.putInt("CHANNEL_ID", rssChannels.get(pos).get_id());
				fragment.setArguments(bundle);
				
				FragmentManager fragmentManager = getFragmentManager();
		        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
						
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
	
}