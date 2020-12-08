package ru.softmine.weatherapp.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.softmine.weatherapp.R;

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = HistoryAdapter.class.getName();

    private final List<HistoryItem> historySource;

    public HistoryAdapter(List<HistoryItem> historySource) {
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
        HistoryItem item = HistoryDataSource.historySource.get(position);

        ((HistoryViewHolder)holder).setData(item);
    }

    @Override
    public int getItemCount() {
        return HistoryDataSource.historySource.size();
    }

    private class HistoryViewHolder extends RecyclerView.ViewHolder {

        private final TextView cityNameTextView;
        private final TextView temperatureTextView;
        private final TextView conditionsTextView;
        private final TextView windTextView;


        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            cityNameTextView = itemView.findViewById(R.id.cityNameTextView);
            temperatureTextView = itemView.findViewById(R.id.temperatureTextView);
            conditionsTextView = itemView.findViewById(R.id.conditionsTextView);
            windTextView = itemView.findViewById(R.id.windTextView);
        }

        public void setData(HistoryItem item) {
            cityNameTextView.setText(item.getCityName());
            temperatureTextView.setText(item.getTemperature());
            conditionsTextView.setText(item.getConditions());
            windTextView.setText(item.getWind());
        }
    }
}
