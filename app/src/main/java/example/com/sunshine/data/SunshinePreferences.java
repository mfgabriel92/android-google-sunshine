package example.com.sunshine.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import example.com.sunshine.R;

public class SunshinePreferences {

    public static final String PREF_CITY_NAME = "city_name";
    public static final String PREF_COORD_LAT = "coord_lat";
    public static final String PREF_COORD_LON = "coord_lon";
    private static final String DEFAULT_WEATHER_LOCATION = "94043, USA";
    private static final String DEFAULT_MAP_LOCATION = "1600 Amphitheatre Parkway, Mountain View, CA 94043";
    private static final double[] DEFAULT_WEATHER_COORDINATES = {37.4284, 122.0724};

    /**
     * Helper method to handle setting location details in Preferences.
     *
     * @param context Context used to get the SharedPreferences
     * @param cityName A human-readable city name, e.g "Mountain View"
     * @param lat The latitude of the city
     * @param lon The longitude of the city
     */
    public static void setLocationDetails(Context context, String cityName, double lat, double lon) {
        /* */
    }

    /**
     * Helper method to handle setting a new location in preferences.
     * When this happens the database may need to be cleared
     *
     * @param context Context used to get the SharedPreferences
     * @param locationSetting The location string used to request updates from the server
     * @param lat The latitude of the city
     * @param lon The longitude of the city
     */
    public static void setLocation(Context context, String locationSetting, double lat, double lon) {
        /* */
    }

    /**
     * Resets the stored location coordinates.
     *
     * @param context Context used to get the SharedPreferences
     */
    public static void resetLocationCoordinates(Context context) {
        /* */
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
        return getDefaultWeatherCoordinates();
    }

    /**
     * Returns true if the latitude and longitude values are available.
     *
     * @param context Context used to get the SharedPreferences
     * @return true if lat/long are set
     */
    public static boolean isLocationAvailable(Context context) {
        return true;
    }

    /**
     * Returns the default coordinates
     *
     * @return An array containing the two coordinate values
     */
    private static double[] getDefaultWeatherCoordinates() {
        return DEFAULT_WEATHER_COORDINATES;
    }

    /**
     * Returns the default location
     *
     * @return String of the default location
     */
    private static String getDefaultWeatherLocation() {
        return DEFAULT_WEATHER_LOCATION;
    }
}
