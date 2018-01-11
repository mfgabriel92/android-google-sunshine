package example.com.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.TextView;

public class WeatherDetailActivity extends AppCompatActivity {

    private TextView mTvWeatherDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);

        mTvWeatherDetail = findViewById(R.id.tvWeatherDetail);

        if (getIntent().hasExtra(Intent.EXTRA_TEXT)) {
            mTvWeatherDetail.setText(getIntent().getStringExtra(Intent.EXTRA_TEXT));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weather_detail_menu, menu);

        menu.findItem(R.id.menu_action_share)
            .setIntent(createShareForecastIntent());

        return true;
    }

    /**
     * Uses the ShareCompat Intent builder to create our Forecast intent for sharing. We set the
     * type of content that we are sharing (just regular text), the text itself, and we return the
     * newly created Intent.
     *
     * @return The Intent to use to start our share.
     */
    private Intent createShareForecastIntent() {
        return ShareCompat.IntentBuilder
            .from(this)
            .setType("text/plain")
            .setText(getIntent().getStringExtra(Intent.EXTRA_TEXT))
            .getIntent();
    }
}
