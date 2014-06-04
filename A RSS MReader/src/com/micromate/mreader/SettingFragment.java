package com.micromate.mreader;


import com.micromate.mreader.service.ServiceAlarmManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingFragment extends PreferenceFragment {
    
	private static final String LOG_TAG= "SettingFragment"; 
	

	@Override
     public void onCreate(final Bundle savedInstanceState) {	 
         super.onCreate(savedInstanceState);
 		 Log.i(LOG_TAG, "onCreate");
         
         //Inflates the given XML resource
         addPreferencesFromResource(R.xml.preferences);
     
	 }
	 
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		Log.i(LOG_TAG, "onDestroyView");
		settingsService();
	}
	 
	 
	//Ustawienia i zarzadzanie us¸ugˆ
	 private void settingsService() {
		 //Zapis ustawieÄ w plikach SheredPreference
		 SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
			
		 //pobranie rezultatow ustawien urzytkownika
		 boolean powiadomienia = sharedPreferences.getBoolean("prefNotification", false);
		 String intervalSecond = sharedPreferences.getString("prefIntervalSecond", "60");
		
		 Log.i(LOG_TAG, "Notifications: "+powiadomienia);
		 Log.i(LOG_TAG, "Notification intervals: " +intervalSecond);
		 
		 //klasa zarzadza us¸uga
		 ServiceAlarmManager serviceAlarmMenager = new ServiceAlarmManager(getActivity());
			
		 serviceAlarmMenager.intervalSecond(Integer.parseInt(intervalSecond));
			
		 if (powiadomienia) {
				serviceAlarmMenager.start();
		 }
		 else {
				serviceAlarmMenager.stop();
		 } 
	 }
	 
	 
	 
}