package ru.softmine.weatherapp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Random;

public class CurrentWeatherFragment extends Fragment {

    private static final String TAG = CurrentWeatherFragment.class.getName();

    private boolean isLandscape;

    private TextView cityNameTextView;
    private TextView forecastTextView;
    private TextView tempsTextView;
    private TextView windTextView;
    private ImageView weatherIconImageView;
    private Button buttonDetails;

    private final Random rand = new Random();

    public CurrentWeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_weather, container, false);

        cityNameTextView = view.findViewById(R.id.cityNameTextView);
        forecastTextView = view.findViewById(R.id.forecastTextView);
        tempsTextView = view.findViewById(R.id.tempsTextView);
        windTextView = view.findViewById(R.id.windTextView);
        weatherIconImageView = view.findViewById(R.id.weatherIconImageView);
        buttonDetails = view.findViewById(R.id.buttonDetails);

        buttonDetails.setVisibility(View.INVISIBLE);

        // Первый запуск, заполняем значениями по умолчанию
        if (savedInstanceState == null) {
            String cityName = getResources().getString(R.string.moscow_city);
            String forecast = getResources().getString(R.string.forecastSunny);;
            String temperature = getResources().getString(R.string.temperature_example);
            String windSpeed = getResources().getString(R.string.wind_example);

            forecastTextView.setText(forecast);
            tempsTextView.setText(temperature);
            windTextView.setText(windSpeed);
            // Иконку будет выставлять в зависимости от значения forecast
            weatherIconImageView.setImageResource(R.drawable.sunny);

            CityModel.getInstance().setCityName(cityName);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        isLandscape = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);

        if (Logger.DEBUG) {
            Log.d(TAG, "onActivityCreated()");
        }

        if (isLandscape) {
            showForecast();
        } else {
            buttonDetails.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        String cityName = CityModel.getInstance().getCityName();
        cityNameTextView.setText(cityName);

        if (savedInstanceState != null) {
            // При восстановлении состояния выводим значение температуры и прогноза
            String forecast = savedInstanceState.getString(BundleKeys.FORECAST);
            String temperature = savedInstanceState.getString(BundleKeys.TEMPERATURE);
            String wind = savedInstanceState.getString(BundleKeys.WIND);

            forecastTextView = getView().findViewById(R.id.forecastTextView);
            tempsTextView = getView().findViewById(R.id.tempsTextView);
            windTextView = getView().findViewById(R.id.windTextView);

            forecastTextView.setText(forecast);
            tempsTextView.setText(temperature);
            windTextView.setText(wind);

            // Иконку будет выставлять в зависимости от значения forecast
            weatherIconImageView.setImageResource(R.drawable.sunny);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (Logger.DEBUG) {
            Log.d(TAG, "onSaveInstanceState()");
        }

        forecastTextView = getView().findViewById(R.id.forecastTextView);
        tempsTextView = getView().findViewById(R.id.tempsTextView);
        windTextView = getView().findViewById(R.id.windTextView);

        // Сохраняем значения температуры и прогноза
        String forecast = forecastTextView.getText().toString();
        String temperature = tempsTextView.getText().toString();
        String wind = windTextView.getText().toString();

        outState.putString(BundleKeys.FORECAST, forecast);
        outState.putString(BundleKeys.TEMPERATURE, temperature);
        outState.putString(BundleKeys.WIND, wind);

        super.onSaveInstanceState(outState);
    }

    private void showForecast() {
        if (Logger.DEBUG) {
            Log.d(TAG, "showForecast()");
        }

        if (isLandscape) {
            WeekForecastFragment forecast = (WeekForecastFragment)getFragmentManager().findFragmentById(R.id.fragment_container);
            if (forecast == null) {
                forecast = WeekForecastFragment.createFragment();

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, forecast);  // замена фрагмента
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        }
    }

    public void setCity(String cityName, String tempUnit, String speedUnit) {
        int t_day = rand.nextInt(50) - 20;
        int t_night = t_day - rand.nextInt(10);

        int w_min = rand.nextInt(10);
        int w_max = rand.nextInt(5) + w_min;

        cityNameTextView.setText(cityName);
        tempsTextView.setText(String.format(getString(R.string.temp_format), t_day, t_night, tempUnit));
        windTextView.setText(String.format(getString(R.string.speed_format), w_min, w_max, speedUnit));
    }
}