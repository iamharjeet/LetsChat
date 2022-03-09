package com.harjeet.letschat.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.harjeet.letschat.Models.Users
import com.harjeet.letschat.MyConstants
import com.harjeet.letschat.MyUtils
import com.harjeet.letschat.MyUtils.chatNearbyList
import com.harjeet.letschat.adapters.NearbyChatAdapter
import harjeet.chitForChat.databinding.FragmentNearbyBinding

/* Showing Nearby Users list */
class NearbyFragment : Fragment() {
    var binding: FragmentNearbyBinding? = null
    var myLat: String = "0"
    var myLong: String = "0"
    var fetchNearbyList = false
    var firebaseUsers =
        FirebaseDatabase.getInstance(MyConstants.FIREBASE_BASE_URL)
            .getReference(MyConstants.NODE_USERS)

    var isSearching = false
    private var locationCallback: LocationCallback? = null
    private val LOCATION_PERMISSION_REQUEST_CODE: Int = 1
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var locationRequest: LocationRequest? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNearbyBinding.inflate(inflater, container, false);
        return binding!!.getRoot();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity().applicationContext)
        binding!!.imgSearch.setOnClickListener {
            isSearching = true
            searchNearby()
        }

        setAdapter()
    }


    // refreshing list of nearby users
    private fun searchNearby() {
        MyUtils.showProgress(requireActivity())
        myLat = MyUtils.getStringValue(requireActivity(), MyConstants.USER_LATITUDE)
        myLong = MyUtils.getStringValue(requireActivity(), MyConstants.USER_LONGITUDE)
        firebaseUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                MyUtils.stopProgress(requireActivity())
                if (snapshot.exists()) {
                    if (isSearching) {
                        isSearching = false
                        MyUtils.showToast(requireContext(), "Refreshed")
                    }

                    chatNearbyList.clear()
                    for (postSnapshot in snapshot.children) {
                        val user: Users? =
                            postSnapshot.getValue(Users::class.java)

                        if (!user!!.phone.equals(
                                MyUtils.getStringValue(
                                    requireActivity(),
                                    MyConstants.USER_PHONE
                                )
                            ) && !myLat.equals("") && getKmFromLatLong(
                                myLat.toFloat(),
                                myLong.toFloat(),
                                user!!.lat!!.toFloat(),
                                user!!.long!!.toFloat()
                            ) < 0.5
                        ) {
                            chatNearbyList.add(user!!)
                        }


                        setAdapter()

                        // here you can access to name property like university.name
                    }


                }
            }

            override fun onCancelled(error: DatabaseError) {
                MyUtils.stopProgress(requireActivity())
            }

        })
    }

    private fun setAdapter() {
        binding!!.recyclerChatList.adapter =
            NearbyChatAdapter(requireActivity(), chatNearbyList!!)
    }


    fun getKmFromLatLong(lat1: Float, lng1: Float, lat2: Float, lng2: Float): Float {
        val loc1 = Location("")
        loc1.latitude = lat1.toDouble()
        loc1.longitude = lng1.toDouble()
        val loc2 = Location("")
        loc2.latitude = lat2.toDouble()
        loc2.longitude = lng2.toDouble()
        val distanceInMeters = loc1.distanceTo(loc2)
        return distanceInMeters / 1000
    }

    override fun onResume() {
        super.onResume()
        when {
            MyUtils.isAccessFineLocationGranted(requireContext()) -> {
                when {
                    MyUtils.isLocationEnabled(requireContext()) -> {
                        setUpLocationListener()
                    }
                    else -> {
                        MyUtils.showGPSNotEnabledDialog(requireContext())
                    }
                }
            }
            else -> {
                MyUtils.requestAccessFineLocationPermission(
                    requireActivity(),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }


    private fun setUpLocationListener() {
        // for getting the current location update after every 2 seconds with high accuracy
        locationRequest = LocationRequest().setInterval(10000).setFastestInterval(10000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                var lat = ""
                var longi = ""
                for (location in locationResult.locations) {
                    lat = location.latitude.toString()
                    longi = location.longitude.toString()
                }


                var users: Users = Users();
                users.name = MyUtils.getStringValue(requireContext(), MyConstants.USER_NAME)
                users.phone = MyUtils.getStringValue(requireContext(), MyConstants.USER_PHONE)
                users.image = MyUtils.getStringValue(requireContext(), MyConstants.USER_IMAGE)
                users.captions = MyUtils.getStringValue(requireContext(), MyConstants.USER_CAPTIONS)
                users.lat = lat!!
                users.long = longi!!
                if (users.phone != null && !users.phone.equals("")) {
                    firebaseUsers.child(users.phone.toString()).setValue(users)
                }
                MyUtils.saveStringValue(
                    requireContext(),
                    MyConstants.USER_LATITUDE,
                    users.lat.toString()
                )
                MyUtils.saveStringValue(
                    requireContext(),
                    MyConstants.USER_LONGITUDE,
                    users.long.toString()
                )

                if (!fetchNearbyList) {
                    fetchNearbyList = true
                    searchNearby()
                }

                // Few more things we can do here:
                // For example: Update the location of user on server
            }
        }
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

    }


    override fun onDestroyView() {
        super.onDestroyView()
        fusedLocationProviderClient!!.removeLocationUpdates(locationCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient!!.removeLocationUpdates(locationCallback)
    }

    override fun onDetach() {
        super.onDetach()
        fusedLocationProviderClient!!.removeLocationUpdates(locationCallback)
    }

}