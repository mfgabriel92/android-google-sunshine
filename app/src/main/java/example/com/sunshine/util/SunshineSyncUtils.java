package example.com.sunshine.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import example.com.sunshine.data.contract.WeatherContract;
import example.com.sunshine.sync.SunshineSyncIntentService;

public class SunshineSyncUtils {

    private static boolean bInitialized;

    @SuppressLint("StaticFieldLeak")
    synchronized public static void initialize(@NonNull final Context context) {
        if (bInitialized) {
            return;
        }

        bInitialized = true;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Cursor cursor = context.getContentResolver().query(
                    WeatherContract.WeatherEntry.CONTENT_URI,
                    new String[]{WeatherContract.WeatherEntry._ID},
                    WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards(),
                    null,
                    null
                );

                if (cursor == null || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }

                cursor.close();

                return null;
            }
        }.execute();
    }

    /**
     * Helper method to perform a sync immediately using an IntentService for asynchronous
     * execution.
     *
     * @param context The Context used to start the IntentService for the sync.
     */
    public static void startImmediateSync(@NonNull final Context context) {
        Intent intent = new Intent(context, SunshineSyncIntentService.class);
        context.startService(intent);
    }
}
