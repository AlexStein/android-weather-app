package ru.softmine.weatherapp.cities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import ru.softmine.weatherapp.constants.Database;

@Entity(tableName = Database.CITIES_TABLE)
public class City {

    public final static String ID = "id";
    public final static String CITY_NAME = "name";
    public final static String LATITUDE = "lat";
    public final static String LONGITUDE = "lon";
    public final static String COUNTRY = "country";

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = ID)
    private int id;

    @ColumnInfo(name = CITY_NAME)
    private String name;

    @ColumnInfo(name = LATITUDE)
    private float lat;

    @ColumnInfo(name = LONGITUDE)
    private float lon;

    @ColumnInfo(name = COUNTRY)
    private String country;

    public City() {
    }

    @Ignore
    public City(int id, String cityName, float lat, float lon, String country) {
        this.id = id;
        this.name = cityName;
        this.lat = lat;
        this.lon = lon;
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    public String getCountry() {
        return country;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
