package ru.softmine.weatherapp.openweathermodel;

/**
 * Класс лписывает температурные данные из прогноза по дням
 */
public class Temperature {

    private final float K = 273.15f;

    private float day;
    private float min;
    private float max;
    private float night;
    private float eve;
    private float morn;

    // At the moment we took Celsius as default
    public float getMin() {
        return getMinCelsius();
    }

    // At the moment we took Celsius as default
    public float getMax() {
        return getMaxCelsius();
    }

    public float getMinCelsius() {
        return Math.round(min - K);
    }

    public float getMaxCelsius() {
        return Math.round(max - K);
    }

    public float getMinFahrenheit() {
        return Math.round((min - K) * 1.8 + 32);
    }

    public float getMaxFahrenheit() {
        return Math.round((min - K) * 1.8 + 32);
    }
}
