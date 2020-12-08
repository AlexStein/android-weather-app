package ru.softmine.weatherapp.history;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.softmine.weatherapp.Logger;

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

        if (Logger.DEBUG) {
            Log.d(TAG, "listLen: " + listLen);
            Log.d(TAG, "lastCityName: " + getLastCityName());
            Log.d(TAG, "cityName: " + cityName);
        }
        HistoryItem item;
        if (getLastCityName().equals(cityName)) {
            item = historySource.get(listLen - 1);
            item.updateHistoryItem(temp, condition, wind);
            historySource.set(listLen - 1, item);
            if (Logger.DEBUG) {
                Log.d(TAG, "History item Updated");
            }
        } else {
            item = new HistoryItem(cityName, temp, condition, wind);
            historySource.add(item);
            if (Logger.DEBUG) {
                Log.d(TAG, "History item Added");
            }
        }
    }
}
