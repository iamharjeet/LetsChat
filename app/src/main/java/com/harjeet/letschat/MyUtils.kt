package com.harjeet.letschat

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import harjeet.chitForChat.R
import java.text.SimpleDateFormat
import java.util.*

/*
*  Make common methods that can be call from anywhere from application
*/
object MyUtils {
    var dialog: Dialog? = null;

    // showing a message toast
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }


    // saving text data in local database
    fun saveStringValue(context: Context, key: String, value: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MySharedPrefChitForChat", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        myEdit.putString(key, value)
        myEdit.commit()
    }


    //getting saved text from local database
    fun getStringValue(context: Context, key: String): String {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MySharedPrefChitForChat", MODE_PRIVATE)
        return sharedPreferences.getString(key, "").toString()
    }


    // saving boolean value in local database

    fun saveBooleanValue(context: Context, key: String, value: Boolean) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MySharedPrefChitForChat", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        myEdit.putBoolean(key, value)
        myEdit.commit()
    }


    // getting boolean from local database
    fun getBooleanValue(context: Context, key: String): Boolean {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MySharedPrefChitForChat", MODE_PRIVATE)
        return sharedPreferences.getBoolean(key, false)
    }

    // clear all save data of local database
    fun clearAllData(context: Context) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MySharedPrefChitForChat", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        myEdit.clear()
        myEdit.commit()
    }


    // showing progress bar while hitting api's
    fun showProgress(context: Context) {
        dialog = Dialog(context);
        dialog!!.setContentView(R.layout.dialog_progress)
        dialog!!.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);
        dialog!!.show()
    }


    // stop showing progress
    fun stopProgress(context: Context) {
        dialog!!.cancel()
    }


    // request for permissions
    fun requestAccessFineLocationPermission(activity: AppCompatActivity, requestId: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            requestId
        )
    }

    /**
     * Function to check if the location permissions are granted or not
     */
    fun isAccessFineLocationGranted(context: Context): Boolean {
        return ContextCompat
            .checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat
            .checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Function to check if location of the device is enabled or not
     */
    fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /**
     * Function to show the "enable GPS" Dialog box
     */
    fun showGPSNotEnabledDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.enable_gps))
            .setMessage(context.getString(R.string.required_for_this_app))
            .setCancelable(false)
            .setPositiveButton(context.getString(R.string.enable_now)) { _, _ ->
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .show()
    }


    // Convert a timestamp into time
    fun convertIntoTime(timeStamp: String): String {
        var Timestamp: Long = timeStamp.toLong()
        var timeD: Date = Date(Timestamp)
        var sdf: SimpleDateFormat = SimpleDateFormat("HH:mm a")
        return sdf.format(timeD)
    }

    // convert a timestamp into date
    fun convertIntoDate(timeStamp: String): String {
        var Timestamp: Long = timeStamp.toLong()
        var timeD: Date = Date(Timestamp)
        var sdf: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        return sdf.format(timeD)
    }


    // showing profile pic dialog
    fun showProfileDialog(context: Context, imageUrl: String) {
        var dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_show_profile)
        var imgUser = dialog.findViewById<CircleImageView>(R.id.imgUser)

        dialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.window!!.setLayout(
            GridLayoutManager.LayoutParams.MATCH_PARENT,
            GridLayoutManager.LayoutParams.WRAP_CONTENT
        )
        if (!imageUrl.equals("")) {
            Glide.with(context).load(imageUrl).placeholder(R.drawable.user).into(imgUser)
        }
        dialog.show()

    }
}