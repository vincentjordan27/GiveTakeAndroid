package com.vincent.givetake.ui.fragment.items.wishlist

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
import androidx.recyclerview.widget.GridLayoutManager
import com.vincent.givetake.R
import com.vincent.givetake.databinding.FragmentWishlistBinding
import com.vincent.givetake.factory.ItemsPrefViewModelFactory
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.fragment.items.ItemsViewModel
import com.vincent.givetake.utils.Result

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class WishlistFragment : Fragment() {

    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ItemsViewModel
    private var token = ""
    private lateinit var wishlistAdapter: WishlistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishlistBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = UserPreferences.getInstance(requireActivity().dataStore)
        val factory = ItemsPrefViewModelFactory.getInstance(pref)
        viewModel = ViewModelProvider(requireActivity(), factory)[ItemsViewModel::class.java]

        wishlistAdapter = WishlistAdapter()

        binding.rvWish.apply {
            adapter = wishlistAdapter
            layoutManager = GridLayoutManager(requireActivity(), 2)
            setHasFixedSize(true)
        }
        setObserver()

    }

    private fun setObserver() {
        viewModel.getAccessKey().observe(viewLifecycleOwner) {
            if (it != null && it != "") {
                token = it
                wishlistAdapter.token = token
                viewModel.getWishlist(token)
            }
        }

        viewModel.resultGetWishlist.observe(viewLifecycleOwner) {
            when(it) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    if (it.data?.data?.isEmpty() == true) {
                        binding.tvNoDataWish.visibility = View.VISIBLE
                        binding.rvWish.visibility = View.GONE
                    }else {
                        binding.tvNoDataWish.visibility = View.GONE
                        binding.rvWish.visibility = View.VISIBLE
                        wishlistAdapter.setData(it.data!!.data)
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
            binding.pgWish.visibility = View.VISIBLE
            binding.rvWish.visibility = View.GONE
        }else {
            binding.pgWish.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getWishlist(token)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}