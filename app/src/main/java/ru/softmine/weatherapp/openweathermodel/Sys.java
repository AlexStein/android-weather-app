package ru.softmine.weatherapp.openweathermodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sys {
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("country")
    @Expose
    private String country;

    public int getId() {
        return id;
    }

    public String getCountry() {
        return country;
    }
}
