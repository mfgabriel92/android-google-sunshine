package example.com.sunshine.data.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import example.com.sunshine.data.contract.DatabaseHelper;
import example.com.sunshine.data.contract.WeatherContract;
import example.com.sunshine.util.SunshineDateUtils;

/**
 * This class serves as the ContentProvider for all of Sunshine's data. This class allows us to
 * bulkInsert data, query data, and delete data.
 * <p>
 * Although ContentProvider implementation requires the implementation of additional methods to
 * perform single inserts, updates, and the ability to get the type of the data from a URI.
 */
public class WeatherProvider extends ContentProvider {

    public static final int CODE_WEATHER = 100;
    public static final int CODE_WEATHER_WITH_DATE = 101;

    private static final UriMatcher sUriMatcher = buildUriMather();
    private DatabaseHelper mDbHelper;

    /**
     * In onCreate, we initialize our content provider on startup. This method is called for all
     * registered content providers on the application main thread at application launch time.
     * It must not perform lengthy operations, or application startup will be delayed.
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new DatabaseHelper(getContext());
        return true;
    }

    /**
     * Handles query requests from clients. We will use this method in Sunshine to query for all
     * of our weather data as well as to query for the weather on a particular day.
     *
     * @param uri The URI to query
     * @param projection The list of columns to put into the cursor. If null, all columns are included.
     * @param selection A selection criteria to apply when filtering rows. If null, then all rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     * the values from selectionArgs, in order that they appear in the
     * selection.
     * @param sortOrder How the rows in the cursor should be sorted.
     * @return A Cursor containing the results of the query. In our implementation,
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case CODE_WEATHER: {
                cursor = mDbHelper.getReadableDatabase().query(
                    WeatherContract.WeatherEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                );

                break;
            }
            case CODE_WEATHER_WITH_DATE: {
                String normalizedUtcDateString = uri.getLastPathSegment();

                cursor = mDbHelper.getReadableDatabase().query(
                    WeatherContract.WeatherEntry.TABLE_NAME,
                    projection,
                    WeatherContract.WeatherEntry.COLUMN_DATE + " = ?",
                    new String[]{normalizedUtcDateString},
                    null,
                    null,
                    sortOrder
                );

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    /**
     * Handles requests to insert a set of new rows. In Sunshine, we are only going to be
     * inserting multiple rows of data at a time from a weather forecast.
     *
     * @param uri The content:// URI of the insertion request.
     * @param values An array of sets of column_name/value pairs to add to the database.
     * @return The number of values that were inserted.
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase mDb = mDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case CODE_WEATHER: {
                mDb.beginTransaction();

                int rowsInserted = 0;

                try {
                    for (ContentValues value : values) {
                        long weatherDate = value.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE);

                        if (!SunshineDateUtils.isDateNormalized(weatherDate)) {
                            throw new IllegalArgumentException("Dates must be normalized");
                        }

                        long _id = mDb.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value);

                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }

                    mDb.setTransactionSuccessful();
                } finally {
                    mDb.endTransaction();
                }

                if (rowsInserted > 1) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    /**
     * Creates the UriMatcher that will match each URI to the CODE_WEATHER and
     * CODE_WEATHER_WITH_DATE constants defined above
     *
     * @return A UriMatcher that correctly matches the constants for CODE_WEATHER and CODE_WEATHER_WITH_DATE
     */
    public static UriMatcher buildUriMather() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WeatherContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, WeatherContract.PATH_WEATHER, CODE_WEATHER);
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/#", CODE_WEATHER_WITH_DATE);

        return matcher;
    }
}
