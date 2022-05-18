package com.vincent.givetake.ui.fragment.items

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vincent.givetake.ui.fragment.items.myitems.MyItemsFragment
import com.vincent.givetake.ui.fragment.items.myoffers.MyOffersFragment
import com.vincent.givetake.ui.fragment.items.wishlist.WishlistFragment
import com.vincent.givetake.ui.fragment.rewards.catalogue.CatalogueRewardFragment
import com.vincent.givetake.ui.fragment.rewards.history.HistoryRewardFragment

class TabItemsAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MyOffersFragment()
            1 -> MyItemsFragment()
            else -> WishlistFragment()
        }
    }
}