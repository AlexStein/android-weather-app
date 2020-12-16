package ru.softmine.weatherapp.openweathermodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Класс описывает прогноз погоды на опредененный день.
 */
public class Daily {
    @SerializedName("dt")
    @Expose
    private int dt;

    @SerializedName("temp")
    @Expose
    private Temperature temp;

    @SerializedName("feels_like")
    @Expose
    private Temperature feels_like;

    @SerializedName("humidity")
    @Expose
    private int humidity;

    @SerializedName("wind_speed")
    @Expose
    private float wind_speed;

    @SerializedName("wind_deg")
    @Expose
    private int wind_deg;

    @SerializedName("weather")
    @Expose
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

    public String getIcon() {
        return WeatherParser.getWeatherIconUri(getWeather().getIcon());
    }
}
