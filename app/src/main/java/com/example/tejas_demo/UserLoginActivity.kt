package com.example.tejas_demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.clevertap.android.sdk.CleverTapAPI
import com.example.tejas_demo.databinding.ActivityUserLoginBinding
import java.util.*

class UserLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserLoginBinding
    private var cleverTapInstance: CleverTapAPI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cleverTapInstance = CleverTapAPI.getDefaultInstance(applicationContext)
        binding.profileName.setText("Tejas")
        binding.profileIdentity.setText("9980742251")
        binding.profileEmail.setText("tejas@outlook.com")
        binding.profilePhone.setText("+919980742251")
        binding.profileLogin.setOnClickListener() {
            callOnUserLogin()
        }
        binding.profilePush.setOnClickListener(){
            callProfilePush()
        }

    }

    private fun callOnUserLogin() {
        // val location = getLocation()!!
        val profileName = binding.profileName.text.trim().toString()
        val profileEmail = binding.profileEmail.text.trim().toString()
        val profilePhone = binding.profilePhone.text.trim().toString()
        val profileIdentity = binding.profileIdentity.text.trim().toString()

        val profile = HashMap<String, Any>()
        profile["Name"] = profileName // String
        profile["Identity"] = profileIdentity // String or number
        profile["Email"] = profileEmail // Email address of the user
        profile["Phone"] = profilePhone
        profile["Gender"] = "M"
        profile["Photo"] = "https://pbs.twimg.com/profile_images/980093006404562945/swewkIEc_400x400.jpg"
        profile["Fav"] = "Biriyani"
        profile["Food"] = "Idli"
        cleverTapInstance?.onUserLogin(profile)
    }
    private fun callProfilePush() {
        // val location = getLocation()!!
        val profileName = binding.profileName.text.trim().toString()
        val profileEmail = binding.profileEmail.text.trim().toString()
        val profilePhone = binding.profilePhone.text.trim().toString()
        val profileIdentity = binding.profileIdentity.text.trim().toString()

        val profile = HashMap<String, Any>()
        profile["Name"] = profileName // String
        profile["Identity"] = profileIdentity // String or number
        profile["Email"] = profileEmail // Email address of the user
        profile["Phone"] = profilePhone
        profile["Gender"] = "M"
        profile["Photo"] = "https://pbs.twimg.com/profile_images/980093006404562945/swewkIEc_400x400.jpg"
        cleverTapInstance?.pushProfile(profile)
    }

    /* private fun getLocation(): Location? {
         var location: Location? = null
         val lm = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
         if (ActivityCompat.checkSelfPermission(
                 this,
                 Manifest.permission.ACCESS_FINE_LOCATION
             ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                 this,
                 Manifest.permission.ACCESS_COARSE_LOCATION
             ) != PackageManager.PERMISSION_GRANTED
         ) {
             location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!

         }

         return location
     }*/
}