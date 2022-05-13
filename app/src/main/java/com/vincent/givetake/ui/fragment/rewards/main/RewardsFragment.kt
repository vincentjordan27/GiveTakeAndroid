package com.vincent.givetake.ui.fragment.rewards.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.vincent.givetake.R
import com.vincent.givetake.databinding.FragmentRewardsBinding
import com.vincent.givetake.ui.fragment.rewards.RewardsViewModel
import com.vincent.givetake.ui.fragment.rewards.TabRewardAdapter

class RewardsFragment : Fragment() {

    private var _binding: FragmentRewardsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRewardsBinding.inflate(inflater, container, false)

        val view = binding.root
        val pagerAdapter = TabRewardAdapter(requireActivity())
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
            "Katalog",
            "Riwayat"
        )
    }
}