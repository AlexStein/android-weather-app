package ru.softmine.weatherapp.openweathermodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CityParser {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("coord")
    @Expose
    private Coord coord;

    @SerializedName("sys")
    @Expose
    private Sys sys;

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public float getLat() {
        return coord.getLat();
    }

    public float getLon() {
        return coord.getLon();
    }

    public String getCountry() {
        return sys.getCountry();
    }
}
