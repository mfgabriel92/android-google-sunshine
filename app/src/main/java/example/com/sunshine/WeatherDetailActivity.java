package example.com.sunshine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
}
