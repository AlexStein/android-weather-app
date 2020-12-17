package ru.softmine.weatherapp.openweathermodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Класс лписывает температурные данные из прогноза по дням
 */
public class Temperature {

    private final float K = 273.15f;
    @SerializedName("day")
    @Expose
    private float day;

    @SerializedName("min")
    @Expose
    private float min;

    @SerializedName("max")
    @Expose
    private float max;

    @SerializedName("night")
    @Expose
    private float night;

    @SerializedName("eve")
    @Expose
    private float eve;

    @SerializedName("morn")
    @Expose
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
