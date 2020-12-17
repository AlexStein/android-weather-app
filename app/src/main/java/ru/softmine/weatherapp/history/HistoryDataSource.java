package ru.softmine.weatherapp.history;

import java.util.ArrayList;
import java.util.List;

/**
 * Хранилице истории погоды
 */
public class HistoryDataSource {
    private static final String TAG = HistoryDataSource.class.getName();

    public static List<HistoryItem> historySource = new ArrayList<>();

    private static String getLastCityName() {
        int listLen = historySource.size();

        if (listLen == 0) {
            return "";
        }

        return historySource.get(listLen - 1).getCityName();
    }

    public static void updateHistoryItem(String cityName, String temp, String condition, String wind) {
        int listLen = historySource.size();

        HistoryItem item;
        if (getLastCityName().equals(cityName)) {
            item = historySource.get(listLen - 1);
            item.updateHistoryItem(temp, condition, wind);
            historySource.set(listLen - 1, item);

        } else {
            item = new HistoryItem(cityName, temp, condition, wind);
            historySource.add(item);
        }
    }
}
