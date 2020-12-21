package ru.softmine.weatherapp.history;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ru.softmine.weatherapp.R;
import ru.softmine.weatherapp.WeatherApp;
import ru.softmine.weatherapp.constants.Logger;

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = HistoryAdapter.class.getName();

    private final HistoryDataSource historySource;
    private final SimpleDateFormat sdf = new SimpleDateFormat(
            WeatherApp.getAppContext().getString(R.string.day_fmt), Locale.getDefault());


    public HistoryAdapter(HistoryDataSource historySource) {
        this.historySource = historySource;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list_item, parent, false);

        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        List<HistoryItem> items = historySource.getHistoryItems();
        HistoryItem item = items.get(position);

        ((HistoryViewHolder)holder).setData(item);
    }

    @Override
    public int getItemCount() {
        return historySource.getHistoryItemsCount();
    }

    public void getItemsByCityName(String cityName) {
        if (Logger.DEBUG) {
            Log.d(TAG, cityName);
        }

        historySource.getHistoryItemsByCityName(cityName);
        //notifyDataSetChanged();
    }

    private class HistoryViewHolder extends RecyclerView.ViewHolder {

        private final TextView cityNameTextView;
        private final TextView dateTextView;
        private final TextView temperatureTextView;
        private final TextView conditionsTextView;
        private final TextView windTextView;


        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            cityNameTextView = itemView.findViewById(R.id.cityNameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            temperatureTextView = itemView.findViewById(R.id.temperatureTextView);
            conditionsTextView = itemView.findViewById(R.id.conditionsTextView);
            windTextView = itemView.findViewById(R.id.windTextView);
        }

        public void setData(HistoryItem item) {
            cityNameTextView.setText(item.getCityName());
            dateTextView.setText(item.getDateString(sdf));
            temperatureTextView.setText(item.getTemperature());
            conditionsTextView.setText(item.getConditions());
            windTextView.setText(item.getWind());
        }
    }
}
