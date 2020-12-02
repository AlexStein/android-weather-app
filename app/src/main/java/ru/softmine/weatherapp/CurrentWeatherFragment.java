package ru.softmine.weatherapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import ru.softmine.weatherapp.openweathermodel.WeatherRequest;
import ru.softmine.weatherapp.openweathermodel.WeatherRequestException;

public class CurrentWeatherFragment extends Fragment {

    private static final String TAG = CurrentWeatherFragment.class.getName();

    private boolean isLandscape;

    private TextView cityNameTextView;
    private TextView forecastTextView;
    private TextView tempsTextView;
    private TextView windTextView;
    private ImageView weatherIconImageView;

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

        // Первый запуск, заполняем значениями по умолчанию
        if (savedInstanceState == null) {
            setCity(getString(R.string.moscow_city));
        }

        // Обновить сводку погоды по нажатию н иконку погоды
        weatherIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCurrentWeather(CityModel.getInstance().getCityName());
            }
        });

        return view;
    }

    private void updateCurrentWeather(String cityName) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            public void run() {
                try {
                    WeatherRequest request = WeatherRequest.getCurrentWeather(cityName);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateWeatherOnDisplay(request);
                        }
                    });
                } catch (WeatherRequestException e) {
                    if (Logger.DEBUG && e.getMessage() != null) {
                        Log.d(TAG, e.getMessage());
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String message = getString(R.string.weather_request_error_message);
                            showErrorDialog(message);
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * Диалог для вывода сообщения об ошибках при получении сводки погоды
     *
     * @param message Сообщение в теле диалога
     */
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.error_dialog_title))
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Logger.VERBOSE) {
                            Log.v(TAG, "Error dialog OK pressed");
                        }
                    }
                }).show();
    }

    private void updateWeatherOnDisplay(WeatherRequest request) {
        forecastTextView.setText(request.getWeatherString());
        tempsTextView.setText(request.getTemperatureString());
        windTextView.setText(request.getWindString());

        // Иконку будет выставлять в зависимости от значения forecast
        weatherIconImageView.setImageResource(R.drawable.sunny);
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
            WeekForecastFragment forecast = (WeekForecastFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
            if (forecast == null) {
                forecast = WeekForecastFragment.createFragment();

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, forecast);  // замена фрагмента
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        }
    }

    public void setCity(String cityName) {
        CityModel.getInstance().setCityName(cityName);
        cityNameTextView.setText(cityName);
        updateCurrentWeather(cityName);
    }
}