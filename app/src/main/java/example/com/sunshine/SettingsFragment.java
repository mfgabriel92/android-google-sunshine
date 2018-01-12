package example.com.sunshine;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

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
