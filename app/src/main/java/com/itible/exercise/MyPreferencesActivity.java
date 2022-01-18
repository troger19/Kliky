package com.itible.exercise;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;


public class MyPreferencesActivity extends PreferenceActivity {

    public static final String NUMBER_OF_SERIES_PREF = "number_of_series_preference";
    public static final String PAUSE_PREF = "pause_preference";
    public static final String MOBILE_NUMBER_PREF = "mobile_number_preference";
    final static String SENDING_SMS = "alarm_preference";
    private final static String LOG_TAG = MyPreferencesActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
        }
    }
}