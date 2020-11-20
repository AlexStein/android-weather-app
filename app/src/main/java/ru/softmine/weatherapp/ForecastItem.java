package ru.softmine.weatherapp;

import java.util.Date;

public class ForecastItem {

    /* Сокращенный набор полей для прогноза на основе API Яндекс
    date - Дата
    part_name - Часть дня: утро, день, вечер, ночь
    temp_min - Минимальная темп.
    temp_max - Максимаьная
    temp_avg - средняя
    condition - Описание погоды
    wind_speed - Скорость ветра
    wind_dir - Направление
    pressure_mm - Давление в мм
    humidity - влажность %
    prec_mm - Количество осадков в мм
    */
    private Date date;
    private String part_name;
    private int temp_min;
    private int temp_max;
    private int temp_avg;
    private String condition;
    private int wind_speed;
    private String wind_dir;
    private int pressure_mm;
    private int humidity;
    private int prec_mm;

    public ForecastItem(Date date, int temp_min, int temp_max, String condition) {
        this.date = date;
        this.temp_min = temp_min;
        this.temp_max = temp_max;
        this.condition = condition;
    }

    public Date getDate() {
        return date;
    }

    public String getPartOfDay() {
        return part_name;
    }

    public int getTempMin() {
        return temp_min;
    }

    public int getTempMax() {
        return temp_max;
    }

    public int getTempAvg() {
        return temp_avg;
    }

    public String getCondition() {
        return condition;
    }

    public int getWindSpeed() {
        return wind_speed;
    }

    public String getWindDirection() {
        return wind_dir;
    }

    public int getPressure() {
        return pressure_mm;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getPrecipitation() {
        return prec_mm;
    }
}
