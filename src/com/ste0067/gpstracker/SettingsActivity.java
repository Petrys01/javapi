package com.ste0067.gpstracker;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceClickListener;


public class SettingsActivity extends PreferenceFragment {

	
	public static final String PREFS_NAME = "MyPreferences";
	public ListPreference lp;
	public Preference pref;
	public CheckBoxPreference cp;
	public String interval;
	public boolean last_location;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.layout.fragment_settings);

        lp = (ListPreference) findPreference("interval");
		cp = (CheckBoxPreference) findPreference("locations");
		pref = (Preference) findPreference("open");
		
		pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(getActivity(), FileBrowserActivity.class);
				startActivity(intent);
				return false;
			}
		});

		lp.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				
				return false;
			}
		});
    }
	
	@Override
	public void onPause(){
		interval = lp.getValue();

		if (cp.isChecked()) {
			last_location = true;
		} else {
			last_location = false;
		}

		SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		editor.putInt("interval", Integer.parseInt(interval));
		editor.putBoolean("last_location", last_location);
		editor.commit();
		
		super.onPause();
		
	} 
}
