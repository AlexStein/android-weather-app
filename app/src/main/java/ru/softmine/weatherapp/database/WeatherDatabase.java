package ru.softmine.weatherapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import ru.softmine.weatherapp.cities.City;
import ru.softmine.weatherapp.history.HistoryItem;

@Database(entities = {HistoryItem.class, City.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class WeatherDatabase extends RoomDatabase {
    public abstract WeatherDao getWeatherDao();
}