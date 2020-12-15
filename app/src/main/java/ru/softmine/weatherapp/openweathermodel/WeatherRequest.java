package ru.softmine.weatherapp.openweathermodel;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import ru.softmine.weatherapp.BuildConfig;
import ru.softmine.weatherapp.WeatherApp;
import ru.softmine.weatherapp.constants.Logger;

public class WeatherRequest {

    private static final String TAG = WeatherRequest.class.getName();

    private static final String WEATHER_API_KEY = BuildConfig.WEATHER_API_KEY;
    private static final String WEB_API_URL = "https://api.openweathermap.org/data/2.5";
    private static final String CITY_URL = "%s/weather?q=%s&appid=%s";
    private static final String CURRENT_URL = "%s/onecall?lat=%f&lon=%f&exclude=minutely,hourly,daily,alerts&appid=%s";
    private static final String DAILY_URL = "%s/onecall?lat=%f&lon=%f&exclude=current,minutely,hourly,alerts&appid=%s";

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

    /**
     * Получение сводки для города в одном методе
     *
     * @param cityName Наименование города
     * @return Истина, если обновление полностью успешно
     */
    public static boolean getWeatherParser(String cityName) {
        WeatherParser weatherParser = WeatherApp.getWeatherParser();
        String urlString = String.format(CITY_URL, WEB_API_URL, cityName, WEATHER_API_KEY);
        boolean success = false;

        try {
            success = WeatherParser.parseCity(weatherParser, getResultForUri(new URL(urlString)));
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + urlString);
            return false;
        } catch (WeatherRequestException e) {
            if (e.getMessage() != null) {
                Log.e(TAG, e.getMessage());
            }
            return false;
        }

        if(!success) {
            if (Logger.DEBUG) {
                Log.d(TAG, "Data process failed");
            }
            return false;
        }

        urlString = String.format(Locale.getDefault(), CURRENT_URL,
                WEB_API_URL, weatherParser.getLat(), weatherParser.getLon(), WEATHER_API_KEY);
        try {
            success = WeatherParser.parseCurrent(weatherParser, getResultForUri(new URL(urlString)));
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + urlString);
            return false;
        } catch (WeatherRequestException e) {
            if (e.getMessage() != null) {
                Log.e(TAG, e.getMessage());
            }
            return false;
        }

        if(!success) {
            if (Logger.DEBUG) {
                Log.d(TAG, "Data process failed");
            }
            return false;
        }

        urlString = String.format(Locale.getDefault(), DAILY_URL,
                WEB_API_URL, weatherParser.getLat(), weatherParser.getLon(), WEATHER_API_KEY);
        try {
            success = WeatherParser.parseDaily(weatherParser, getResultForUri(new URL(urlString)));
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + urlString);
            return false;
        } catch (WeatherRequestException e) {
            if (e.getMessage() != null) {
                Log.e(TAG, e.getMessage());
            }
            return false;
        }

        return success;
    }
}
