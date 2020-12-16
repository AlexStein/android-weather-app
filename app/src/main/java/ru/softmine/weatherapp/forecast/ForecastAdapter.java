package ru.softmine.weatherapp.forecast;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ru.softmine.weatherapp.R;
import ru.softmine.weatherapp.constants.Logger;

/**
 * Адаптер для прогноза погоды на несколько дней
 */
public class ForecastAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ForecastAdapter.class.getName();

    private Context context;

    private SimpleDateFormat sdf;
    private String tmf;
    private String temp_units;

    private final List<ForecastItem> dataSource;

    public ForecastAdapter(List<ForecastItem> dataSource) {
        this.dataSource = dataSource;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.forecast_list_item, parent, false);
        RecyclerView.ViewHolder vh = new ForecastViewHolder(view);

        sdf = new SimpleDateFormat(context.getString(R.string.day_fmt), Locale.getDefault());
        tmf = context.getString(R.string.temp_format);
        temp_units = context.getString(R.string.celsius);

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
                i.getIcon(),
                String.format(Locale.getDefault(), tmf, i.getTempMin(), i.getTempMax(), temp_units),
                i.getCondition());
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    public void update(List<ForecastItem> dataSource){
        this.dataSource.clear();
        this.dataSource.addAll(dataSource);
        notifyDataSetChanged();
    }
}
