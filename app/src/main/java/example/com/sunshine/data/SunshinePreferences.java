package example.com.sunshine.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

import example.com.sunshine.R;

public class SunshinePreferences {

    public static final String PREF_COORD_LAT = "coord_lat";
    public static final String PREF_COORD_LON = "coord_lon";

    /**
     * Helper method to handle setting location details in Preferences.
     *
     * @param context Context used to get the SharedPreferences
     * @param cityName A human-readable city name, e.g "Mountain View"
     * @param lat The latitude of the city
     * @param lon The longitude of the city
     */
    public static void setLocationDetails(Context context, String cityName, double lat, double lon) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(PREF_COORD_LAT, Double.doubleToRawLongBits(lat));
        editor.putLong(PREF_COORD_LON, Double.doubleToRawLongBits(lon));
        editor.apply();
    }

    /**
     * Resets the stored location coordinates.
     *
     * @param context Context used to get the SharedPreferences
     */
    public static void resetLocationCoordinates(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(PREF_COORD_LAT);
        editor.remove(PREF_COORD_LON);
        editor.apply();
    }

    /**
     * Returns the location currently set in Preferences.
     *
     * @param context Context used to get the SharedPreferences
     * @return The current user has set in SharedPreferences
     */
    public static String getPreferredWeatherLocation(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String locationKey = context.getString(R.string.pref_location_key);
        String locationDefault = context.getString(R.string.pref_location_default);

        return preferences.getString(locationKey, locationDefault);
    }

    /**
     * Returns true if the user has selected Celsius as temperature display.
     * Returns Fahrenheit if not
     *
     * @param context Context used to get the SharedPreferences
     * @return true If metric display should be used
     */
    public static boolean isCelsius(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String unitsKey = context.getString(R.string.pref_units_key);
        String unitsDefault = context.getString(R.string.pref_units_celsius);
        String unitsPreferred = preferences.getString(unitsKey, unitsDefault);
        String celsius = context.getString(R.string.pref_units_celsius);

        return celsius.equals(unitsPreferred);
    }

    /**
     * Returns true if the user has selected kilometers per hour as speed display.
     * Returns miles per hour if not
     *
     * @param context Context used to get the SharedPreferences
     * @return true If metric display should be used
     */
    public static boolean isKilometers(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String speedKey = context.getString(R.string.pref_speed_key);
        String speedDefault = context.getString(R.string.pref_speed_kilometers);
        String speedPreferred = preferences.getString(speedKey, speedDefault);
        String kilometers = context.getString(R.string.pref_speed_kilometers);

        return kilometers.equals(speedPreferred);
    }

    /**
     * Returns the location coordinates associated with the location
     *
     * @param context Context used to get the SharedPreferences
     * @return An array containing the two coordinate values
     */
    public static double[] getLocationCoordinates(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        double[] coordinates = new double[2];
        coordinates[0] = Double.longBitsToDouble(preferences.getLong(PREF_COORD_LAT, Double.doubleToRawLongBits(0.0)));
        coordinates[1] = Double.longBitsToDouble(preferences.getLong(PREF_COORD_LON, Double.doubleToRawLongBits(0.0)));

        return coordinates;
    }

    /**
     * Returns true if the latitude and longitude values are available.
     *
     * @param context Context used to get the SharedPreferences
     * @return true if lat/long are set
     */
    public static boolean isLocationAvailable(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean prefContainsLat = preferences.contains(PREF_COORD_LAT);
        boolean prefContainsLon = preferences.contains(PREF_COORD_LON);

        return prefContainsLat && prefContainsLon;
    }
}
