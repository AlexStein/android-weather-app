package ru.softmine.weatherapp.forecast;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.softmine.weatherapp.WeatherDataSource;
import ru.softmine.weatherapp.openweathermodel.Daily;

public class ForecastSource implements WeatherDataSource {
    private final List<ForecastItem> dataSource;
    private final Resources resources;

    private final Random rand = new Random();

    public ForecastSource(Resources resources) {
        this.dataSource = new ArrayList<>(7);
        this.resources = resources;
    }

    public ForecastSource init(Daily[] daily) {
        for (Daily d : daily) {
            dataSource.add(new ForecastItem(d.getDate(),
                    Math.round(d.getTempMin()), Math.round(d.getTempMax()),
                    d.getWeather().getMain()));
        }
        return this;
    }

    public List<ForecastItem> getDataSource() {
        return dataSource;
    }

    public ForecastItem getForecastItem(int position) {
        return dataSource.get(position);
    }

    public int size() {
        return dataSource.size();
    }
}
