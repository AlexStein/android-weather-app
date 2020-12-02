package ru.softmine.weatherapp;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.softmine.weatherapp.forecast.ForecastAdapter;
import ru.softmine.weatherapp.forecast.ForecastItem;
import ru.softmine.weatherapp.forecast.ForecastSource;
import ru.softmine.weatherapp.openweathermodel.WeatherRequest;
import ru.softmine.weatherapp.openweathermodel.WeatherRequestException;

public class WeekForecastFragment extends Fragment {

    private static final String TAG = WeekForecastFragment.class.getName();
    private RecyclerView recyclerView;

    public static WeekForecastFragment createFragment() {
        if (Logger.DEBUG) {
            Log.d(TAG, "createFragment()");
        }

        return new WeekForecastFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (Logger.DEBUG) {
            Log.d(TAG, "onCreateView()");
        }
        return inflater.inflate(R.layout.fragment_week_forecast_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (Logger.DEBUG) {
            Log.d(TAG, "onViewCreated()");
        }
        recyclerView = view.findViewById(R.id.recycler_view);

        updateCurrentWeather(CityModel.getInstance().getCityName());
    }

    /**
     * Инициализация источника данных
     */
    private void initDataSource(WeatherRequest request) {
        if (Logger.DEBUG) {
            Log.d(TAG, "initDataSource()");
        }

        WeatherDataSource source = new ForecastSource(getResources()).init(request.getDaily());
        initRecyclerView(source.getDataSource());
    }

    private void updateCurrentWeather(String cityName) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            public void run() {
                try {
                    WeatherRequest request = WeatherRequest.getDailyWeather(cityName);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            initDataSource(request);
                        }
                    });
                } catch (WeatherRequestException e) {
                    if (Logger.DEBUG && e.getMessage() != null) {
                        Log.d(TAG, e.getMessage());
                    }
                }

            }
        }).start();
    }

    private void initRecyclerView(List<ForecastItem> sourceData) {
        if (Logger.DEBUG) {
            Log.d(TAG, "initRecyclerView()");
        }

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // Декоратор
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.decorator, getActivity().getTheme()));
        recyclerView.addItemDecoration(itemDecoration);

        // Адаптер
        ForecastAdapter adapter = new ForecastAdapter(sourceData);
        recyclerView.setAdapter(adapter);
    }
}
