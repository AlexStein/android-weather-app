package ru.softmine.weatherapp.openweathermodel;

/**
 * Класс лписывает температурные данные из прогноза по дням
 */
public class Temperature {

    private float day;
    private float min;
    private float max;
    private float night;
    private float eve;
    private float morn;

    public float getMin() {
        return Math.round(min - 273.15f);
    }

    public float getMax() {
        return Math.round(max - 273.15f);
    }
}
