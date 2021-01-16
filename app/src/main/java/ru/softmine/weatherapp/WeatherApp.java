package ru.softmine.weatherapp;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import ru.softmine.weatherapp.constants.Database;
import ru.softmine.weatherapp.database.DatabaseHandler;
import ru.softmine.weatherapp.database.WeatherDao;
import ru.softmine.weatherapp.database.WeatherDatabase;
import ru.softmine.weatherapp.openweathermodel.WeatherApiHolder;
import ru.softmine.weatherapp.openweathermodel.WeatherParser;

public class WeatherApp extends Application {

    private static WeatherApp app;
    private static WeatherParser weatherParser;
    private static WeatherApiHolder weatherApiHolder;
    private static WeatherDatabase db;
    private static DatabaseHandler databaseHandler;
    private static String lang;

    @Override
    public void onCreate() {
        super.onCreate();

        app = this;
        db = Room.databaseBuilder(app, WeatherDatabase.class, Database.DATABASE_NAME).build();

        weatherParser = new WeatherParser();
        weatherApiHolder = new WeatherApiHolder();
        databaseHandler = new DatabaseHandler();
        lang = getResources().getString(R.string.locale);
    }

    public static WeatherDao getWeatherDao() {
        return db.getWeatherDao();
    }

    public static DatabaseHandler getDatabaseHandler() {
        return databaseHandler;
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

    public static String getLang() {
        return lang;
    }
}
