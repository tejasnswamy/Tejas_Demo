package com.example.tejas_demo.fcm


import android.os.Bundle
import android.util.Log
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.pushnotification.fcm.CTFcmMessageHandler
import com.example.tejas_demo.sdk.PushNotificationListener
import com.example.tejas_demo.sdk.TemplateRenderer
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFCMService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CleverTapAPI.getDefaultInstance(applicationContext)?.pushFcmRegistrationId(token, true)

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val extras = Bundle()
        for ((key, value) in message.data) {
            extras.putString(key, value)
        }

        if (extras.getString("pt_type").equals("custom")) {
            TemplateRenderer.getInstance().showPushNotification(
                applicationContext,
                extras,
                object :
                    PushNotificationListener {
                    override fun onPushRendered() {
                        CleverTapAPI.getDefaultInstance(applicationContext)!!
                            .pushNotificationViewedEvent(extras) // to track push impression.
                    }

                    override fun onPushFailed() {
                        CTFcmMessageHandler().createNotification(
                            applicationContext,
                            message
                        )
                    }
                })
        } else {
            CTFcmMessageHandler()
                .createNotification(applicationContext, message)
        }
    }
}