package example.com.sunshine.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import example.com.sunshine.data.contract.WeatherContract;
import example.com.sunshine.util.NetworkUtils;
import example.com.sunshine.util.OpenWeatherJsonUtils;

public class SunshineSyncTask {

    /**
     * Performs the network request for updated weather, parses the JSON from that request, and
     * inserts the new weather information into our ContentProvider. Will notify the user that new
     * weather has been loaded if the user hasn't been notified of the weather within the last day
     * AND they haven't disabled notifications in the preferences screen.
     *
     * @param context Used to access utility methods and the ContentResolver
     */
    synchronized public static void syncWeather(Context context) {
        try {
            URL requestUrl = NetworkUtils.getUrl(context);
            String jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);
            ContentValues[] values = OpenWeatherJsonUtils.parseJsonFromWebResponse(context, jsonResponse);

            Log.v("SunshineSyncTask", String.valueOf(values.length));

            if (values != null && values.length != 0) {
                ContentResolver contentResolver = context.getContentResolver();
                contentResolver.delete(
                    WeatherContract.WeatherEntry.CONTENT_URI,
                    null,
                    null
                );
                contentResolver.bulkInsert(
                    WeatherContract.WeatherEntry.CONTENT_URI,
                    values
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
