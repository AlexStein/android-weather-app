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
import ru.softmine.weatherapp.constants.BaseUrl;
import ru.softmine.weatherapp.constants.Logger;

public class WeatherRequest {

    private static final String TAG = WeatherRequest.class.getName();

    private static final String WEATHER_API_KEY = BuildConfig.WEATHER_API_KEY;

    private static final String CITY_URL = "%s/weather?q=%s&appid=%s&lang=%s";
    private static final String CURRENT_URL = "%s/onecall?lat=%f&lon=%f&exclude=minutely,hourly,alerts&appid=%s&lang=%s";

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
    public static WeatherParser getWeatherParser(String cityName) {
        String urlString = String.format(CITY_URL, BaseUrl.WEB_API_URL, cityName, WEATHER_API_KEY,
                WeatherApp.getLang());

        CityParser city;
        try {
            city = WeatherParser.parseCity(getResultForUri(new URL(urlString)));
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + urlString);
            return null;
        } catch (WeatherRequestException e) {
            if (e.getMessage() != null) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        if(city == null) {
            if (Logger.DEBUG) {
                Log.d(TAG, "Data process failed");
            }
            return null;
        }

        return getWeatherParser(city.getLat(), city.getLon());
    }

    public static WeatherParser getWeatherParser(float lat, float lon) {
        String urlString = String.format(Locale.getDefault(), CURRENT_URL,
                BaseUrl.WEB_API_URL, lat, lon, WEATHER_API_KEY,
                WeatherApp.getLang());

        WeatherParser parser;
        try {
            parser = WeatherParser.parseWeather(getResultForUri(new URL(urlString)));
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + urlString);
            return null;
        } catch (WeatherRequestException e) {
            if (e.getMessage() != null) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        if(parser == null) {
            if (Logger.DEBUG) {
                Log.d(TAG, "Data process failed");
            }
            return null;
        }

        return parser;
    }
}
