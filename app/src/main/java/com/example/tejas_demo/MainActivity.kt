package com.example.tejas_demo

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.clevertap.android.geofence.CTGeofenceAPI
import com.clevertap.android.geofence.CTGeofenceSettings
import com.clevertap.android.geofence.Logger
import com.clevertap.android.geofence.interfaces.CTGeofenceEventsListener
import com.clevertap.android.sdk.CTInboxListener
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.PushPermissionResponseListener
import com.clevertap.android.sdk.pushnotification.amp.CTPushAmpListener
import com.example.tejas_demo.databinding.ActivityMainBinding
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity() , LocationListener , View.OnClickListener,CTInboxListener,CTPushAmpListener{
    private var cleverTapInstance:CleverTapAPI? = null
    var ctGeofenceAPI : CTGeofenceAPI? = null
    lateinit var binding: ActivityMainBinding
    private lateinit var requestLauncher: ActivityResultLauncher<String>


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        "got",
                        "Game of Thrones",
                        NotificationManager.IMPORTANCE_HIGH,

                        )
                    val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    manager.createNotificationChannel(channel)
                }
            }
            else {
                //show error message
                Toast.makeText(applicationContext, "Enable Notification To Receive Notifications", Toast.LENGTH_SHORT).show()
            }
        }

        cleverTapInstance = CleverTapAPI.getDefaultInstance(applicationContext)
        ctGeofenceAPI = CTGeofenceAPI.getInstance(applicationContext)
        cleverTapInstance?.ctPushAmpListener = this
        cleverTapInstance?.enableDeviceNetworkInfoReporting(true)
        binding.updateProfile.setOnClickListener(this)
        binding.addEvents.setOnClickListener(this)
        binding.webView.setOnClickListener(this)
        binding.nativeDisplay.setOnClickListener(this)
        binding.appInbox.setOnClickListener(this)
        binding.customInbox.setOnClickListener(this)
        binding.geofence.setOnClickListener(this)
        binding.inapp.setOnClickListener(this)
        binding.pushTemplate.setOnClickListener(this)

        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.DEBUG)
        cleverTapInstance?.apply {

            ctNotificationInboxListener = this@MainActivity

            //Initialize the inbox and wait for callbacks on overridden methods
            initializeInbox()
        }
        if (!cleverTapInstance?.isPushPermissionGranted!!){

            askForNotificationPermission()
            Toast.makeText(applicationContext, "Enable Notification", Toast.LENGTH_SHORT).show()
        }



        //GeoFence
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(applicationContext, "Please grant the Permission", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            //Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
            geoFencing()
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun askForNotificationPermission() {
        requestLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
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

    private fun geoFencing() {
        val ctGeofenceSettings = CTGeofenceSettings.Builder()
            .enableBackgroundLocationUpdates(true)//boolean to enable background location updates
            .setLogLevel(Logger.DEBUG)//Log Level
            .setLocationAccuracy(CTGeofenceSettings.ACCURACY_HIGH)//byte value for Location Accuracy
            .setLocationFetchMode(CTGeofenceSettings.FETCH_LAST_LOCATION_PERIODIC)//byte value for Fetch Mode
            .setGeofenceMonitoringCount(50)//int value for number of Geofences CleverTap can monitor
            .setInterval(1800000)//long value for interval in milliseconds
            .setFastestInterval(1800000)//long value for fastest interval in milliseconds
            .setSmallestDisplacement(200F)//float value for smallest Displacement in meters
            .setGeofenceNotificationResponsiveness(0)// int value for geofence notification responsiveness in milliseconds
            .build()

        ctGeofenceAPI?.init(ctGeofenceSettings, cleverTapInstance!!)

        try {
            CTGeofenceAPI.getInstance(applicationContext).triggerLocation()
        } catch (e: IllegalStateException) { // thrown when this method is called before geofence SDK initialization
            e.printStackTrace()
        }

        //callbacks
        CTGeofenceAPI.getInstance(applicationContext).setOnGeofenceApiInitializedListener {
            //App is notified on the main thread that CTGeofenceAPI is initialized
            Log.d("clevertap_geofence", "CTGeofenceAPI is initialized")
            println("CTGeofenceAPI is initialized called")
        }

        CTGeofenceAPI.getInstance(applicationContext)
            .setCtGeofenceEventsListener(object : CTGeofenceEventsListener {
                override fun onGeofenceEnteredEvent(jsonObject: JSONObject) {
                    //Callback on the main thread when the user enters Geofence with info in jsonObject
                    Log.d("clevertap_geofence", "onGeofenceEnteredEvent: entered")
                    Log.d("clevertap_geofence", jsonObject.toString())
                    println("geofence entered called")
                }

                override fun onGeofenceExitedEvent(jsonObject: JSONObject) {
                    //Callback on the main thread when user exits Geofence with info in jsonObject
                    Log.d("clevertap_geofence", "onGeofenceExitedEvent: entered")
                    Log.d("clevertap_geofence", jsonObject.toString())
                    println("geofence exited called")
                }
            })

        CTGeofenceAPI.getInstance(applicationContext).setCtLocationUpdatesListener {
            //New location on the main thread as provided by the Android OS
            cleverTapInstance?.setLocationForGeofences(it,Build.VERSION.SDK_INT)
        }

        //for deactivation
//        CTGeofenceAPI.getInstance(applicationContext).deactivate()
    }

    override fun onLocationChanged(location: Location) {

    }

    override fun onClick(v: View?) {
        when (v?.id){
            R.id.update_profile -> startActivity(Intent(this, UserLoginActivity::class.java))
            R.id.add_events -> callEvent()
            R.id.geofence -> startActivity(Intent(this, GeofenceActivity::class.java))
            R.id.inapp -> cleverTapInstance?.pushEvent("In app Event")
            R.id.app_inbox -> {
                cleverTapInstance?.showAppInbox()
                //startActivity(Intent(this, CustomAppInboxActivity::class.java))
            }
            R.id.customInbox -> startActivity(Intent(this, CustomAppInboxActivity::class.java))
            R.id.native_display -> {
                cleverTapInstance?.pushEvent("NativeDisplay Event")
                startActivity(Intent(this, NativeDisplayActivity::class.java))
            }
            R.id.push_template -> cleverTapInstance?.pushEvent("Template Event")

            R.id.web_view -> startActivity(Intent(this,WebViewActivity::class.java))


        }
    }



    private fun callEvent(){
        val arrayList = ArrayList<String>()//Creating an empty arraylist
            arrayList.add("Men")//Adding object in arraylist
            arrayList.add("Accessories")
            arrayList.add("59.99")
            arrayList.add("Rohan")
            val prodViewedAction = mapOf(
                "Product Name" to "Casio Chronograph Watch",
                "Category" to "Mens Accessories",
                "Price" to 59.99,
                "Date" to Date(),
                "Data" to arrayList)

            cleverTapInstance?.pushEvent("Product Clicked", prodViewedAction)
           // cleverTapInstance?.let { it1 -> recordPurchase(it1) }
    }

    override fun inboxDidInitialize() {

    }


    override fun inboxMessagesDidUpdate() {

    }

    override fun onPushAmpPayloadReceived(extras: Bundle?) {

    }

    override fun onDestroy() {
        super.onDestroy()

    }


}
