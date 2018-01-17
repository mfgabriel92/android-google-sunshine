package example.com.sunshine.data.contract;

import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import example.com.sunshine.util.SunshineDateUtils;

public class WeatherContract {

    public static final String CONTENT_AUTHORITY = "example.com.sunshine";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_WEATHER = "weather";

    public static final class WeatherEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();
        public static final String TABLE_NAME = "weather";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_WEATHER_ID = "weatherId";
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_WIND_SPEED = "wind";
        public static final String COLUMN_DEGREES = "degrees";
        public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_DATE + " INTEGER UNIQUE NOT NULL," +
                COLUMN_WEATHER_ID + " INTEGER NOT NULL," +
                COLUMN_MIN_TEMP + " REAL NOT NULL," +
                COLUMN_MAX_TEMP + " REAL NOT NULL," +
                COLUMN_HUMIDITY + " REAL NOT NULL," +
                COLUMN_PRESSURE + " REAL NOT NULL," +
                COLUMN_WIND_SPEED + " REAL NOT NULL," +
                COLUMN_DEGREES + " REAL NOT NULL" +
            ");";
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

        /**
         * Builds a URI that adds the weather date to the end of the forecast content URI path.
         * This is used to query details about a single weather entry by date. This is what we
         * use for the detail view query. We assume a normalized date is passed to this method.
         *
         * @param date Normalized date in milliseconds
         * @return Uri to query details about a single weather entry
         */
        public static Uri buildWeatherUriWithDate(long date) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(date)).build();
        }

        /**
         * Returns just the selection part of the weather query from a normalized today value.
         * This is used to get a weather forecast from today's date. To make this easy to use
         * in compound selection, we embed today's date as an argument in the query.
         *
         * @return The selection part of the weather query for today onwards
         */
        public static String getSqlSelectForTodayOnwards() {
            return COLUMN_DATE + " >= " + SunshineDateUtils.normalizeDate(System.currentTimeMillis());
        }
    }
}
