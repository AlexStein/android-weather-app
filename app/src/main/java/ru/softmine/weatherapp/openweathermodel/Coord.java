package ru.softmine.weatherapp.openweathermodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Класс описывает географические координаты, получаемы в запросе погоды
 */
public class Coord {
    @SerializedName("lon")
    @Expose
    private float lon;

    @SerializedName("lat")
    @Expose
    private float lat;

    public Coord() {
    }

    public Coord(float lon, float lat) {
        this.lon = lon;
        this.lat = lat;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }
}
