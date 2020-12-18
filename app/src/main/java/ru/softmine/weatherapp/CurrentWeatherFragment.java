package ru.softmine.weatherapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import ru.softmine.weatherapp.constants.BundleKeys;
import ru.softmine.weatherapp.constants.Logger;
import ru.softmine.weatherapp.custom.ThermometerView;
import ru.softmine.weatherapp.history.HistoryDataSource;
import ru.softmine.weatherapp.interfaces.WeatherObserver;
import ru.softmine.weatherapp.openweathermodel.WeatherParser;

public class CurrentWeatherFragment extends Fragment implements WeatherObserver {

    private static final String TAG = CurrentWeatherFragment.class.getName();

    private TextView cityNameTextView;
    private TextView forecastTextView;
    private TextView tempsTextView;
    private TextView windTextView;
    private ImageView weatherIconImageView;

    private ThermometerView thermometerView;

    public CurrentWeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_weather, container, false);

        if (Logger.DEBUG) {
            Log.d(TAG, "onCreateView()");
        }

        WeatherApp.getWeatherParser().addObserver(this);

        cityNameTextView = view.findViewById(R.id.cityNameTextView);
        forecastTextView = view.findViewById(R.id.forecastTextView);
        tempsTextView = view.findViewById(R.id.tempsTextView);
        windTextView = view.findViewById(R.id.windTextView);
        weatherIconImageView = view.findViewById(R.id.weatherIconImageView);

        // Термометр
        thermometerView = view.findViewById(R.id.thermometerView);

        return view;
    }

    private void updateWeatherOnDisplay(WeatherParser weatherParser) {
        if (Logger.DEBUG) {
            Log.d(TAG, "updateWeatherOnDisplay()");
        }
        cityNameTextView.setText(weatherParser.getCityName());

        // Проверим, а была ли получена погода
        if (weatherParser.getCurrent().getWeather() == null) {
            return;
        }

        forecastTextView.setText(weatherParser.getWeatherString());
        tempsTextView.setText(weatherParser.getTemperatureString());
        windTextView.setText(weatherParser.getWindString());

        HistoryDataSource.updateHistoryItem(weatherParser.getCityName(),
                weatherParser.getTemperatureString(),
                weatherParser.getWeatherString(),
                weatherParser.getWindString());

        // Иконку будет выставлять в зависимости от значения forecast
        Picasso.get().load(weatherParser.getIcon())
                .placeholder(R.drawable.unknown)
                .into(weatherIconImageView);

        // Установить уровень на градуснике
        thermometerView.setLevel(weatherParser.getTemperature());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (Logger.DEBUG) {
            Log.d(TAG, "onViewStateRestored()");
        }
        
        if (savedInstanceState != null) {
            // При восстановлении состояния выводим значение температуры и прогноза
            String forecast = savedInstanceState.getString(BundleKeys.FORECAST);
            String temperature = savedInstanceState.getString(BundleKeys.TEMPERATURE_STRING);
            String wind = savedInstanceState.getString(BundleKeys.WIND);

            forecastTextView.setText(forecast);
            tempsTextView.setText(temperature);
            windTextView.setText(wind);

            // Иконку будет выставлять в зависимости от значения forecast
            weatherIconImageView.setImageResource(R.drawable.unknown);

            // Установить уровень на градуснике
            int tempLevel = savedInstanceState.getInt(BundleKeys.TEMPERATURE);
            thermometerView.setLevel(tempLevel);
        }
        updateWeatherOnDisplay(WeatherApp.getWeatherParser());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (Logger.DEBUG) {
            Log.d(TAG, "onSaveInstanceState()");
        }

        // Сохраняем значения температуры и прогноза
        String forecast = forecastTextView.getText().toString();
        String temperature = tempsTextView.getText().toString();
        String wind = windTextView.getText().toString();
        int tempLevel = thermometerView.getLevel();

        outState.putString(BundleKeys.FORECAST, forecast);
        outState.putString(BundleKeys.TEMPERATURE_STRING, temperature);
        outState.putString(BundleKeys.WIND, wind);
        outState.putInt(BundleKeys.TEMPERATURE, tempLevel);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onWeatherDataChanged() {
        updateWeatherOnDisplay(WeatherApp.getWeatherParser());
    }
}