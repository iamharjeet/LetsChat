package com.harjeet.letschat.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.harjeet.letschat.fragments.ChatsFragment
import com.harjeet.letschat.fragments.NearbyFragment
import com.harjeet.letschat.fragments.Settings

class HomeTabApapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 3;

    }

    override fun getItem(position: Int): Fragment {
        if (position == 0) return NearbyFragment();
        if (position == 1) return ChatsFragment()
        if (position == 2) return Settings();
        else return NearbyFragment()
    }

    override fun getPageTitle(position: Int): CharSequence? {

        if (position == 0) return "Nearby";
        if (position == 1) return "Chat"
        if (position == 2) return "Settings"
        else return "Nearby"
        return super.getPageTitle(position)

    }
}