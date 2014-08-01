/*
 * DeleteArticleDialogFragment called from ArticleListFragment class
 */
package com.micromate.mreader.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
//import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.micromate.mreader.MainActivity;
import com.micromate.mreader.R;
import com.micromate.mreader.database.DBoperacje;

public class DeleteArticleDialogFragment extends DialogFragment {
	
	private DBoperacje baza;
	private String articleTitle;
	private String articleURL;
	

	public void setData(String articleTitle, String aricleURL) {
		this.articleURL = aricleURL;
		this.articleTitle = articleTitle;
	}
	
	//private EditText editText;
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		Vibrator vibrator;
		vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(100);

		//AlertDialog builder instance		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder
	    	.setTitle("Delete Article")
	    	.setMessage(articleTitle)
	    	.setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
	    		public void onClick(DialogInterface dialog, int which) {
	    			// The 'which' argument contains the index position of the selected item
    		
	    			baza = new DBoperacje(getActivity());
	    			
	    			baza.deleteArticle(articleURL);
    		
	    			Toast.makeText(getActivity(), "Article deleted", Toast.LENGTH_SHORT).show();
    		
	    			//updating Articles and Feed list view
	    			((MainActivity)getActivity()).updateArticleListView();
	    	 		((MainActivity)getActivity()).updateFeedListView();
				
	    		}
	    	})
	    	.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	    		public void onClick(DialogInterface dialog, int id) {
	    			DeleteArticleDialogFragment.this.getDialog().cancel();
	    		}
	    	}); 
	  
	  
	    //Create the AlertDialog
	    return builder.create();
	}

}
