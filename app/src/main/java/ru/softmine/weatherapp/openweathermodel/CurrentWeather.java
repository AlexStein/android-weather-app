package ru.softmine.weatherapp.openweathermodel;

import android.content.res.Resources;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Locale;

import ru.softmine.weatherapp.R;
import ru.softmine.weatherapp.WeatherApp;

public class CurrentWeather {

    private final static float ZERO = -273.15f;
    private final Resources res;

    @SerializedName("temp")
    @Expose
    private float temp;

    @SerializedName("feels_like")
    @Expose
    private float feels_like;

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

    public CurrentWeather() {
        res = WeatherApp.getAppContext().getResources();
    }

    public int getTemp() {
        return Math.round(temp + ZERO);
    }

    public int getFeelsLike() {
        return Math.round(feels_like + ZERO);
    }

    public int getHumidity() {
        return humidity;
    }

    /**
     * Получить скорость ветра в единицах измерения
     *
     * @return Скорость
     */
    public float getWindSpeedUnits() {
        return wind_speed;
    }

    public String getWindDirectionName() {
        return res.getStringArray(R.array.directions)[wind_deg % 360 / 45];
    }

    public Weather getWeather() {
        if (weather == null || weather.length == 0) {
            return null;
        }
        return weather[0];
    }

    public String getTempString() {
        return String.format(Locale.getDefault(), res.getString(R.string.current_temp_format),
                getTemp(), getFeelsLike(), res.getString(R.string.celsius));
    }

    public String getWindString() {
        return String.format(Locale.getDefault(), res.getString(R.string.current_wind_format),
                getWindSpeedUnits(), res.getString(R.string.m_s), getWindDirectionName());
    }
}
