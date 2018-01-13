package example.com.sunshine.data;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import example.com.sunshine.data.contract.DatabaseHelper;
import example.com.sunshine.data.contract.WeatherContract;
import example.com.sunshine.data.provider.WeatherProvider;

import static example.com.sunshine.data.TestUtilities.BULK_INSERT_RECORDS_TO_INSERT;
import static example.com.sunshine.data.TestUtilities.createBulkInsertTestWeatherValues;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Although these tests aren't a complete set of tests one should run on a ContentProvider
 * implementation, they do test that the basic functionality of Sunshine's ContentProvider is
 * working properly.
 * <p>
 * In this test suite, we have the following tests:
 * <p>
 *   1) A test to ensure that your ContentProvider has been properly registered in the
 *    AndroidManifest
 * <p>
 *   2) A test to determine if you've implemented the query functionality for your
 *    ContentProvider properly
 * <p>
 *   3) A test to determine if you've implemented the bulkInsert functionality of your
 *    ContentProvider properly
 * <p>
 *   4) A test to determine if you've implemented the delete functionality of your
 *    ContentProvider properly.
 * <p>
 * If any of these tests fail, you should see useful error messages in the testing console's
 * output window.
 * <p>
 * Finally, we have a method annotated with the @Before annotation, which tells the test runner
 * that the {@link #setUp()} method should be called before every method annotated with a @Test
 * annotation. In our setUp method, all we do is delete all records from the database to start our
 * tests with a clean slate each time.
 */
@RunWith(AndroidJUnit4.class)
public class TestWeatherProvider {

    private final Context mContext = InstrumentationRegistry.getTargetContext();

    @Before
    public void setUp() {
        deleteAllRecordsFromWeatherTable();
    }

    /**
     * This test checks to make sure that the content provider is registered correctly in the
     * AndroidManifest file. If it fails, you should check the AndroidManifest to see if you've
     * added a <provider/> tag and that you've properly specified the android:authorities attribute.
     * <p>
     * Potential causes for failure:
     * <p>
     *   1) Your WeatherProvider was registered with the incorrect authority
     * <p>
     *   2) Your WeatherProvider was not registered at all
     */
    @Test
    public void testProviderRegistry() {
        String packageName = mContext.getPackageName();
        String weatherProviderClassName = WeatherProvider.class.getName();
        ComponentName componentName = new ComponentName(packageName, weatherProviderClassName);

        try {
            PackageManager pm = mContext.getPackageManager();
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            String actualAuthority = providerInfo.authority;
            String expectedAuthority = WeatherContract.CONTENT_AUTHORITY;

            assertEquals(
                "Error: WeatherProvider registered with authority: " + actualAuthority + " instead of expected authority: " + expectedAuthority,
                actualAuthority,
                expectedAuthority
            );

        } catch (PackageManager.NameNotFoundException e) {
            fail("Error: WeatherProvider not registered at " + mContext.getPackageName());
        }
    }

    /**
     * This test uses the database directly to insert a row of test data and then uses the
     * ContentProvider to read out the data. We access the database directly to insert the data
     * because we are testing our ContentProvider's query functionality. If we wanted to use the
     * ContentProvider's insert method, we would have to assume that that insert method was
     * working, which defeats the point of testing.
     * <p>
     * If this test fails, you should check the logic in your
     * {@link WeatherProvider#insert(Uri, ContentValues)} and make sure it matches up with our
     * solution code.
     * <p>
     * Potential causes for failure:
     * <p>
     *   1) There was a problem inserting data into the database directly via SQLite
     * <p>
     *   2) The values contained in the cursor did not match the values we inserted via SQLite
     */
    @Test
    public void testBasicWeatherQuery() {
        DatabaseHelper dbHelper = new DatabaseHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues testWeatherValues = TestUtilities.createTestWeatherContentValues();

        long weatherRowId = database.insert(
            WeatherContract.WeatherEntry.TABLE_NAME,
            null,
            testWeatherValues
        );

        String insertFailed = "Unable to insert into the database";
        assertTrue(insertFailed, weatherRowId != -1);

        database.close();

        Cursor weatherCursor = mContext.getContentResolver().query(
            WeatherContract.WeatherEntry.CONTENT_URI,
            null,
            null,
            null,
            null
        );

        TestUtilities.validateThenCloseCursor("testBasicWeatherQuery",
            weatherCursor,
            testWeatherValues
        );
    }

    /**
     * This test test the bulkInsert feature of the ContentProvider. It also verifies that
     * registered ContentObservers receive onChange callbacks when data is inserted.
     * <p>
     * It finally queries the ContentProvider to make sure that the table has been successfully
     * inserted.
     * <p>
     * Potential causes for failure:
     * <p>
     *   1) Within {@link WeatherProvider#delete(Uri, String, String[])}, you didn't call
     *    getContext().getContentResolver().notifyChange(uri, null) after performing an insertion.
     * <p>
     *   2) The number of records the ContentProvider reported that it inserted do no match the
     *    number of records we inserted into the ContentProvider.
     * <p>
     *   3) The size of the Cursor returned from the query does not match the number of records
     *    that we inserted into the ContentProvider.
     * <p>
     *   4) The data contained in the Cursor from our query does not match the data we inserted
     *    into the ContentProvider.
     * </p>
     */
    @Test
    public void testBulkInsert() {
        ContentValues[] bulkInsertTestContentValues = createBulkInsertTestWeatherValues();
        TestUtilities.TestContentObserver weatherObserver = TestUtilities.getTestContentObserver();
        ContentResolver contentResolver = mContext.getContentResolver();

        contentResolver.registerContentObserver(
            WeatherContract.WeatherEntry.CONTENT_URI,
            true,
            weatherObserver
        );

        int insertCount = contentResolver.bulkInsert(
            WeatherContract.WeatherEntry.CONTENT_URI,
            bulkInsertTestContentValues
        );

        contentResolver.unregisterContentObserver(weatherObserver);

        assertEquals(
            "Number of expected records inserted does not match actual inserted record count",
            insertCount,
            BULK_INSERT_RECORDS_TO_INSERT
        );

        Cursor cursor = mContext.getContentResolver().query(
            WeatherContract.WeatherEntry.CONTENT_URI,
            null,
            null,
            null,
            WeatherContract.WeatherEntry.COLUMN_DATE + " ASC"
        );

        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        cursor.moveToFirst();

        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext()) {
            TestUtilities.validateCurrentRecord(
                "testBulkInsert. Error validating WeatherEntry " + i,
                cursor,
                bulkInsertTestContentValues[i]
            );
        }

        cursor.close();
    }

    /**
     * This method will clear all rows from the weather table in our database.
     * <p>
     * Please note:
     * <p>
     * - This does NOT delete the table itself. We call this method from our @Before annotated
     * method to clear all records from the database before each test on the ContentProvider.
     * <p>
     * - We don't use the ContentProvider's delete functionality to perform this row deletion
     * because in this class, we are attempting to test the ContentProvider. We can't assume
     * that our ContentProvider's delete method works in our ContentProvider's test class.
     */
    private void deleteAllRecordsFromWeatherTable() {
        DatabaseHelper helper = new DatabaseHelper(InstrumentationRegistry.getTargetContext());
        SQLiteDatabase database = helper.getWritableDatabase();
        database.delete(WeatherContract.WeatherEntry.TABLE_NAME, null, null);
        database.close();
    }
}