package ru.softmine.weatherapp.openweathermodel;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import ru.softmine.weatherapp.R;
import ru.softmine.weatherapp.WeatherApp;

public class WeatherParser {

    private String cityName;
    private int cityId;
    private Coord coord;
    private CurrentWeather current;
    private Daily[] daily;

    // Вспомогательные классы для парсинга
    private static class CurrentParser {
        private CurrentWeather current;

        public CurrentWeather getCurrent() {
            return current;
        }
    }

    private static class DailyParser {
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

    public static boolean parseCurrent(WeatherParser weatherParser, String jsonString) throws WeatherRequestException {
        Gson gson = new Gson();
        CurrentParser currentParser;
        try {
            currentParser = gson.fromJson(jsonString, CurrentParser.class);

        } catch (JsonSyntaxException e) {
            throw new WeatherRequestException(e.getMessage());
        }

        if (currentParser != null) {
            weatherParser.current = currentParser.current;
        }

        return (currentParser != null);
    }

    public static boolean parseDaily(WeatherParser weatherParser, String jsonString) throws WeatherRequestException {
        Gson gson = new Gson();
        DailyParser dailyParser;
        try {
            dailyParser = gson.fromJson(jsonString, DailyParser.class);

        } catch (JsonSyntaxException e) {
            throw new WeatherRequestException(e.getMessage());
        }

        if (dailyParser != null) {
            weatherParser.daily = dailyParser.daily;
        }

        return (dailyParser != null);
    }

    public String getCityName() {
        return cityName;
    }

    public int getCityId() {
        return cityId;
    }

    public String getWeatherString() {
        return current.getWeather().getMain();
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
