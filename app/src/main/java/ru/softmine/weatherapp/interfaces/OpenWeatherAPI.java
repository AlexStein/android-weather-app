package ru.softmine.weatherapp.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.softmine.weatherapp.openweathermodel.CityParser;
import ru.softmine.weatherapp.openweathermodel.WeatherParser;

public interface OpenWeatherAPI {
    @GET("weather")
    Call<CityParser> loadCity(@Query("q") String city, @Query("appid") String keyApi);

    @GET("onecall")
    Call<WeatherParser> loadWeather(@Query("lat") float lat,
                                    @Query("lon") float lon,
                                    @Query("exclude") String exclude,
                                    @Query("appid") String keyApi);
}
