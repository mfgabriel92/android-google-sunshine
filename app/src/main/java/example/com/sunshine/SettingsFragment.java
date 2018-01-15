package example.com.sunshine;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import example.com.sunshine.data.SunshinePreferences;
import example.com.sunshine.data.contract.WeatherContract;
import example.com.sunshine.util.SunshineSyncUtils;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.weather_preferences);
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int count = preferenceScreen.getPreferenceCount();

        for (int i = 0; i < count; i++) {
            Preference preference = preferenceScreen.getPreference(i);
            String val = sharedPreferences.getString(preference.getKey(), "");

            setPreferenceSummary(preference, val);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Activity activity = getActivity();

        if (key.equals(getString(R.string.pref_location_key))) {
            SunshinePreferences.resetLocationCoordinates(activity);
            SunshineSyncUtils.startImmediateSynt(activity);
        } else if (key.equals(getString(R.string.pref_units_key))) {
            activity.getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null);
        }

        Preference preference = findPreference(key);

        if (preference != null) {
            setPreferenceSummary(preference, sharedPreferences.getString(key, ""));
        }
    }

    private void setPreferenceSummary(Preference preference, Object value) {
        String val = value.toString();

        if (preference instanceof ListPreference) {
            ListPreference list = (ListPreference) preference;
            int index = list.findIndexOfValue(val);

            if (index >= 0) {
                preference.setSummary(list.getEntries()[index]);
            }
        } else {
            preference.setSummary(val);
        }
    }
}
