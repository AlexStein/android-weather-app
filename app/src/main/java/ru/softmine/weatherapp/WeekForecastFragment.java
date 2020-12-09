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

import ru.softmine.weatherapp.cities.CityModel;
import ru.softmine.weatherapp.constants.Logger;
import ru.softmine.weatherapp.forecast.ForecastAdapter;
import ru.softmine.weatherapp.forecast.ForecastItem;
import ru.softmine.weatherapp.forecast.ForecastSource;
import ru.softmine.weatherapp.interfaces.OnFragmentErrorListener;
import ru.softmine.weatherapp.interfaces.Updatable;
import ru.softmine.weatherapp.interfaces.WeatherDataSource;
import ru.softmine.weatherapp.openweathermodel.WeatherRequest;
import ru.softmine.weatherapp.openweathermodel.WeatherRequestException;

public class WeekForecastFragment extends Fragment implements Updatable {

    private static final String TAG = WeekForecastFragment.class.getName();

    private WeatherDataSource source;
    private RecyclerView recyclerView;
    private ForecastAdapter adapter;

    private OnFragmentErrorListener errorListener;

    public static WeekForecastFragment createFragment() {
        if (Logger.DEBUG) {
            Log.d(TAG, "createFragment()");
        }

        return new WeekForecastFragment();
    }

    public void setOnErrorListener(OnFragmentErrorListener listener) {
        this.errorListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (Logger.DEBUG) {
            Log.d(TAG, "onCreateView()");
        }

        View view = inflater.inflate(R.layout.fragment_week_forecast_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (Logger.DEBUG) {
            Log.d(TAG, "onViewCreated()");
        }

        source = new ForecastSource();
        initRecyclerView(source.getDataSource());

        update();
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
                            source = new ForecastSource().init(request.getDaily());
                            adapter.update(source.getDataSource());
                            adapter.notifyDataSetChanged();
                            if (Logger.DEBUG) {
                                Log.d(TAG, "notifyDataSetChanged");
                            }
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
        adapter = new ForecastAdapter(sourceData);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void update() {
        updateCurrentWeather(CityModel.getInstance().getCityName());
    }
}
