package ru.softmine.weatherapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ForecastAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ForecastAdapter.class.getName();

    SimpleDateFormat sdf = new SimpleDateFormat("EEE dd.MM", Locale.getDefault());

    private final List<ForecastItem> dataSource;

    public ForecastAdapter(List<ForecastItem> dataSource) {
        this.dataSource = dataSource;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.forecast_list_item, parent, false);
        RecyclerView.ViewHolder vh = new ForecastViewHolder(view);

        if (Logger.DEBUG) {
            Log.d(TAG, "onCreateViewHolder");
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ForecastItem i = dataSource.get(position);

        ((ForecastViewHolder) holder).setData(
                sdf.format(i.getDate()),
                R.drawable.sunny,
                String.format(Locale.getDefault(), "%d ... %d C", i.getTempMin(), i.getTempMax()),
                i.getCondition());
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}
