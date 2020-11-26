package ru.softmine.weatherapp;

import android.app.Application;
import android.content.Context;

public class WeatherApp extends Application {
    private static WeatherApp app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static Context getAppContext() {
        return app;
    }
}
