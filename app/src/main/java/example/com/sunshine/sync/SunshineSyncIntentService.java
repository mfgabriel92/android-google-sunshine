package example.com.sunshine.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import example.com.sunshine.util.SunshineWeatherUtils;

public class SunshineSyncIntentService extends IntentService {

    public SunshineSyncIntentService() {
        super(SunshineWeatherUtils.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SunshineSyncTask.syncWeather(this);
    }
}
