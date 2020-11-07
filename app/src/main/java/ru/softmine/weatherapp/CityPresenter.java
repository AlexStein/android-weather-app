package ru.softmine.weatherapp;

/**
 * Синглтон для хранения текущего города
 */
public class CityPresenter {

    private static CityPresenter instance;
    private static final Object obj = new Object();
    private String cityName;

    private CityPresenter() {
        this.cityName = "";
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public static CityPresenter getInstance() {
        synchronized (obj) {
            if (instance == null) {
                instance = new CityPresenter();
            }
            return instance;
        }
    }
}
