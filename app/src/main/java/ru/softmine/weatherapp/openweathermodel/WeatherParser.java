package ru.softmine.weatherapp.openweathermodel;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ru.softmine.weatherapp.R;
import ru.softmine.weatherapp.WeatherApp;

public class WeatherParser {

    private static final String WEB_IMG_URL = "https://openweathermap.org/img/wn/%s@2x.png";

    @SerializedName("name")
    @Expose
    private String cityName;

    @SerializedName("id")
    @Expose
    private int cityId;

    @SerializedName("coord")
    @Expose
    private Coord coord;

    @SerializedName("current")
    @Expose
    private CurrentWeather current;

    @SerializedName("daily")
    @Expose
    private Daily[] daily;

    // Вспомогательные классы для парсинга
    private static class WeatherDataParser {
        private CurrentWeather current;
        private Daily[] daily;
    }

    private static class CityParser {
        private String name;
        private int id;
        private Coord coord;
    }

    /**
     * В конструкторе задается начальное состояни сводки погоды.
     */
    public WeatherParser() {
        cityName = WeatherApp.getAppContext().getString(R.string.moscow_city);
        current = new CurrentWeather();
        daily = new Daily[] {};
    }

    public static boolean parseCity(WeatherParser weatherParser, String jsonString) throws WeatherRequestException {
        Gson gson = new Gson();
        CityParser city;
        try {
            city = gson.fromJson(jsonString, CityParser.class);

        } catch (JsonSyntaxException e) {
            throw new WeatherRequestException(e.getMessage());
        }

        if (city != null) {
            weatherParser.cityName = city.name;
            weatherParser.cityId = city.id;
            weatherParser.coord = city.coord;
        }

        return (city != null);
    }

    public static boolean parseWeather(WeatherParser weatherParser, String jsonString) throws WeatherRequestException {
        Gson gson = new Gson();
        WeatherDataParser parser;
        try {
            parser = gson.fromJson(jsonString, WeatherDataParser.class);

        } catch (JsonSyntaxException e) {
            throw new WeatherRequestException(e.getMessage());
        }

        if (parser != null) {
            weatherParser.daily = parser.daily;
            weatherParser.current = parser.current;
        }

        return (parser != null);
    }

    public String getCityName() {
        return cityName;
    }

    public String getWeatherString() {
        return current.getWeather().getMain();
    }

    public static String getWeatherIconUri(String iconName) {
        return String.format(WEB_IMG_URL, iconName);
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

    public float getLat() {
        return coord.getLat();
    }

    public float getLon() {
        return coord.getLon();
    }

    public Daily[] getDaily() {
        return daily;
    }
}
