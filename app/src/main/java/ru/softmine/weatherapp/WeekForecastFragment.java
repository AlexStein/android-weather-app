package ru.softmine.weatherapp;

import android.os.Bundle;
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
        initDataSource();
    }

    /**
     * Инициализация источника данных
     */
    private void initDataSource() {
        if (Logger.DEBUG) {
            Log.d(TAG, "initDataSource()");
        }

        WeatherDataSource source = new ForecastSource(getResources()).init();
        initRecyclerView(source.getDataSource());
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
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.decorator));
        recyclerView.addItemDecoration(itemDecoration);

        // Адаптер
        ForecastAdapter adapter = new ForecastAdapter(sourceData);
        recyclerView.setAdapter(adapter);
    }
}
