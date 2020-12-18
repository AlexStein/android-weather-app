package ru.softmine.weatherapp.openweathermodel;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.softmine.weatherapp.constants.BaseUrl;
import ru.softmine.weatherapp.interfaces.OpenWeatherAPI;

public class WeatherApiHolder {

    private final OpenWeatherAPI openWeatherAPI;

    public WeatherApiHolder() {
        Retrofit retrofit;

        retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl.WEB_API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson()))
                .build();

        openWeatherAPI = retrofit.create(OpenWeatherAPI.class);
    }

    private Gson gson() {
        return new GsonBuilder()
                .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .excludeFieldsWithoutExposeAnnotation().create();
    }

    public OpenWeatherAPI getOpenWeather() {
        return openWeatherAPI;
    }
}
