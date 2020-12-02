package ru.softmine.weatherapp.openweathermodel;

import java.util.Date;

/**
 * Класс описывает прогноз погоды на опредененный день.
 */
public class Daily {

    private int dt;
    private Temperature temp;
    private Temperature feels_like;
    private int humidity;
    private float wind_speed;
    private int wind_deg;
    private Weather[] weather;

    public Date getDate() {
        return new java.util.Date(dt * 1000L);
    }

    public float getTempMin() {
        return temp.getMin();
    }

    public float getTempMax() {
        return temp.getMax();
    }

    public float getWind_speed() {
        return wind_speed;
    }

    public Weather getWeather() {
        return weather[0];
    }
}
