package com.vincent.givetake.ui.fragment.rewards.catalogue

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vincent.givetake.databinding.FragmentCatalogueRewardBinding
import com.vincent.givetake.factory.RewardsPrefFactory
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.fragment.rewards.RewardsViewModel
import com.vincent.givetake.utils.Result

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class CatalogueRewardFragment : Fragment() {

    private var _binding: FragmentCatalogueRewardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RewardsViewModel
    private lateinit var catalogueRewardAdapter: CatalogueRewardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatalogueRewardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = UserPreferences.getInstance(requireActivity().dataStore)
        val factory = RewardsPrefFactory.getInstance(pref)
        viewModel = ViewModelProvider(requireActivity(), factory)[RewardsViewModel::class.java]
        catalogueRewardAdapter = CatalogueRewardAdapter()


        setObserver()

        binding.rvRewardsCatalogue.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = catalogueRewardAdapter
            setHasFixedSize(true)
        }

    }

    private fun setObserver() {
        viewModel.resultRewards.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    if (it.data?.data!!.isEmpty()) {
                        binding.rvRewardsCatalogue.visibility = View.GONE
                        binding.tvNoDataRewardsCalagoue.visibility = View.VISIBLE
                    } else {
                        catalogueRewardAdapter.setData(it.data.data)
                        binding.rvRewardsCatalogue.visibility = View.VISIBLE
                        binding.tvNoDataRewardsCalagoue.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pgRewardsCatalogue.visibility = View.VISIBLE
            binding.tvNoDataRewardsCalagoue.visibility = View.INVISIBLE
        } else {
            binding.pgRewardsCatalogue.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllRewards()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}