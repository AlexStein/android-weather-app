package ru.softmine.weatherapp.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ru.softmine.weatherapp.constants.BundleKeys;
import ru.softmine.weatherapp.constants.Logger;
import ru.softmine.weatherapp.openweathermodel.WeatherRequest;

/**
 * Служба для получения информаци о погоде
 */
public class WeatherIntentService extends IntentService {

    private static final String TAG = WeatherIntentService.class.getName();

    private static final String ACTION_WEATHER_UPDATE = "ru.softmine.weatherapp.services.action.ACTION_WEATHER_UPDATE";
    private static final String EXTRA_CITY_NAME = "ru.softmine.weatherapp.services.extra.EXTRA_CITY_NAME";

    public WeatherIntentService() {
        super("WeatherIntentService");
    }

    /**
     * Запустить процесс обновления сводки погоды
     */
    public static void startWeatherUpdate(Context context, String cityName) {
        Intent intent = new Intent(context, WeatherIntentService.class);
        intent.setAction(ACTION_WEATHER_UPDATE);
        intent.putExtra(EXTRA_CITY_NAME, cityName);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String cityName = intent.getStringExtra(EXTRA_CITY_NAME);
            handleWeatherRequest(cityName);
        }
    }

    /**
     * Обработка запросов
     */
    private void handleWeatherRequest(String cityName) {
        boolean success = WeatherRequest.getWeatherParser(cityName);
        sendBroadcastResult(cityName, success);
    }

    private void sendBroadcastResult(String cityName, boolean success) {
        if (Logger.DEBUG) {
            Log.d(TAG, "sendBroadcastResult()");
        }

        Intent broadcastIntent = new Intent(BundleKeys.BROADCAST_ACTION_WEATHER_UPDATED);
        broadcastIntent.putExtra(BundleKeys.WEATHER_UPDATED_CITY, cityName);
        broadcastIntent.putExtra(BundleKeys.WEATHER_UPDATED_SUCCESS, success);
        sendBroadcast(broadcastIntent);
    }
}