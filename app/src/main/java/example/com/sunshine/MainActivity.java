package example.com.sunshine;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import example.com.sunshine.data.SunshinePreferences;
import example.com.sunshine.util.NetworkUtils;
import example.com.sunshine.util.OpenWeatherJsonUtils;

public class MainActivity extends AppCompatActivity {

    private TextView mTvDummyData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvDummyData = findViewById(R.id.tvDummyData);

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
            case R.id.menuRefresh:
                mTvDummyData.setText("");
                loadWeatherData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadWeatherData() {
        String location = SunshinePreferences.getPreferredWeatherLocation(this);
        new WeatherQueryTask().execute(location);
    }

    /**
     * Execution of the task for retrieving weather results
     */
    @SuppressLint("StaticFieldLeak")
    public class WeatherQueryTask extends AsyncTask<String, Void, String[]> {

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
            if (data != null) {
                for (String d : data) {
                    mTvDummyData.append(d + "\n\n\n");
                }
            }
        }
    }
}
