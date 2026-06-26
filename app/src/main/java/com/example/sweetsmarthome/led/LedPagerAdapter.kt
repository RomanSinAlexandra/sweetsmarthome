package com.example.sweetsmarthome.led

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class LedPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LedControlFragment()
            1 -> LedFunctionsFragment()
            else -> throw IllegalArgumentException("Некорректная позиция: $position")
        }
    }
}