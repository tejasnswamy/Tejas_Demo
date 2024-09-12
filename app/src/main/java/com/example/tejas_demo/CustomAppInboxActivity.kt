package com.example.tejas_demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.clevertap.android.sdk.CTInboxListener
import com.clevertap.android.sdk.CleverTapAPI
import com.example.tejas_demo.adapter.CustomAInboxAdapter
import com.example.tejas_demo.databinding.ActivityCustomAppInboxBinding

class CustomAppInboxActivity : AppCompatActivity(), CTInboxListener {
    lateinit var binding: ActivityCustomAppInboxBinding
    var cleverTapDefaultInstance: CleverTapAPI? = null
    lateinit var customAIAdapter: CustomAInboxAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomAppInboxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.DEBUG)
        cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(applicationContext)
        //Opens Activity with default style config



        val allMessage = cleverTapDefaultInstance!!.allInboxMessages


        binding.customAppInboxRv.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        binding.customAppInboxRv.layoutManager = linearLayoutManager
        customAIAdapter = CustomAInboxAdapter(allMessage, applicationContext)
        binding.customAppInboxRv.adapter = customAIAdapter

        cleverTapDefaultInstance?.apply {
            allMessage.forEach {
                pushInboxNotificationViewedEvent(it.messageId)
                pushInboxNotificationClickedEvent(it.messageId)
            }
        }
    }

    override fun inboxDidInitialize() {

    }

    override fun inboxMessagesDidUpdate() {
    }
}