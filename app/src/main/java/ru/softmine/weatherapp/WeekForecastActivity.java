package ru.softmine.weatherapp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class WeekForecastActivity extends AppCompatActivity {

    private static final String TAG = WeekForecastActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_forecast);

        if (Logger.DEBUG) {
            Log.d(TAG, "WeekForecastActivity onCreate()");
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Если устройство перевернули в альбомную ориентацию, то надо эту activity закрыть
            finish();
            return;
        }

        if (savedInstanceState == null) {
            if (Logger.DEBUG) {
                Log.d(TAG, "savedInstanceState null");
            }
            WeekForecastFragment forecast = new WeekForecastFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, forecast).commit();
        }
    }
}