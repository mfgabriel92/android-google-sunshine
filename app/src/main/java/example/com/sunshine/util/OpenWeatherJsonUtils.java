package example.com.sunshine.util;

import android.content.ContentValues;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class OpenWeatherJsonUtils {

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the weather over various days from the forecast.
     *
     * @param json JSON response from server
     * @return Array of Strings describing weather data
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static String[] parseJsonFromWebResponse(Context context, String json) throws JSONException {
        final String OWM_LIST = "list";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_WEATHER = "weather";
        final String OWM_DESCRIPTION = "main";
        final String OWM_MESSAGE_CODE = "cod";
        String[] parsedWeatherData;
        JSONObject forecastJson = new JSONObject(json);

        if (forecastJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
        parsedWeatherData = new String[weatherArray.length()];
        long localDate = System.currentTimeMillis();
        long utcDate = SunshineDateUtils.getUTCDateFromLocal(localDate);
        long startDay = SunshineDateUtils.normalizeDate(utcDate);

        for (int i = 0; i < weatherArray.length(); i++) {
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            long datetimeInMillis = startDay + SunshineDateUtils.DAY_IN_MILLIS * i;
            String date = SunshineDateUtils.getFriendlyDateString(context, datetimeInMillis, false);

            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);

            String description = weatherObject.getString(OWM_DESCRIPTION);

            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);
            String highAndLow = SunshineWeatherUtils.formatHighLow(context, high, low);

            parsedWeatherData[i] = date + " - " + description + " - " + highAndLow;
        }

        return parsedWeatherData;
    }

    /**
     * Parse the JSON and convert it into ContentValues that can be inserted into our database.
     *
     * @param context An application context, such as a service or activity context.
     * @param json The JSON to parse into ContentValues.
     * @return An array of ContentValues parsed from the JSON.
     */
    public static ContentValues[] getFullWeatherDataFromJson(Context context, String json) {
        return null;
    }
}
