package ru.softmine.weatherapp.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ru.softmine.weatherapp.R;
import ru.softmine.weatherapp.constants.Logger;

public class PushMessagingService extends FirebaseMessagingService {

    private final static String TAG = PushMessagingService.class.getSimpleName();

    public PushMessagingService() {
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null) {
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();
            if (title == null) {
                title = getString(R.string.push_notification);
            }
            if (message == null) {
                message = getString(R.string.empty_push_body);;
            }

            NotificationIntentService.sendNotification(this, title, message);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        if (Logger.DEBUG) {
            Log.d(TAG, token);
        }
        sendToServer(token);
    }

    private void sendToServer(String token) {

    }


}