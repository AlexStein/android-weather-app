package ru.softmine.weatherapp.openweathermodel;

import android.content.res.Resources;

import java.util.Locale;

import ru.softmine.weatherapp.R;
import ru.softmine.weatherapp.WeatherApp;

public class CurrentWeather {

    private final Resources res;

    private float temp;
    private float feels_like;
    private int humidity;
    private float  wind_speed;
    private int  wind_deg;
    private Weather[] weather;

    public CurrentWeather() {
        res = WeatherApp.getAppContext().getResources();
    }

    public int getTemp() {
        return Math.round(temp - 273.15f);
    }

    public int getFeelsLike() {
        return Math.round(feels_like - 273.15f);
    }

    public int getHumidity() {
        return humidity;
    }

    /**
     * Получить скорость ветра в единицах измерения
     * @return Скорость
     */
    public float getWindSpeedUnits() {
        return wind_speed;
    }

    public String getWindDirectionName() {
        return res.getStringArray(R.array.directions)[wind_deg % 360 / 45];
    }

    public Weather getWeather() {
        return weather[0];
    }

    public String getTempString() {
        return String.format(Locale.getDefault(), res.getString(R.string.current_temp_format),
                getTemp(), getFeelsLike(), "C");
    }

    public String getWindString() {
        return String.format(Locale.getDefault(), res.getString(R.string.current_wind_format),
                getWindSpeedUnits(), "m/s", getWindDirectionName());
    }
}
