package ru.softmine.weatherapp;

import java.util.List;

public interface WeatherDataSource {
    List<ForecastItem> getDataSource();
    ForecastItem getForecastItem(int position);
    int size();
}
