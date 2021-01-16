package ru.softmine.weatherapp.openweathermodel;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import ru.softmine.weatherapp.constants.BaseUrl;
import ru.softmine.weatherapp.interfaces.WeatherObserver;

public class WeatherParser {

    /* Наблюдатели за погодой, фрагменты, View */
    private final List<WeatherObserver> observers;

    private final static Gson gson = new Gson();

    private String cityName;

    @SerializedName("coord")
    @Expose
    private Coord coord;

    @SerializedName("current")
    @Expose
    private CurrentWeather current;

    @SerializedName("daily")
    @Expose
    private Daily[] daily;

    /**
     * В конструкторе задается начальное состояни сводки погоды.
     */
    public WeatherParser() {
        observers = new ArrayList<WeatherObserver>() {};
    }

    public void setCity(String cityName, float lat, float lon) {
        this.cityName = cityName;
        this.coord = new Coord(lon, lat);
        this.current = new CurrentWeather();
        this.daily = new Daily[]{};
    }

    public void updateWeather(CurrentWeather currentWeather, Daily[] daily) {
        this.current = currentWeather;
        this.daily = daily;
    }

    public void addObserver(WeatherObserver observer) {
        observers.add(observer);
    }

    public static CityParser parseCity(String jsonString) throws WeatherRequestException {
        CityParser city;
        try {
            city = gson.fromJson(jsonString, CityParser.class);

        } catch (JsonSyntaxException e) {
            throw new WeatherRequestException(e.getMessage());
        }

        return city;
    }

    public static WeatherParser parseWeather(String jsonString) throws WeatherRequestException {
        WeatherParser parser;
        try {
            parser = gson.fromJson(jsonString, WeatherParser.class);

        } catch (JsonSyntaxException e) {
            throw new WeatherRequestException(e.getMessage());
        }
        return parser;
    }

    public void notifyObservers() {
        for (WeatherObserver observer : observers) {
            observer.onWeatherDataChanged();
        }
    }

    public String getCityName() {
        return cityName;
    }

    public String getWeatherString() {
        return current.getWeather().getDescription();
    }

    public static String getWeatherIconUri(String iconName) {
        return String.format(BaseUrl.WEB_IMG_URL, iconName);
    }

    public String getIcon() {
        return getWeatherIconUri(current.getWeather().getIcon());
    }

    public String getTemperatureString() {
        return current.getTempString();
    }

    public int getTemperature() {
        return current.getTemp();
    }

    public String getWindString() {
        return current.getWindString();
    }

    public CurrentWeather getCurrent() {
        return current;
    }

    public Daily[] getDaily() {
        return daily;
    }

    public float getLat() {
        return coord.getLat();
    }

    public float getLon() {
        return coord.getLon();
    }
}
