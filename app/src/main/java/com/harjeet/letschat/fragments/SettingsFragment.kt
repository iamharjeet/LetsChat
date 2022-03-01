package com.harjeet.letschat.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.FirebaseDatabase
import com.harjeet.letschat.MyConstants
import com.harjeet.letschat.MyUtils
import com.harjeet.letschat.ProfileActivity
import harjeet.chitForChat.databinding.FragmentSettingsBinding

/* Change Setting of app
* show profile option
* show Logout option
* */
class Settings : Fragment() {
    var firebaseOnlineStatus =
        FirebaseDatabase.getInstance(MyConstants.FIREBASE_BASE_URL)
            .getReference(MyConstants.NODE_ONLINE_STATUS)
    var binding: FragmentSettingsBinding? = null;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(requireActivity().layoutInflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.btnProfile.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    ProfileActivity::class.java
                ).putExtra(
                    MyConstants.PHONE_NUMBER,
                    MyUtils.getStringValue(requireContext(), MyConstants.USER_PHONE)
                )
            )
        }

        binding!!.btnLogout.setOnClickListener {
            if(!MyUtils.getStringValue(requireActivity(),MyConstants.USER_PHONE).equals("")) {
                firebaseOnlineStatus.child(
                    MyUtils.getStringValue(
                        requireContext(),
                        MyConstants.USER_PHONE
                    )
                ).child(MyConstants.NODE_ONLINE_STATUS).setValue("Offline").addOnCompleteListener {
                    MyUtils.clearAllData(requireActivity())
                    requireActivity()!!.finish()
                }}

        }

    }


}