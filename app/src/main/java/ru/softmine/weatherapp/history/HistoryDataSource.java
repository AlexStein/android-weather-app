package ru.softmine.weatherapp.history;

import java.util.ArrayList;
import java.util.List;

import ru.softmine.weatherapp.database.WeatherDao;

/**
 * Хранилице истории погоды
 */
public class HistoryDataSource {
    private static final String TAG = HistoryDataSource.class.getName();

    private final WeatherDao weatherDao;

    public static List<HistoryItem> historyItems = new ArrayList<>();

    public HistoryDataSource(WeatherDao weatherDao) {
        this.weatherDao = weatherDao;
    }

    public void loadAllHistoryItems(){
        historyItems = weatherDao.getHistoryItems();
    }

    public List<HistoryItem> getHistoryItems(){
        if (historyItems == null){
            loadAllHistoryItems();
        }
        return historyItems;
    }

    public void getHistoryItemsByCityName(String cityName){
        historyItems = weatherDao.searchHistoryItems(cityName);
    }

    public int getHistoryItemsCount(){
        return historyItems.size();
    }

    public void addHistoryItem(HistoryItem historyItem){
        weatherDao.insertHistoryItem(historyItem);
        loadAllHistoryItems();
    }

    public void updateHistoryItem(HistoryItem historyItem){
        HistoryItem lastHistoryItem = getLastHistoryItem();

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

        loadAllHistoryItems();
    }

    private HistoryItem getLastHistoryItem() {
        return weatherDao.lastHistoryItem();
    }
}
