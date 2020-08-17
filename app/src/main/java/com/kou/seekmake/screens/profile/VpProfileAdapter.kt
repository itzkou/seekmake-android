package com.kou.seekmake.screens.profile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class VpProfileAdapter internal constructor(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private val mFragmentList = ArrayList<Fragment>()
    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Projects"
            1 -> "Quote"
            else -> return "Updates"


        }
    }


    fun addFragment(fragment: Fragment) {  //This one adds fragments to the array
        mFragmentList.add(fragment)
    }
}