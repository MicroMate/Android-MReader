package com.micromate.mreader.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver{

	
	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO Auto-generated method stub
			Log.i("AlarmReceiver","Start Service");
			//Intent usluga = new Intent(context, NewArticleService.class); //Only checking if new article released (SAX parser)
			Intent usluga = new Intent(context, UpdateArticlesService.class); //Updating all new articles (ROME parser
			context.startService(usluga);	
	
	}
}
