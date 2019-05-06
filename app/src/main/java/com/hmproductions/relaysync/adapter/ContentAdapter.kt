package com.hmproductions.relaysync.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hmproductions.relaysync.fragments.AlternatorFragment
import com.hmproductions.relaysync.fragments.RelayFragment

class ContentAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> RelayFragment()
            else -> AlternatorFragment()
        }
    }

    override fun getCount() = NUMBER_OF_FRAGMENTS

    override fun getPageTitle(position: Int): CharSequence? {

        return when (position) {
            0 -> "IDMT Protection"
            else -> "Alternator"
        }
    }

    companion object {
        private const val NUMBER_OF_FRAGMENTS = 2
    }
}