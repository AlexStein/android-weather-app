package ru.softmine.weatherapp;

import java.util.List;

import ru.softmine.weatherapp.forecast.ForecastItem;

public interface WeatherDataSource {
    List<ForecastItem> getDataSource();
    ForecastItem getForecastItem(int position);
    int size();
}
