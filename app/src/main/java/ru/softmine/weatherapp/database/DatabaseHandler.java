package ru.softmine.weatherapp.database;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import ru.softmine.weatherapp.WeatherApp;
import ru.softmine.weatherapp.constants.Logger;
import ru.softmine.weatherapp.history.HistoryDataSource;
import ru.softmine.weatherapp.history.HistoryItem;

public class DatabaseHandler {

    private final static String TAG = DatabaseHandler.class.getName();

    private final HandlerThread handlerThread;
    private final Handler handler;
    private final WeatherDao weatherDao;

    public DatabaseHandler() {
        handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        weatherDao = WeatherApp.getWeatherDao();
    }

    public void updateHistory() {
        final HistoryItem historyItem = new HistoryItem(
                WeatherApp.getWeatherParser());
        handler.post(new Runnable() {
            @Override
            public void run() {
                HistoryItem lastHistoryItem = weatherDao.lastHistoryItem();

                if (lastHistoryItem == null ||
                        !lastHistoryItem.getCityName().equals(historyItem.getCityName())) {
                    // Вставляем
                    weatherDao.insertHistoryItem(historyItem);
                } else {
                    // Обновляем
                    lastHistoryItem.setConditions(historyItem.getConditions());
                    lastHistoryItem.setTemperature(historyItem.getTemperature());
                    lastHistoryItem.setWind(historyItem.getWind());

                    weatherDao.updateHistoryItem(lastHistoryItem);
                }
            }
        });
    }

    public void searchHistory(HistoryDataSource source, String query, Handler.Callback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                source.setHistoryItems(weatherDao.searchHistoryItems("%" + query + "%"));
                if (Logger.DEBUG) {
                    Log.d(TAG, "searchHistory");
                }
                new Handler(callback).sendMessage(new Message());
            }
        });
    }

    public void loadHistory(HistoryDataSource source, Handler.Callback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                source.setHistoryItems(weatherDao.getHistoryItems());
                if (Logger.DEBUG) {
                    Log.d(TAG, "loadHistory");
                }
                new Handler(callback).sendMessage(new Message());
            }
        });
    }
}