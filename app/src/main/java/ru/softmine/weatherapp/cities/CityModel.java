package ru.softmine.weatherapp.cities;

import java.util.regex.Pattern;

/**
 * Синглтон для хранения текущего города
 */
public class CityModel {

    private static final String cityNameRegex = "^([a-zA-Z\\\\u0080-\\\\u024F]+(?:. |-| |\\'))*[a-zA-Z\\\\u0080-\\\\u024F]*$";

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

    public static boolean isValidName(String cityName) {
        Pattern cityNamePattern = Pattern.compile(cityNameRegex);

        return cityNamePattern.matcher(cityName).matches();
    }
}
