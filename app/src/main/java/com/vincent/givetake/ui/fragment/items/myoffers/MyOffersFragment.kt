package com.vincent.givetake.ui.fragment.items.myoffers

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
import com.vincent.givetake.R
import com.vincent.givetake.databinding.FragmentMyOffersBinding
import com.vincent.givetake.factory.ItemsPrefViewModelFactory
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.fragment.items.ItemsViewModel
import com.vincent.givetake.utils.Result

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class MyOffersFragment : Fragment() {

    private var _binding: FragmentMyOffersBinding?= null
    private val binding get() = _binding!!
    private lateinit var viewModel: ItemsViewModel
    private lateinit var adapterMyOffer: MyOffersAdapter
    private var token = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyOffersBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = UserPreferences.getInstance(requireActivity().dataStore)
        val factory = ItemsPrefViewModelFactory.getInstance(pref)
        viewModel = ViewModelProvider(requireActivity(), factory)[ItemsViewModel::class.java]
        setObserver()
        adapterMyOffer = MyOffersAdapter()

        binding.rvMyOffers.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = adapterMyOffer
            setHasFixedSize(true)
        }
    }

    private fun setObserver() {
        viewModel.getAccessKey().observe(viewLifecycleOwner) {
            if (it != null && it != "" ) {
                token = it
                adapterMyOffer.token = it
                viewModel.getMyOffers(it)
            }
        }
        viewModel.myOfferResult.observe(viewLifecycleOwner) {
            when(it) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    if (it.data?.data?.isEmpty() == true) {
                        binding.tvNoDataMyOffers.visibility = View.VISIBLE
                        binding.rvMyOffers.visibility = View.GONE
                    }else {
                        binding.tvNoDataMyOffers.visibility = View.GONE
                        binding.rvMyOffers.visibility = View.VISIBLE
                        adapterMyOffer.setData(it.data!!.data)
                    }
                }
                is Result.Error -> {
                    showLoading(false)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pgMyOffers.visibility = View.VISIBLE
        }else {
            binding.pgMyOffers.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMyOffers(token)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}