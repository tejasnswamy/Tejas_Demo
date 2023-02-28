package com.example.tejas_demo

import com.clevertap.android.pushtemplates.PushTemplateNotificationHandler
import com.clevertap.android.sdk.Application
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.interfaces.NotificationHandler

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        CleverTapAPI.setNotificationHandler(PushTemplateNotificationHandler() as NotificationHandler)
    }
}