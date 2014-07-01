package com.micromate.mreader;

import com.micromate.mreader.service.ServiceAlarmManager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingActivity extends Activity {

	private static final String LOG_TAG= "SettingFragment"; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		//Enable the app icon as an Up button (Home button)   
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
	    
	}

	public static class MyPreferenceFragment extends PreferenceFragment
	{
		@Override
		public void onCreate(final Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
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
			 boolean updateEnable = sharedPreferences.getBoolean("prefUpdateEnable", false);	 
			 String intervalSecond = sharedPreferences.getString("prefIntervalSecond", "60");
				
			 Log.i(LOG_TAG, "Notification intervals: " +intervalSecond);
			 
			 //klasa zarzadza us¸uga
			 ServiceAlarmManager serviceAlarmMenager = new ServiceAlarmManager(getActivity());
				
			 serviceAlarmMenager.intervalSecond(Integer.parseInt(intervalSecond));
				
			 if (updateEnable) {
					serviceAlarmMenager.start();
			 }
			 else {
					serviceAlarmMenager.stop();
			 } 
		 } 
	}
	
	
}
