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
			Intent usluga = new Intent(context, NewArticleService.class);
			context.startService(usluga);	
	
	}
}
