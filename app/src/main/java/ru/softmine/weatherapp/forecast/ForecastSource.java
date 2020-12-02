package ru.softmine.weatherapp.forecast;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import ru.softmine.weatherapp.R;
import ru.softmine.weatherapp.WeatherDataSource;

public class ForecastSource implements WeatherDataSource {
    private final List<ForecastItem> dataSource;
    private final Resources resources;

    private final Random rand = new Random();

    public ForecastSource(Resources resources) {
        this.dataSource = new ArrayList<>(7);
        this.resources = resources;
    }

    public ForecastSource init() {
        // строки прогнозов
        String[] conditions = resources.getStringArray(R.array.forecasts);

        Calendar calendar = Calendar.getInstance();
        Date dt;
        int tMin;
        int tMax;

        // заполнение источника данных
        for (int i = 0; i < conditions.length; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            dt = calendar.getTime();
            tMin = rand.nextInt(10);
            tMax = rand.nextInt(5) + tMin;

            dataSource.add(new ForecastItem(dt, tMin, tMax, conditions[i]));
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
