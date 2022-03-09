package com.harjeet.letschat

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.FirebaseDatabase
import com.harjeet.letschat.Models.Users
import com.harjeet.letschat.adapters.HomeTabApapter
import harjeet.chitForChat.databinding.ActivityHomeBinding
import java.util.*
import harjeet.chitForChat.R
/* Showing all three screens (chats,nearby and settings ) on activity
* Updating location of user
* Handling online or offline status of user
* */
class HomeActivity : AppCompatActivity() {
    private val LOCATION_PERMISSION_REQUEST_CODE: Int = 1
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var locationRequest: LocationRequest? = null
    lateinit var binding: ActivityHomeBinding
    private var locationCallback: LocationCallback? = null
    var firebaseUsers =
        FirebaseDatabase.getInstance(MyConstants.FIREBASE_BASE_URL)
            .getReference(MyConstants.NODE_USERS)
    var firebaseOnlineStatus =
        FirebaseDatabase.getInstance(MyConstants.FIREBASE_BASE_URL)
            .getReference(MyConstants.NODE_ONLINE_STATUS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.myViewPager.adapter = HomeTabApapter(supportFragmentManager)
        binding.myTablayout.setupWithViewPager(binding.myViewPager)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this@HomeActivity)


    }


    //getting current location of user

    override fun onResume() {
        super.onResume()
        if (!MyUtils.getStringValue(this@HomeActivity, MyConstants.USER_PHONE).equals(""))
            firebaseOnlineStatus.child(
                MyUtils.getStringValue(
                    this@HomeActivity,
                    MyConstants.USER_PHONE
                )
            ).child(MyConstants.NODE_ONLINE_STATUS).setValue("Online")
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onPause() {
        super.onPause()
        if (!MyUtils.getStringValue(this@HomeActivity, MyConstants.USER_PHONE).equals(""))
            firebaseOnlineStatus.child(
                MyUtils.getStringValue(
                    this@HomeActivity,
                    MyConstants.USER_PHONE
                )
            ).child(MyConstants.NODE_ONLINE_STATUS).setValue(MyUtils.convertIntoTime(Calendar.getInstance().timeInMillis.toString()))

    }


}