package com.vincent.givetake.ui.fragment.rewards

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vincent.givetake.ui.fragment.rewards.catalogue.CatalogueRewardFragment
import com.vincent.givetake.ui.fragment.rewards.history.HistoryRewardFragment

class TabRewardAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CatalogueRewardFragment()
            else -> HistoryRewardFragment()
        }
    }
}