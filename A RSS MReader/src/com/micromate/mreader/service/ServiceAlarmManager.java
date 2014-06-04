package com.micromate.mreader.service;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/*
 * Planowanie wykonywania us¸gi
 * 
 * Klasa zarzadza us¸uga (NewArticleService) wsp—¸pracujacˆ z alarmami systemowymi
 * - uruchamianie us¸ugi w okreslonym czasie
 * - wylaczanie us¸ugi
 * - ustawianie interwa¸—w czasowych
 */

public class ServiceAlarmManager {
	
	/*Schedule repeating alarm*/
	private AlarmManager alarmManager;
	private PendingIntent  pendingIntent;
	private static final String LOG_TAG = "ServiceAlarmManager"; 
	private int intervalSec;

	
	/*Constructor*/
	public ServiceAlarmManager(Context context) {
		// TODO Auto-generated constructor stub
	
		alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmReceiver.class);
		pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	}	
	
	
	
	/*Methods*/
	public void intervalSecond(int intervalSec) {
		
		this.intervalSec = intervalSec;
		
		Log.i(LOG_TAG,"interval time: "+intervalSec+"sec");
	}
	
	
	public void start() {	
				
		Log.i(LOG_TAG,"start");
		// restart service every ... seconds
		final long REPEAT_TIME = 1000 * 60 * intervalSec;
			  
		//getInstance() method returns a calendar whose locale is based on system settings
		Calendar cal = Calendar.getInstance();
		// start 5 seconds after boot completed
		cal.add(Calendar.SECOND, 5);
				
		// Schedule a repeating alarm every REPEAT_TIME seconds
		// InexactRepeating allows Android to optimize the energy consumption
		alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), REPEAT_TIME, pendingIntent);
		//RTC_WAKEUP - uruchamianie alarmu nawet w stanie uspienia telefonu

		//alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), REPEAT_TIME, pendingIntent);		
				
	}
	
	
		
	public void	stop() {

		Log.i(LOG_TAG,"stop");	
		//cancel Alarm (stop restarting service)
		alarmManager.cancel(pendingIntent);
	}
	
		
}


