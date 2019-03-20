/**
 * This class is excluded from project
 * **/
package com.micromate.mreader.dialogs;

import com.micromate.mreader.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class AddFeedDialogFragment extends DialogFragment {

	//private EditText editText;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(inflater.inflate(R.layout.dialog_add_feed, null))
	    	.setTitle(R.string.title)
	    	// Add action buttons
	    	.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
	    		@Override
	    		public void onClick(DialogInterface dialog, int id) {
	    			// sign in the user ...
	    		}
	    	})
	    	.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	    		public void onClick(DialogInterface dialog, int id) {
	    			AddFeedDialogFragment.this.getDialog().cancel();
	    		}
	    	}); 
	    
	    return builder.create();
		
	}
	
	
	//Prevent close a dialog when click positive button
	@Override
	public void onStart() {
	    super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
	    AlertDialog d = (AlertDialog)getDialog();
	    if(d != null) {
	        Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
	        positiveButton.setOnClickListener(new View.OnClickListener() {
	        	@Override
	        	public void onClick(View v)
	        	{
	        		Boolean wantToCloseDialog = false;
	        		//Do stuff, possibly set wantToCloseDialog to true then...
	        		if(wantToCloseDialog)
	        			dismiss();
	        		//else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
	        	}
	        });
	    }
	}
	
	

}
