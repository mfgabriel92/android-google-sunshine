package example.com.sunshine.data.contract;

import android.provider.BaseColumns;

public class WeatherContract {

    public static final class WeatherEntry implements BaseColumns {

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
    }
}
