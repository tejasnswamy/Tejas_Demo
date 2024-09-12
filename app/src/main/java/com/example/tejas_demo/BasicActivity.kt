package com.example.tejas_demo

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.clevertap.android.sdk.CleverTapAPI

open class BasicActivity : AppCompatActivity() {
    var cTInstance: CleverTapAPI? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        cTInstance = CleverTapAPI.getDefaultInstance(applicationContext)
        CleverTapAPI.setDebugLevel(3)

    }
}