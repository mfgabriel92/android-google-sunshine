package example.com.sunshine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import example.com.sunshine.adapter.MainActivityAdapter;
import example.com.sunshine.data.SunshinePreferences;
import example.com.sunshine.util.NetworkUtils;
import example.com.sunshine.util.OpenWeatherJsonUtils;

public class MainActivity
    extends AppCompatActivity
    implements MainActivityAdapter.MainActivityAdapterOnClickHandler,
               LoaderManager.LoaderCallbacks<String[]>,
               SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "V/" + MainActivity.class.getSimpleName();
    private static final int FORECAST_WEATHER_ID = 0;
    private static boolean PREFERENCES_UPDATED = false;
    private TextView mTvErrorMessage;
    private ProgressBar mPbProgressBar;
    private RecyclerView mRvMainActivity;
    private MainActivityAdapter mMainActivityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvErrorMessage = findViewById(R.id.tvErrorMessage);
        mPbProgressBar = findViewById(R.id.pbProgressBar);
        mRvMainActivity = findViewById(R.id.rvMainActivity);
        mMainActivityAdapter = new MainActivityAdapter(this);

        setupLayoutManager();
        loadWeatherData();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * OnStart is called when the Activity is coming into view. This happens when the Activity is
     * first created, but also happens when the Activity is returned to from another Activity.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (PREFERENCES_UPDATED) {
            getSupportLoaderManager().restartLoader(FORECAST_WEATHER_ID, null, this);
            PREFERENCES_UPDATED = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menuMap:
                openMapLocation();
                return true;
            case R.id.menuSettings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<String[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {
            String[] mWeatherData = null;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                if (mWeatherData != null) {
                    deliverResult(mWeatherData);
                    return;
                }

                mPbProgressBar.setVisibility(View.VISIBLE);
                forceLoad();
            }

            @Override
            public String[] loadInBackground() {
                String locationQuery = SunshinePreferences.getPreferredWeatherLocation(MainActivity.this);
                URL requestUrl = NetworkUtils.buildUrl(locationQuery);

                try {
                    String jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);
                    return OpenWeatherJsonUtils.parseJsonFromWebResponse(MainActivity.this, jsonResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(String[] data) {
                super.deliverResult(data);
                mWeatherData = data;
            }
        };
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        mPbProgressBar.setVisibility(View.INVISIBLE);

        if (data == null) {
            mRvMainActivity.setVisibility(View.INVISIBLE);
            mTvErrorMessage.setVisibility(View.VISIBLE);
            return;
        }

        mMainActivityAdapter.setWeatherData(data);

        mRvMainActivity.setVisibility(View.VISIBLE);
        mTvErrorMessage.setVisibility(View.INVISIBLE);
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable. The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item
     * clicks from {@link MainActivityAdapter.MainActivityAdapterOnClickHandler}.
     *
     * @param data data to display
     */
    @Override
    public void onClickHandler(String data) {
        Intent weatherDetailIntent = new Intent(this, WeatherDetailActivity.class);
        weatherDetailIntent.putExtra(Intent.EXTRA_TEXT, data);

        startActivity(weatherDetailIntent);
    }

    /**
     * Sets up the LayoutManager
     */
    private void setupLayoutManager() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRvMainActivity.setLayoutManager(layoutManager);
        mRvMainActivity.setAdapter(mMainActivityAdapter);
    }

    /**
     * Loads the weather data by executing the inner class WeatherQueryTask
     */
    private void loadWeatherData() {
        getSupportLoaderManager().restartLoader(FORECAST_WEATHER_ID, null, MainActivity.this);
    }

    /**
     * Implicit intent that opens any apps that supports geo location, such as Google Maps
     */
    private void openMapLocation() {
        String address = SunshinePreferences.getPreferredWeatherLocation(this);
        Uri geo =  Uri.parse("geo:0,0?q" + address);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geo);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Could not call " + geo.toString() + ", no receiving apps installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void restartLoader() {}
}
