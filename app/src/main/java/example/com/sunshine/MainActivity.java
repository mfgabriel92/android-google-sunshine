package example.com.sunshine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity implements MainActivityAdapter.MainActivityAdapterOnClickHandler {

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
            case R.id.menuActionRefresh:
                mMainActivityAdapter.setWeatherData(null);
                loadWeatherData();
                return true;
            case R.id.menuActionMap:
                openMapLocation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        String location = SunshinePreferences.getPreferredWeatherLocation(this);
        new WeatherQueryTask().execute(location);
    }

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

    /**
     * Execution of the task for retrieving weather results
     */
    @SuppressLint("StaticFieldLeak")
    public class WeatherQueryTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPbProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... strings) {
            if (strings.length == 0) {
                return null;
            }

            String location = strings[0];
            URL requestUrl = NetworkUtils.buildUrl(location);

            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);
                return OpenWeatherJsonUtils.parseJsonFromWebResponse(MainActivity.this, jsonResponse);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] data) {
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
    }
}
