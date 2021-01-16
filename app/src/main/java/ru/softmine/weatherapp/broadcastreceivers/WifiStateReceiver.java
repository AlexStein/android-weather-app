package ru.softmine.weatherapp.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;

import ru.softmine.weatherapp.R;
import ru.softmine.weatherapp.WeatherApp;
import ru.softmine.weatherapp.constants.Logger;
import ru.softmine.weatherapp.services.NotificationIntentService;

public class WifiStateReceiver extends BroadcastReceiver {

    private final static String TAG = WifiStateReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Logger.VERBOSE) {
            Log.v(TAG, "Action " + action + " received");
        }
        String title = WeatherApp.getAppContext().getString(R.string.no_connection_title);
        switch (action) {
            case WifiManager.WIFI_STATE_CHANGED_ACTION:
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                if (wifiState != WifiManager.WIFI_STATE_ENABLED) {
                    if (Logger.VERBOSE) {
                        Log.v(TAG, "Wifi is not enabled");
                    }
                    String message = WeatherApp.getAppContext().getString(R.string.no_wifi_message);
                    NotificationIntentService.sendNotification(context, title, message);
                }
                break;
            case Intent.ACTION_AIRPLANE_MODE_CHANGED:
                boolean isAirplaneModeOn = intent.getBooleanExtra("state", false);
                if (Logger.VERBOSE) {
                    Log.v(TAG, "Airplane mode changed");
                }
                if (isAirplaneModeOn) {
                    String message = WeatherApp.getAppContext().getString(R.string.airplane_mode_message);
                    NotificationIntentService.sendNotification(context, title, message);
                }
                break;
            case ConnectivityManager.CONNECTIVITY_ACTION:
                boolean isConnected = intent.getBooleanExtra(
                        ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
                if (Logger.VERBOSE) {
                    Log.v(TAG, "Connectivity changed");
                }
                if (!isConnected) {
                    String message = WeatherApp.getAppContext().getString(R.string.no_internet_connection);
                    NotificationIntentService.sendNotification(context, title, message);
                }
        }
    }
}
