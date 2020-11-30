package ru.softmine.weatherapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private static final int CITY_RESULT = 0xAA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Logger.DEBUG) {
            Log.d(TAG, "onCreate()");
            if (savedInstanceState != null) {
                Log.d(TAG, "Повторный запуск.");
            } else {
                Log.d(TAG, "Первый запуск.");
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (Logger.DEBUG) {
            Log.d(TAG, "onRestart()");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Logger.DEBUG) {
            Log.d(TAG, "onStart()");
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (Logger.DEBUG) {
            Log.d(TAG, "onRestoreInstanceState()");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Logger.DEBUG) {
            Log.d(TAG, "onResume()");
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (Logger.DEBUG) {
            Log.d(TAG, "onSaveInstanceState()");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (Logger.DEBUG) {
            Log.d(TAG, "onStop()");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (Logger.DEBUG) {
            Log.d(TAG, "onDestroy()");
        }
    }

    /**
     * Переход на выбор города, по клику на наименовании текущего города
     *
     * @param view TextView с именем города @id/cityName
     */
    public void cityOnClick(View view) {
        Intent intent = new Intent(this, SelectCityActivity.class);
        startActivityForResult(intent, CITY_RESULT);
    }

    /**
     * Переход на страницу с информацией о городе. Например на Wiki.
     *
     * @param view Button
     */
    public void onButtonInfoClick(View view) {
        String cityName = CityModel.getInstance().getCityName();
        String url = String.format(getString(R.string.wiki_url_format), cityName);

        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    /**
     * Переключить на активити с фрагментом списка прогноза на неделю
     *
     * @param view Button
     */
    public void onButtonDetailsClick(View view) {
        String cityName = CityModel.getInstance().getCityName();

        Intent intent = new Intent();
        intent.setClass(this, WeekForecastActivity.class);
        intent.putExtra(BundleKeys.CITY_NAME, cityName);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != CITY_RESULT) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (resultCode == RESULT_OK) {
            String cityName = data.getStringExtra(BundleKeys.CITY_NAME);
            String tempUnit = data.getStringExtra(BundleKeys.TEMP_UNITS);
            String speedUnit = data.getStringExtra(BundleKeys.SPEED_UNITS);

            CurrentWeatherFragment fragment = (CurrentWeatherFragment)
                    getSupportFragmentManager().findFragmentById(R.id.current_weather);
            fragment.setCity(cityName, tempUnit, speedUnit);
        }
    }
}