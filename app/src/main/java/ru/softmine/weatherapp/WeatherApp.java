package ru.softmine.weatherapp;

import android.app.Application;
import android.content.Context;

import ru.softmine.weatherapp.openweathermodel.WeatherApiHolder;
import ru.softmine.weatherapp.openweathermodel.WeatherParser;

public class WeatherApp extends Application {

    private static WeatherApp app;
    private static WeatherParser weatherParser;
    private static WeatherApiHolder weatherApiHolder;

    @Override
    public void onCreate() {
        super.onCreate();

        app = this;
        weatherParser = new WeatherParser();
        weatherApiHolder = new WeatherApiHolder();
    }

    public static Context getAppContext() {
        return app;
    }

    public static WeatherParser getWeatherParser() {
        return weatherParser;
    }

    public static WeatherApiHolder getWeatherApiHolder() {
        return weatherApiHolder;
    }
}
