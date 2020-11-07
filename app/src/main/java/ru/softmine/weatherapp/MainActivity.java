package ru.softmine.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final boolean DEBUG = true;
    private static final String TAG = MainActivity.class.getName();

    private TextView cityNameTextView;
    private TextView forecastTextView;
    private TextView tempsTextView;
    private ImageView weatherIconImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (DEBUG) {
            Log.d(TAG, "onCreate()");
            if (savedInstanceState != null) {
                Log.d(TAG, "Повторный запуск.");
            } else {
                Log.d(TAG, "Первый запуск.");
            }
        }

        Toast.makeText(getApplicationContext(), "onCreate()", Toast.LENGTH_SHORT).show();

        cityNameTextView = findViewById(R.id.cityNameTextView);
        forecastTextView = findViewById(R.id.forecastTextView);
        tempsTextView = findViewById(R.id.tempsTextView);
        weatherIconImageView = findViewById(R.id.weatherIconImageView);

        // Первый запуск, заполняем значениями по умолчанию
        if (savedInstanceState == null) {
            String cityName = getResources().getString(R.string.moscow_city);
            String forecast = getResources().getString(R.string.forecastSunny);;
            String temperature = getResources().getString(R.string.temperature_example);

            forecastTextView.setText(forecast);
            tempsTextView.setText(temperature);
            // Иконку будет выставлять в зависимости от значения forecast
            weatherIconImageView.setImageResource(R.drawable.sunny);

            CityPresenter.getInstance().setCityName(cityName);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (DEBUG) {
            Log.d(TAG, "onRestart()");
        }

        Toast.makeText(getApplicationContext(), "onRestart()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (DEBUG) {
            Log.d(TAG, "onStart()");
        }

        Toast.makeText(getApplicationContext(), "onStart()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (DEBUG) {
            Log.d(TAG, "onRestoreInstanceState()");
        }

        Toast.makeText(getApplicationContext(), "onRestoreInstanceState()", Toast.LENGTH_SHORT).show();

        // При восстановлении состояния выводим значение температуры и прогноза
        String forecast = savedInstanceState.getString(BundleKeys.FORECAST);
        String temperature = savedInstanceState.getString(BundleKeys.TEMPERATURE);

        forecastTextView.setText(forecast);
        tempsTextView.setText(temperature);
        // Иконку будет выставлять в зависимости от значения forecast
        weatherIconImageView.setImageResource(R.drawable.sunny);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (DEBUG) {
            Log.d(TAG, "onResume()");
        }

        Toast.makeText(getApplicationContext(), "onResume()", Toast.LENGTH_SHORT).show();

        cityNameTextView.setText(CityPresenter.getInstance().getCityName());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (DEBUG) {
            Log.d(TAG, "onSaveInstanceState()");
        }

        Toast.makeText(getApplicationContext(), "onSaveInstanceState()", Toast.LENGTH_SHORT).show();

        // Сохраняем значения температуры и прогноза
        String forecast = forecastTextView.getText().toString();
        String temperature = tempsTextView.getText().toString();

        outState.putString(BundleKeys.FORECAST, forecast);
        outState.putString(BundleKeys.TEMPERATURE, temperature);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (DEBUG) {
            Log.d(TAG, "onStop()");
        }

        Toast.makeText(getApplicationContext(), "onStop()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (DEBUG) {
            Log.d(TAG, "onDestroy()");
        }

        Toast.makeText(getApplicationContext(), "onDestroy()", Toast.LENGTH_SHORT).show();
    }

    /**
     * Переход на выбор города, по клику на наименовании текущего города
     *
     * @param view TextView с именем города @id/cityName
     */
    public void cityOnClick(View view) {
        Intent intent = new Intent(this, SelectCityActivity.class);
        startActivity(intent);
    }
}