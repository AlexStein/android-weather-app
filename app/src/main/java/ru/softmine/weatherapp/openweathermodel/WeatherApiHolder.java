package ru.softmine.weatherapp.openweathermodel;

import android.content.Intent;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.softmine.weatherapp.BuildConfig;
import ru.softmine.weatherapp.WeatherApp;
import ru.softmine.weatherapp.constants.BundleKeys;
import ru.softmine.weatherapp.constants.Logger;
import ru.softmine.weatherapp.interfaces.OpenWeatherAPI;

public class WeatherApiHolder {

    private static final String TAG = WeatherApiHolder.class.getName();

    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private final String EXCLUDE = "minutely,hourly,alerts";
    private final OpenWeatherAPI openWeatherAPI;

    public WeatherApiHolder() {
        Retrofit retrofit;

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson()))
                .build();

        openWeatherAPI = retrofit.create(OpenWeatherAPI.class);
    }

    private Gson gson() {
        return new GsonBuilder()
                .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .excludeFieldsWithoutExposeAnnotation().create();
    }
//
//    public OpenWeatherAPI getOpenWeather() {
//        return openWeatherAPI;
//    }

    public void requestWeatherUpdate(String cityName) {

        openWeatherAPI.loadCity(cityName, BuildConfig.WEATHER_API_KEY).enqueue(new Callback<WeatherParser>() {
            @Override
            public void onResponse(Call<WeatherParser> call, Response<WeatherParser> response) {
                float lat = response.body().getLat();
                float lon = response.body().getLon();

                openWeatherAPI.loadWeather(lat, lon, EXCLUDE, BuildConfig.WEATHER_API_KEY).enqueue(new Callback<WeatherParser>() {
                    @Override
                    public void onResponse(Call<WeatherParser> call, Response<WeatherParser> response) {
                        WeatherApp.setWeatherParser(response.body());
                        sendBroadcastResult(cityName, true, "");
                    }

                    @Override
                    public void onFailure(Call<WeatherParser> call, Throwable t) {
                        sendBroadcastResult(cityName, false, t.getMessage());
                        if (Logger.DEBUG) {
                            Log.e(TAG, t.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<WeatherParser> call, Throwable t) {
                sendBroadcastResult(cityName, false, t.getMessage());
                if (Logger.DEBUG) {
                    Log.e(TAG, t.getMessage());
                }
            }
        });

    }

    private void sendBroadcastResult(String cityName, boolean success, String message) {
        if (Logger.DEBUG) {
            Log.d(TAG, "sendBroadcastResult()");
        }

        Intent broadcastIntent = new Intent(BundleKeys.BROADCAST_ACTION_WEATHER_UPDATED);
        broadcastIntent.putExtra(BundleKeys.WEATHER_UPDATED_CITY, cityName);
        broadcastIntent.putExtra(BundleKeys.WEATHER_UPDATED_SUCCESS, success);
        broadcastIntent.putExtra(BundleKeys.WEATHER_UPDATED_MESSAGE, message);

        WeatherApp.getAppContext().sendBroadcast(broadcastIntent);
    }
}
