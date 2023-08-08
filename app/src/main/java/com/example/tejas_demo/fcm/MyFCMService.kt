package com.example.tejas_demo.fcm


import android.os.Bundle
import android.util.Log
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.pushnotification.fcm.CTFcmMessageHandler
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFCMService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CleverTapAPI.getDefaultInstance(applicationContext)?.pushFcmRegistrationId(token, true)

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        try {
            CTFcmMessageHandler()
                .createNotification(applicationContext, message)
        } catch (e: Throwable) {
            Log.d("Clevertap", e.message + "");
        }
    }
}