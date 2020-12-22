package ru.softmine.weatherapp.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import ru.softmine.weatherapp.R;
import ru.softmine.weatherapp.constants.Notifications;

public class NotificationIntentService extends IntentService {

    private static final String EXTRA_TITLE = "ru.softmine.weatherapp.services.extra.TITLE";
    private static final String EXTRA_MESSAGE = "ru.softmine.weatherapp.services.extra.MESSAGE";

    private int messageId = 0;

    public NotificationIntentService() {
        super("NotificationIntentService");
    }

    public static void sendNotification(Context context, String title, String message) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String title = intent.getStringExtra(EXTRA_TITLE);
            String message = intent.getStringExtra(EXTRA_MESSAGE);

            createNotification(title, message);
        }
    }

    private void createNotification(String title, String message) {
        Notification notification = new Notification.Builder(this, Notifications.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(messageId++, notification);
    }
}