package com.example.tejas_demo

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.clevertap.android.sdk.*
import com.clevertap.android.sdk.inapp.CTLocalInApp
import com.clevertap.android.sdk.inapp.CTLocalInApp.InAppType
import com.clevertap.android.sdk.pushnotification.CTPushNotificationListener
import com.clevertap.android.sdk.pushnotification.amp.CTPushAmpListener
import com.example.tejas_demo.databinding.ActivityMainBinding
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.*


class MainActivity : BasicActivity() , LocationListener , View.OnClickListener,CTInboxListener,CTPushAmpListener,InAppNotificationButtonListener,
    CTPushNotificationListener, PushPermissionResponseListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var requestLauncher: ActivityResultLauncher<String>
    private var TAG = "FirebaseAnalytics"


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        CleverTapAPI.createNotificationChannel(this,"got","Game of Thrones","Game Of Thrones",NotificationManager.IMPORTANCE_MAX,true,"ding.mp3")
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE)
        cTInstance = CleverTapAPI.getDefaultInstance(applicationContext)
        cTInstance?.ctPushAmpListener = this
        cTInstance?.enableDeviceNetworkInfoReporting(true)
        binding.updateProfile.setOnClickListener(this)
        binding.addEvents.setOnClickListener(this)
        binding.webView.setOnClickListener(this)
        binding.nativeDisplay.setOnClickListener(this)
        binding.appInbox.setOnClickListener(this)
        binding.customInbox.setOnClickListener(this)
        binding.geofence.setOnClickListener(this)
        binding.inapp.setOnClickListener(this)
        binding.productViewed.setOnClickListener(this)
        binding.pushTemplate.setOnClickListener(this)
        binding.fab.setOnClickListener(this)
        cTInstance?.let { ins ->
            Log.i(TAG, "setting object id to firebase : ${ins.cleverTapID}")
            FirebaseAnalytics.getInstance(this).setUserProperty("ct_objectId", ins.cleverTapID)
        } ?: run {
            Log.e(TAG, "Uninstall tracking not setup cause of non initialised instance")
        }
        cTInstance?.registerPushPermissionNotificationResponseListener(this)
        cTInstance?.removeValueForKey("multi500")
        val stuff = ArrayList<String>()
        for (i in 1..1000){
            stuff.add("$i+1234567891011")
        }
        cTInstance?.setMultiValuesForKey("multi500", stuff)

        //val values = cTInstance?.getProperty("multi500") as ArrayList<String>
    /*    for (item in values) {
            println(item)
        }*/

      /*  if (!cleverTapInstance?.isPushPermissionGranted!!) {
            requestLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }*/
        cTInstance?.apply {

            ctNotificationInboxListener = this@MainActivity
            ctPushNotificationListener = this@MainActivity
            setInAppNotificationButtonListener(this@MainActivity)
            //Initialize the inbox and wait for callbacks on overridden methods
            initializeInbox()
        }



        //GeoFence
   /*     val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(applicationContext, "Please grant the Permission", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
            //geoFencing()
        }*/

    }
    @SuppressLint("RestrictedApi")
    private fun askForNotificationPermission() {
        //requestLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            Toast.makeText(applicationContext, "Enable Notification", Toast.LENGTH_SHORT).show()
            val jsonObject = CTLocalInApp.builder()
                .setInAppType(InAppType.HALF_INTERSTITIAL)
                .setTitleText("Get Notified \uD83D\uDD14")
                .setMessageText("Please enable notifications on your device to use Push Notifications.")
                .followDeviceOrientation(true)
                .setPositiveBtnText("Allow")
                .setNegativeBtnText("Cancel")
                .setBackgroundColor(Constants.WHITE)
                .setBtnBorderColor(Constants.BLUE)
                .setTitleTextColor(Constants.BLUE)
                .setMessageTextColor(Constants.BLACK)
                .setBtnTextColor(Constants.WHITE)
                .setBtnBackgroundColor(Constants.BLUE)
                .setFallbackToSettings(true)
                .build()
            cTInstance?.promptPushPrimer(jsonObject)

        } else{
            Toast.makeText(applicationContext, "Push Notification Already Enabled", Toast.LENGTH_SHORT).show()
        }

    }

    fun recordPurchase(cleverTapAPI: CleverTapAPI){
        val charges = hashMapOf<String,Any>("Total Number Of Items" to 3, "Total Amount" to 400)
        val items = arrayListOf(
            hashMapOf<String,Any>("Item name" to "Shoes", "Number of Items" to 1, "Amount" to 200),
            hashMapOf<String,Any>("Item name" to "Watch", "Number of Items" to 1, "Amount" to 100),
            hashMapOf<String,Any>("Item name" to "Biscuit", "Number of Items" to 1, "Amount" to 100),
        )
        cleverTapAPI.pushChargedEvent(charges,items)
    }

    override fun onLocationChanged(location: Location) {
        Toast.makeText(applicationContext,location.toString(),Toast.LENGTH_SHORT).show()
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onClick(v: View?) {
        when (v?.id){
            R.id.update_profile -> startActivity(Intent(this, UserLoginActivity::class.java))
            R.id.add_events -> callEvent()
            R.id.geofence -> startActivity(Intent(this, GeofenceActivity::class.java))
            R.id.inapp -> cTInstance?.pushEvent("In app Event")
            R.id.app_inbox -> {
                cTInstance?.showAppInbox()
                //startActivity(Intent(this, CustomAppInboxActivity::class.java))
            }
            R.id.customInbox -> startActivity(Intent(this, CustomAppInboxActivity::class.java))
            R.id.native_display -> {
                cTInstance?.pushEvent("NativeDisplay Event")
                startActivity(Intent(this, NativeDisplayActivity::class.java))
            }
            R.id.push_template -> cTInstance?.pushEvent("Template Event")
            R.id.product_viewed -> pushProductEvent()

            R.id.web_view -> {
                startActivity(Intent(this,WebViewActivity::class.java))
            }

            R.id.fab -> askForNotificationPermission()


        }
    }



    @SuppressLint("SuspiciousIndentation")
    private fun callEvent(){
        val arrayList = ArrayList<String>()//Creating an empty arraylist
            val prodViewedAction = mapOf(
                "Product Name" to "Casio Chronograph Watch",
                "Category" to "Mens Accessories",
                "Price" to 59.99,
                "Date" to Date(),
                "Data" to arrayList)

        cTInstance?.pushEvent("Product Clicked", prodViewedAction)
    }

    private fun pushProductEvent(){
        val arrayList = ArrayList<String>()
        val prodViewedAction = mapOf(
            "Product Name" to "CleverTap Orange Hoodie",
            "Data" to arrayList)
        cTInstance?.pushEvent("Product viewed",prodViewedAction)
    }

    override fun inboxDidInitialize() {

    }


    override fun inboxMessagesDidUpdate() {

    }

    override fun onPushAmpPayloadReceived(extras: Bundle?) {
        CleverTapAPI.createNotification(applicationContext,extras)
    }

    private fun updateCTProfileIsBrowsing(isBrowsing: Boolean) {
        val profileUpdate = HashMap<String, Any>()
        profileUpdate[getString(R.string.ct_profile_key_is_browsing)] = isBrowsing
        cTInstance?.pushProfile(profileUpdate)
    }

    override fun onStop() {
        super.onStop()
        updateCTProfileIsBrowsing(false)
    }
    override fun onDestroy() {
        super.onDestroy()
        cTInstance?.unregisterPushPermissionNotificationResponseListener(this)

    }
    override fun onNotificationClickedPayloadReceived(p0: HashMap<String, Any>?) {

    }

    override fun onInAppButtonClick(payload: HashMap<String, String>?) {
        val str = payload?.getValue("event")
        Log.d("Lakshya", "Tejas$str")
        Toast.makeText(applicationContext,"Tejas$str",Toast.LENGTH_SHORT).show()
        val settingsIntent: Intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            .putExtra(Settings.EXTRA_CHANNEL_ID, "GOT")
        startActivity(settingsIntent)
        cTInstance?.pushEvent(str)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPushPermissionResponse(accepted: Boolean) {
        Log.d("Clevertap", "onPushPermissionResponse :  InApp---> response() called accepted=$accepted")
        if (accepted) {
            val channel = NotificationChannel(
                "got",
                "Game of Thrones",
                NotificationManager.IMPORTANCE_HIGH,
                )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

}
