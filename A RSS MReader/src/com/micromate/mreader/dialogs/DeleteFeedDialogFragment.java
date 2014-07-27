package com.micromate.mreader.dialogs;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Toast;

import com.micromate.mreader.MainActivity;
import com.micromate.mreader.R;
import com.micromate.mreader.database.DBoperacje;
import com.micromate.mreader.database.Feed;

public class DeleteFeedDialogFragment extends DialogFragment {
	
	private List<Feed> feeds;
	private DBoperacje baza;
	private int position;
	

	public void setData(List<Feed> feeds, DBoperacje baza, int position) {
		this.feeds = feeds;
		this.baza = baza;
		this.position = position;
	}
	
	//private EditText editText;
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//AlertDialog builder instance
		
		Vibrator vibrator;
		vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(100);

		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder
	    	.setTitle("Delete Feed")
	    	.setMessage(feeds.get(position).getLink())
	    	.setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int which) {
	    		// The 'which' argument contains the index position of the selected item
	    		
	    		baza.deleteRssChannel(feeds.get(position).get_id());
	    		baza.deleteArticles(feeds.get(position).get_id());
	    		
	    		Toast.makeText(getActivity(), "Deleted Feed _id: "+feeds.get(position).get_id(), Toast.LENGTH_SHORT).show();
	    		
	    		/*updating list view*/
	    		feeds.clear();
	    		feeds.addAll(baza.readAllRssChannels());	
	    		
	    		//refreshing navigation drawer list - feed list 
	    		((MainActivity)getActivity()).updateFeedListView();
	            	   
	    		}
	    	})
	    	.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	    		public void onClick(DialogInterface dialog, int id) {
	    			DeleteFeedDialogFragment.this.getDialog().cancel();
	    		}
	    	}); 
	    
	  
	    //Create the AlertDialog
	    return builder.create();
	}

}
