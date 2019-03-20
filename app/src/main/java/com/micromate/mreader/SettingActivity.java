package com.micromate.mreader;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.TwoStatePreference;
import android.util.Log;

import com.micromate.mreader.service.ServiceAlarmManager;

public class SettingActivity extends Activity {

	public static final String KEY_UPDATE_ENABLED = "KEY_UPDATE_ENABLED";
	public static final String KEY_UPDATE_INTERVAL = "KEY_UPDATE_INTERVAL";
	public static final String KEY_DELETE_ENABLED = "KEY_DELETE_ENABLED";
	public static final String KEY_DELETE_QTY_KEPT = "KEY_DELETE_QTY_KEPT";
	public static final String KEY_NOTIFICATIONS_ENABLED ="KEY_NOTIFICATIONS_ENABLED";

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
		
		private CheckBoxPreference updateAuto;
		private ListPreference updateInterval;
		private CheckBoxPreference deleteAuto;
		private ListPreference deleteArticleKeptQty;
		
		
		
		@Override
		public void onCreate(final Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
			
			
			updateAuto = (CheckBoxPreference)findPreference(KEY_UPDATE_ENABLED);
			updateInterval = (ListPreference)findPreference(KEY_UPDATE_INTERVAL);
			deleteAuto = (CheckBoxPreference)findPreference(KEY_DELETE_ENABLED);
			deleteArticleKeptQty = (ListPreference)findPreference(KEY_DELETE_QTY_KEPT);
			
			
			//auto update views initialization
			if (updateAuto.isChecked())
				updateInterval.setEnabled(true);
			
			updateInterval.setSummary(updateInterval.getEntry());
				
					
			//auto delete views initialization
			if (deleteAuto.isChecked())
				deleteArticleKeptQty.setEnabled(true);

			final String qtySummary = "The oldest articles will be deleted except of the favorite articles.\nQ-ty entry: ";
			String qtyEntry = (String) deleteArticleKeptQty.getEntry();
			deleteArticleKeptQty.setSummary(qtySummary+qtyEntry);
				
			
			// Dynamically updating summary view after entering value 			
			
			updateInterval.setOnPreferenceChangeListener(new MyOnSummaryChange());
			//deleteArticleKeptQty.setOnPreferenceChangeListener(new MyOnSummaryChange());	
			
			// I created this callback for deleteArticleKeptQty 
			// because setSumamry is with special parameter: qtySummary + ... 
			deleteArticleKeptQty.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					
					// Set the value as the new value
		            ((ListPreference) preference).setValue(newValue.toString());
		            // Get the entry which corresponds to the current value and set as summary
		            preference.setSummary(qtySummary+((ListPreference) preference).getEntry());
					return true;
				}
			});
			
			
			// Enable list preferences
			updateAuto.setOnPreferenceChangeListener(new MyOnEnabledChange(updateInterval));
			deleteAuto.setOnPreferenceChangeListener(new MyOnEnabledChange(deleteArticleKeptQty));
			
			
	    }
			
		// Callback be invoked when this Preference is changed by the user 
		
		private class MyOnSummaryChange implements OnPreferenceChangeListener {
			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
					
				// Set the value as the new value
	            ((ListPreference) preference).setValue(newValue.toString());
	            // Get the entry which corresponds to the current value and set as summary
	            preference.setSummary(((ListPreference) preference).getEntry());
	            
				return true;
			}	
		}
		
		private class MyOnEnabledChange implements OnPreferenceChangeListener {
			
			Preference preferenceEnabled;
			
			public MyOnEnabledChange(Preference preference) {
				preferenceEnabled = preference;
			}
			
			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
					          
	            if (((TwoStatePreference) preference).isChecked())
					preferenceEnabled.setEnabled(false);
	            else
	            	preferenceEnabled.setEnabled(true);
	            
				return true;
			}	
		}

		
		
		@Override
		public void onDestroyView() {
			// TODO Auto-generated method stub
			super.onDestroyView();
			Log.i(LOG_TAG, "onDestroyView");
			settingsService();
		}
		
		//Service Managing
		 private void settingsService() {
		
			//  retrieve preferences values
			 SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
			 boolean updateEnable = sharedPreferences.getBoolean(KEY_UPDATE_ENABLED, false);	 
			 String intervalSecond = sharedPreferences.getString(KEY_UPDATE_INTERVAL, "60");  // 60 = 1 hour
				
			 Log.i(LOG_TAG, "Notification intervals: " +intervalSecond);
			 
			 //Create instance of ServiceAlarmManager class
			 ServiceAlarmManager serviceAlarmMenager = new ServiceAlarmManager(getActivity());
			 
			 //setting intervals of update an articles 
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
