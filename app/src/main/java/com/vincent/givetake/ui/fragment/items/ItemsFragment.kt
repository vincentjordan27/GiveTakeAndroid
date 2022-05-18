package com.vincent.givetake.ui.fragment.items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.vincent.givetake.databinding.FragmentItemsBinding
import com.vincent.givetake.ui.fragment.rewards.TabRewardAdapter
import com.vincent.givetake.ui.fragment.rewards.main.RewardsFragment

class ItemsFragment : Fragment() {

    private var _binding: FragmentItemsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemsBinding.inflate(layoutInflater)
        val view = binding.root
        val pagerAdapter = TabItemsAdapter(requireActivity())
        binding.viewpager.adapter = pagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tabs, position ->
            tabs.text = tabTitles[position]
        }.attach()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val tabTitles = arrayOf(
            "Tawaran",
            "Barang",
            "Wishlist"
        )
    }
}