package ru.softmine.weatherapp;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.softmine.weatherapp.forecast.ForecastAdapter;
import ru.softmine.weatherapp.forecast.ForecastItem;
import ru.softmine.weatherapp.forecast.ForecastSource;
import ru.softmine.weatherapp.interfaces.OnFragmentErrorListener;
import ru.softmine.weatherapp.interfaces.WeatherDataSource;
import ru.softmine.weatherapp.interfaces.WeatherObserver;

public class WeekForecastFragment extends Fragment implements WeatherObserver {

    private static final String TAG = WeekForecastFragment.class.getName();

    private WeatherDataSource source;
    private RecyclerView recyclerView;
    private ForecastAdapter adapter;

    private OnFragmentErrorListener errorListener;

    private Drawable decorator;

    public WeekForecastFragment() {
        // Required empty public constructor
    }

    public static WeekForecastFragment createFragment() {
        return new WeekForecastFragment();
    }

    public void setOnErrorListener(OnFragmentErrorListener listener) {
        this.errorListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week_forecast_list, container, false);
        decorator = ContextCompat.getDrawable(WeatherApp.getAppContext(), R.drawable.decorator);
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
        source = new ForecastSource();
        initRecyclerView(source.getDataSource());

        WeatherApp.getWeatherParser().addObserver(this);

        onWeatherDataChanged();
    }

    private void initRecyclerView(List<ForecastItem> sourceData) {
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(WeatherApp.getAppContext());
        recyclerView.setLayoutManager(layoutManager);

        // Декоратор
        DividerItemDecoration itemDecoration = new DividerItemDecoration(WeatherApp.getAppContext(),
                LinearLayoutManager.VERTICAL);
        itemDecoration.setDrawable(decorator);
        recyclerView.addItemDecoration(itemDecoration);

        // Адаптер
        adapter = new ForecastAdapter(sourceData);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onWeatherDataChanged() {
        source = new ForecastSource().init(WeatherApp.getWeatherParser().getDaily());
        adapter.update(source.getDataSource());
        adapter.notifyDataSetChanged();
    }
}
