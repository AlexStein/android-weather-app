package ru.softmine.weatherapp;

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

import ru.softmine.weatherapp.cities.CityModel;
import ru.softmine.weatherapp.constants.BundleKeys;
import ru.softmine.weatherapp.constants.Logger;
import ru.softmine.weatherapp.custom.ThermometerView;
import ru.softmine.weatherapp.history.HistoryDataSource;
import ru.softmine.weatherapp.interfaces.OnFragmentErrorListener;
import ru.softmine.weatherapp.interfaces.Updatable;
import ru.softmine.weatherapp.openweathermodel.WeatherRequest;
import ru.softmine.weatherapp.openweathermodel.WeatherRequestException;

public class CurrentWeatherFragment extends Fragment implements Updatable {

    private static final String TAG = CurrentWeatherFragment.class.getName();

    private OnFragmentErrorListener errorListener;

    private TextView cityNameTextView;
    private TextView forecastTextView;
    private TextView tempsTextView;
    private TextView windTextView;
    private ImageView weatherIconImageView;

    private ThermometerView thermometerView;

    public CurrentWeatherFragment() {
        // Required empty public constructor
    }

    public void setOnErrorListener(OnFragmentErrorListener listener) {
        this.errorListener = listener;
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

        // Термометр
        thermometerView = view.findViewById(R.id.thermometerView);

        // Первый запуск, заполняем значениями по умолчанию
        if (savedInstanceState == null) {
            setCity(getString(R.string.moscow_city));
            thermometerView.setLevel(0);
        }

        // Обновить сводку погоды по нажатию н иконку погоды
        weatherIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });

        try {
            errorListener = ((MainActivity) getActivity()).getErrorListener();
        } catch (NullPointerException e) {
            errorListener = new OnFragmentErrorListener() {
                @Override
                public void onFragmentError(String message) {
                    Log.e(TAG, message);
                }
            };
        }

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
                            errorListener.onFragmentError(e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    private void updateWeatherOnDisplay(WeatherRequest request) {
        forecastTextView.setText(request.getWeatherString());
        tempsTextView.setText(request.getTemperatureString());
        windTextView.setText(request.getWindString());

        // Обновим данные в истории
        String cityName = CityModel.getInstance().getCityName();
        if (Logger.DEBUG) {
            Log.d(TAG, "updateWeatherOnDisplay()");
        }
        HistoryDataSource.updateHistoryItem(cityName, request.getTemperatureString(),
                request.getWeatherString(), request.getWindString());

        // Иконку будет выставлять в зависимости от значения forecast
        weatherIconImageView.setImageResource(R.drawable.sunny);

        // Установить уровень на градуснике
        thermometerView.setLevel(request.getTemperature());
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

    public void setCity(String cityName) {
        CityModel.getInstance().setCityName(cityName);
        cityNameTextView.setText(cityName);
        updateCurrentWeather(cityName);
    }

    @Override
    public void update() {
        updateCurrentWeather(CityModel.getInstance().getCityName());
    }
}