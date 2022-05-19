package com.vincent.givetake.ui.fragment.items.myitems

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
import com.vincent.givetake.databinding.FragmentMyItemsBinding
import com.vincent.givetake.factory.ItemsPrefViewModelFactory
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.fragment.items.ItemsViewModel
import com.vincent.givetake.utils.Result

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class MyItemsFragment : Fragment() {

    private var _binding: FragmentMyItemsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ItemsViewModel
    private lateinit var myItemsAdapter: MyItemsAdapter
    private var token = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMyItemsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pref = UserPreferences.getInstance(requireActivity().dataStore)
        val factory = ItemsPrefViewModelFactory.getInstance(pref)
        viewModel = ViewModelProvider(requireActivity(), factory)[ItemsViewModel::class.java]
        myItemsAdapter = MyItemsAdapter()

        binding.rvMyItems.apply {
            adapter = myItemsAdapter
            layoutManager = LinearLayoutManager(requireActivity())
            setHasFixedSize(true)
        }

        setObserver()

    }

    private fun setObserver() {
        viewModel.getAccessKey().observe(viewLifecycleOwner) {
            if (it != null && it != "") {
                token = it
                viewModel.getMyItems(token)
            }
        }

        viewModel.myItemsResult.observe(viewLifecycleOwner) {
            when(it) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    if (it.data?.data?.isEmpty() == true) {
                        binding.tvNoDataMyItems.visibility = View.VISIBLE
                        binding.rvMyItems.visibility = View.GONE
                    }else {
                        binding.tvNoDataMyItems.visibility = View.GONE
                        binding.rvMyItems.visibility = View.VISIBLE
                        myItemsAdapter.setData(it.data!!.data)
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
            binding.pgMyItems.visibility = View.VISIBLE
            binding.rvMyItems.visibility = View.GONE
        }else {
            binding.pgMyItems.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMyItems(token)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}