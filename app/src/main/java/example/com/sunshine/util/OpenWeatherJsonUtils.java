package example.com.sunshine.util;

import android.content.ContentValues;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import example.com.sunshine.data.SunshinePreferences;
import example.com.sunshine.data.contract.WeatherContract;

public class OpenWeatherJsonUtils {

    private static final String OWM_CITY = "city";
    private static final String OWM_COORD = "coord";
    private static final String OWM_LATITUDE = "lat";
    private static final String OWM_LONGITUDE = "lon";
    private static final String OWM_LIST = "list";
    private static final String OWM_PRESSURE = "pressure";
    private static final String OWM_HUMIDITY = "humidity";
    private static final String OWM_WINDSPEED = "speed";
    private static final String OWM_WIND_DIRECTION = "deg";
    private static final String OWM_TEMPERATURE = "temp";
    private static final String OWM_MAX = "max";
    private static final String OWM_MIN = "min";
    private static final String OWM_WEATHER = "weather";
    private static final String OWM_WEATHER_ID = "id";
    private static final String OWM_MESSAGE_CODE = "cod";

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the weather over various days from the forecast.
     *
     * @param json JSON response from server
     * @return Array of Strings describing weather data
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ContentValues[] parseJsonFromWebResponse(Context context, String json) throws JSONException {
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
        JSONObject cityObj = forecastJson.getJSONObject(OWM_CITY);
        JSONObject cityCoord = cityObj.getJSONObject(OWM_COORD);
        double cityLat = cityCoord.getDouble(OWM_LATITUDE);
        double cityLon = cityCoord.getDouble(OWM_LONGITUDE);
        long normalizedDay = SunshineDateUtils.getNormalizedUtcDateForToday();
        ContentValues[] values = new ContentValues[weatherArray.length()];

        SunshinePreferences.setLocationDetails(context, cityLat, cityLon);

        for (int i = 0; i < weatherArray.length(); i++) {
            long dateTimeMillis;
            int weatherId;
            int humidity;
            double pressure;
            double windSpeed;
            double windDirection;
            double high;
            double low;

            JSONObject dayForecast = weatherArray.getJSONObject(i);
            JSONObject weatherObj = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            JSONObject temperatureObj = dayForecast.getJSONObject(OWM_TEMPERATURE);

            dateTimeMillis = normalizedDay + SunshineDateUtils.DAY_IN_MILLIS * i;
            weatherId = weatherObj.getInt(OWM_WEATHER_ID);
            humidity = dayForecast.getInt(OWM_HUMIDITY);
            pressure = dayForecast.getDouble(OWM_PRESSURE);
            windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
            windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);
            high = temperatureObj.getDouble(OWM_MAX);
            low = temperatureObj.getDouble(OWM_MIN);

            ContentValues vals = new ContentValues();

            vals.put(WeatherContract.WeatherEntry.COLUMN_DATE, dateTimeMillis);
            vals.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
            vals.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
            vals.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
            vals.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, windDirection);
            vals.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, high);
            vals.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, low);
            vals.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherId);

            values[i] = vals;

        }

        return values;
    }
}
