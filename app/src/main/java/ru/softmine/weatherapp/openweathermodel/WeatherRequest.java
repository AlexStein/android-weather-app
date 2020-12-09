package ru.softmine.weatherapp.openweathermodel;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import ru.softmine.weatherapp.BuildConfig;
import ru.softmine.weatherapp.constants.Logger;

public class WeatherRequest {

    private static final String TAG = WeatherRequest.class.getName();

    private static final String WEATHER_API_KEY = BuildConfig.WEATHER_API_KEY;
    private static final String WEB_CURRENT_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s";
    private static final String WEB_ONECALL_URL = "https://api.openweathermap.org/data/2.5/onecall?lat=%f&lon=%f&exclude=minutely,hourly,daily,alerts&appid=%s";
    private static final String WEB_ONECALL_DAILY_URL = "https://api.openweathermap.org/data/2.5/onecall?lat=%f&lon=%f&exclude=current,minutely,hourly,alerts&appid=%s";

    private String name;
    private int dt;
    private Coord coord;
    private CurrentWeather current;
    private Daily[] daily;


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

    /**
     * Получить объект с текущей погодой для указанного города
     *
     * @param cityName город
     * @return Объект погоды
     * @throws WeatherRequestException В случае неожиданых проблем
     */
    public static WeatherRequest getCurrentWeather(String cityName) throws WeatherRequestException {
        WeatherRequest city = getCity(cityName);
        WeatherRequest weatherRequest = null;

        final URL uri;
        try {
            uri = new URL(String.format(Locale.getDefault(), WEB_ONECALL_URL,
                    city.getLat(), city.getLon(), WEATHER_API_KEY));
        } catch (MalformedURLException e) {
            throw new WeatherRequestException("MalformedURLException: " + String.format(Locale.getDefault(), WEB_ONECALL_URL,
                    city.getLat(), city.getLon(), WEATHER_API_KEY));
        }

        if (Logger.VERBOSE) {
            Log.v(TAG, String.format("getCurrentWeather URI: %s", uri));
        }
        String result = getResultForUri(uri);

        Gson gson = new Gson();
        try {
            weatherRequest = gson.fromJson(result, WeatherRequest.class);
        } catch (JsonSyntaxException e) {
            throw new WeatherRequestException(e.getMessage());
        }

        return weatherRequest;
    }

    /**
     * Получить объект с текущей погодой для указанного города
     *
     * @param cityName город
     * @return Объект погоды
     * @throws WeatherRequestException В случае неожиданых проблем
     */
    public static WeatherRequest getDailyWeather(String cityName) throws WeatherRequestException {
        WeatherRequest city = getCity(cityName);

        final URL uri;
        try {
            uri = new URL(String.format(Locale.getDefault(), WEB_ONECALL_DAILY_URL,
                    city.getLat(), city.getLon(), WEATHER_API_KEY));
        } catch (MalformedURLException e) {
            throw new WeatherRequestException("MalformedURLException: " + String.format(Locale.getDefault(), WEB_ONECALL_URL,
                    city.getLat(), city.getLon(), WEATHER_API_KEY));
        }

        if (Logger.VERBOSE) {
            Log.v(TAG, String.format("getDailyWeather URI: %s", uri));
        }
        String result = getResultForUri(uri);

        Gson gson = new Gson();
        WeatherRequest weatherRequest = null;
        try {
            weatherRequest = gson.fromJson(result, WeatherRequest.class);
        } catch (JsonSyntaxException e) {
            throw new WeatherRequestException(e.getMessage());
        }

        if (weatherRequest == null) {
            throw new WeatherRequestException("No data");
        }

        return weatherRequest;
    }

    private static WeatherRequest getCity(String cityName) throws WeatherRequestException {
        final URL uri;
        try {
            uri = new URL(String.format(WEB_CURRENT_URL, cityName, WEATHER_API_KEY));
        } catch (MalformedURLException e) {
            throw new WeatherRequestException("MalformedURLException: " + String.format(WEB_CURRENT_URL, cityName, WEATHER_API_KEY));
        }

        if (Logger.VERBOSE) {
            Log.v(TAG, String.format("getCity URI: %s", uri));
        }
        String result = getResultForUri(uri);

        WeatherRequest weatherRequest = null;
        Gson gson = new Gson();
        try {
            weatherRequest = gson.fromJson(result, WeatherRequest.class);
        } catch (JsonSyntaxException e) {
            if (Logger.DEBUG && e.getMessage() != null) {
                Log.d(TAG, e.getMessage());
            }
            throw new WeatherRequestException(e.getMessage());
        }

        if (weatherRequest == null) {
            throw new WeatherRequestException("No city data");
        }

        return weatherRequest;
    }

    private static String getResultForUri(URL uri) throws WeatherRequestException {
        String result = "";
        HttpsURLConnection urlConnection = null;
        try {
            urlConnection = (HttpsURLConnection) uri.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(5000);

            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            result = in.lines().collect(Collectors.joining("\n"));

        } catch (Exception e) {
            if (Logger.DEBUG && e.getMessage() != null) {
                Log.d(TAG, e.getMessage());
            }
            throw new WeatherRequestException(e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }
}
