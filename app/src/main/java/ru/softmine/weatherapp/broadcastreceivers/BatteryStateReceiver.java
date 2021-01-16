package ru.softmine.weatherapp.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

import ru.softmine.weatherapp.R;
import ru.softmine.weatherapp.WeatherApp;
import ru.softmine.weatherapp.constants.Logger;
import ru.softmine.weatherapp.services.NotificationIntentService;

public class BatteryStateReceiver extends BroadcastReceiver {

    private final static String TAG = BatteryStateReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Logger.VERBOSE) {
            Log.v(TAG, "Action " + action + " received");
        }
        if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                boolean isBatterLow = intent.getBooleanExtra(BatteryManager.EXTRA_BATTERY_LOW, false);
                if (isBatterLow) {
                    if (Logger.VERBOSE) {
                        Log.v(TAG, "Low battery");
                    }
                    String title = WeatherApp.getAppContext().getString(R.string.battery_low);
                    String message = WeatherApp.getAppContext().getString(R.string.no_low_message);
                    NotificationIntentService.sendNotification(context, title, message);
                }
            }
        } else if (action.equals(Intent.ACTION_BATTERY_LOW)) {
            String title = WeatherApp.getAppContext().getString(R.string.battery_low);
            String message = WeatherApp.getAppContext().getString(R.string.no_low_message);
            NotificationIntentService.sendNotification(context, title, message);
        }
    }
}