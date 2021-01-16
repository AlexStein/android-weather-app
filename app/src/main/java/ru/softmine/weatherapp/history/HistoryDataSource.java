package ru.softmine.weatherapp.history;

import java.util.ArrayList;
import java.util.List;

/**
 * Хранилице истории погоды
 */
public class HistoryDataSource {
    public List<HistoryItem> historyItems;

    public HistoryDataSource() {
        historyItems = new ArrayList<>();
    }

    public List<HistoryItem> getHistoryItems(){
        return historyItems;
    }

    public void setHistoryItems(List<HistoryItem> historyItems) {
        this.historyItems = historyItems;
    }

    public int getHistoryItemsCount(){
        return historyItems.size();
    }
}
