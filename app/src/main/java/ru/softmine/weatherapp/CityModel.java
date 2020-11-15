package ru.softmine.weatherapp;

/**
 * Синглтон для хранения текущего города
 */
public class CityModel {

    private static CityModel instance;
    private static final Object obj = new Object();
    private String cityName;

    private CityModel() {
        this.cityName = "";
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public static CityModel getInstance() {
        synchronized (obj) {
            if (instance == null) {
                instance = new CityModel();
            }
            return instance;
        }
    }
}
