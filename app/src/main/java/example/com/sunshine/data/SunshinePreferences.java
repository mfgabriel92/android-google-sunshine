package example.com.sunshine.data;

import android.content.Context;

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
        return getDefaultWeatherLocation();
    }

    /**
     * Returns true if the user has selected metric temperature display.
     *
     * @param context Context used to get the SharedPreferences
     * @return true If metric display should be used
     */
    public static boolean isMetric(Context context) {
        return true;
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
