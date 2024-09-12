package com.example.tejas_demo.fcm;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.pushnotification.fcm.CTFcmMessageHandler;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FCM  extends FirebaseMessagingService {


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        CleverTapAPI.getDefaultInstance(getApplicationContext()).pushFcmRegistrationId(token,true);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        if(!message.getData().isEmpty()) {
            try {
                Bundle extras = new Bundle();
                for (Map.Entry<String, String> entry : message.getData().entrySet()) {
                    extras.putString(entry.getKey(), entry.getValue());
                }

                CleverTapAPI.processPushNotification(getApplicationContext(),extras );
                new CTFcmMessageHandler()
                        .createNotification(getApplicationContext(), message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
