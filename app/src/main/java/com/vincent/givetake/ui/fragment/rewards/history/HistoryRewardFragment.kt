package com.vincent.givetake.ui.fragment.rewards.history

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vincent.givetake.R
import com.vincent.givetake.databinding.FragmentHistoryRewardBinding
import com.vincent.givetake.factory.RewardsPrefFactory
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.fragment.rewards.RewardsViewModel
import com.vincent.givetake.utils.Result

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class HistoryRewardFragment : Fragment() {

    private var _binding: FragmentHistoryRewardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RewardsViewModel
    private lateinit var adapterHistory: HistoryRewardAdapter
    private var token = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHistoryRewardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pref = UserPreferences.getInstance(requireActivity().dataStore)
        val factory = RewardsPrefFactory.getInstance(pref)
        viewModel = ViewModelProvider(this, factory)[RewardsViewModel::class.java]
        adapterHistory = HistoryRewardAdapter()
        binding.rvRewardsHistory.apply {
            adapter = adapterHistory
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
        setObserver()

    }

    private fun setObserver() {
        viewModel.getAccessKey().observe(viewLifecycleOwner) {
            if (it != null && it != "") {
                token = it
                viewModel.getMyRewards(token)
            }
        }

        viewModel.resultMyReward.observe(viewLifecycleOwner) {
            when(it) {
                is Result.Loading -> {
                    binding.pgRewardsHistory.visibility = View.VISIBLE
                    binding.rvRewardsHistory.visibility = View.INVISIBLE
                }
                is Result.Success -> {
                    binding.pgRewardsHistory.visibility = View.GONE
                    if (it.data?.data!!.isEmpty()) {
                        binding.tvNoDataRewardsHistory.visibility = View.VISIBLE
                        binding.rvRewardsHistory.visibility = View.INVISIBLE
                    } else {
                        binding.tvNoDataRewardsHistory.visibility = View.INVISIBLE
                        binding.rvRewardsHistory.visibility = View.VISIBLE
                        adapterHistory.setData(it.data.data)
                    }
                }
                is Result.Error -> {
                    Toast.makeText(requireActivity(), it.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMyRewards(token)
    }

}