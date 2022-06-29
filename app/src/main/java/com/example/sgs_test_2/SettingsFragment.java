package com.example.sgs_test_2;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

public class SettingsFragment extends PreferenceFragmentCompat {

    private EditTextPreference prefProfileName = null;
    private SwitchPreference prefNightDayMode = null;
    //private ListPreference prefMapTheme = null;
    //private SwitchPreference prefNotifications = null;
    //private ListPreference prefLanguage = null;

    private SharedPreferences sp;

    public SettingsFragment() {
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //prefMapTheme = findPreference("mapTheme");
        //prefNotifications = findPreference("notifications");
        //prefLanguage = findPreference("list_preference_1");
        /*String mapTheme = sp.getString("mapTheme", "");
        prefMapTheme.setSummary(mapTheme);*/
        //String language = sp.getString("list_preference_1", "");
        //prefLanguage.setSummary(language);
        // prefProfileName = findPreference("newName");


        setPreferencesFromResource(R.xml.main_preference, rootKey);
        prefNightDayMode = findPreference("nightDayMode");
        sp = PreferenceManager.getDefaultSharedPreferences(requireContext());

        prefNightDayMode.setOnPreferenceChangeListener((preference, newValue) -> {
            setNightDayMode(prefNightDayMode.isChecked());
            return true;
        });

     /*   prefProfileName.setOnPreferenceChangeListener((preference, newValue) -> {
            prefProfileName.setText(String.valueOf(newValue));
            sp.edit().putString("newName", String.valueOf(newValue)).apply();
            requireActivity().recreate();
            return true;
        });*/



        /*prefMapTheme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                setMapTheme(newValue);
                return true;
            }
        });*/

        /*prefNotifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                setNotifications();
                return true;
            }
        });*/

        /*prefLanguage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                setLanguage(newValue);
                return true;
            }
        });*/

    }

    private void setNightDayMode(boolean nightDayMode) {
        //Boolean nightOn = prefNightDayMode.isChecked();
        if (nightDayMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            prefNightDayMode.setChecked(true);
            sp.edit().putBoolean("nightDayMode", true).apply();
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            prefNightDayMode.setChecked(false);
            sp.edit().putBoolean("nightDayMode", false).apply();
        }
    }

    /*private void setNotifications() {
        Boolean notificationsOn = prefNotifications.isChecked();
        if(notificationsOn){
            prefNotifications.setSummary("Notifications turned off");
        }else{
            prefNotifications.setSummary("Notifications turned on");
        }
    }*/

    /*private void setMapTheme(Object newValue) {
        String temp = (String) newValue;
        sp.edit().putString("mapTheme", temp).apply();
        prefMapTheme.setSummary(temp);
    }*/

    /*private void setLanguage(Object newValue){
        String temp = (String) newValue;
        sp.edit().putString("list_preference_1", temp).apply();
        prefLanguage.setSummary(temp);
    }*/
}

