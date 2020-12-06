package ru.softmine.weatherapp.history;

public class HistoryItem {
    private final String cityName;
    private String temperature;
    private String conditions;
    private String wind;

    public HistoryItem(String cityName, String temperature, String conditions, String wind) {
        this.cityName = cityName;
        this.temperature = temperature;
        this.conditions = conditions;
        this.wind = wind;
    }

    public String getCityName() {
        return cityName;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getConditions() {
        return conditions;
    }

    public String getWind() {
        return wind;
    }

    public void updateHistoryItem(String temperature, String conditions, String wind) {
        this.temperature = temperature;
        this.conditions = conditions;
        this.wind = wind;
    }
}
