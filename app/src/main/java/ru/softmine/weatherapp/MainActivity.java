package ru.softmine.weatherapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getName();
    private static final int CITY_RESULT = 0xAA;
    private static final int SETTING_CODE = 0xBB;

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

        if (savedInstanceState == null) {
            WeekForecastFragment forecast = new WeekForecastFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, forecast).commit();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != CITY_RESULT) {
            super.onActivityResult(requestCode, resultCode, data);

            // Вернулись из настроек
            if (requestCode == SETTING_CODE) {
                recreate();
            }
            return;
        }

        if (resultCode == RESULT_OK) {
            if (data == null) {
                if (Logger.DEBUG) {
                    Log.d(TAG, "onActivityResult: data is null");
                }
                return;
            }

            String cityName = data.getStringExtra(BundleKeys.CITY_NAME);
            CurrentWeatherFragment fragment = (CurrentWeatherFragment)
                    getSupportFragmentManager().findFragmentById(R.id.current_weather);
            if (fragment != null) {
                fragment.setCity(cityName);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivityForResult(intent, SETTING_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}