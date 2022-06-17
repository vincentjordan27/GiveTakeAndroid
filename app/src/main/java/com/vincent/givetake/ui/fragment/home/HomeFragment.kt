package com.vincent.givetake.ui.fragment.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.messaging.FirebaseMessaging
import com.vincent.givetake.data.source.request.FilterRequest
import com.vincent.givetake.data.source.request.UpdateTokenRequest
import com.vincent.givetake.databinding.FragmentHomeBinding
import com.vincent.givetake.factory.ItemUserPrefViewModelFactory
import com.vincent.givetake.preference.FilterData
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.activity.items.add.AddActivity
import com.vincent.givetake.ui.activity.detail.DataDetail
import com.vincent.givetake.ui.activity.detail.DetailActivity
import com.vincent.givetake.ui.fragment.filter.BottomSheetFragment
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Result

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var accessKey = ""
    private var userId = ""
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var viewModel: HomeViewModel
    private lateinit var filterData: FilterData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pref = UserPreferences.getInstance(requireActivity().dataStore)
        val factory = ItemUserPrefViewModelFactory.getInstance(pref)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]


        itemAdapter = ItemAdapter()
        viewModel.getAccessKey().observe(viewLifecycleOwner) {
            if (it != null) {
                accessKey = it
                getToken()
                binding.btnAddItemHome.visibility = View.VISIBLE
                viewModel.getFilter().observe(viewLifecycleOwner) { filter ->
                    if (filter != null) {
                        filterData = filter
                        if (filter.index == -1){
                            if (binding.edtSearch.text.toString().isNotEmpty()) {
                                viewModel.getAllItemsSearch(accessKey, binding.edtSearch.text.toString())
                            } else {
                                viewModel.getAllItemsLogin(accessKey)
                            }
                        }else {
                            val body = FilterRequest(
                                filter.category,
                                filter.radius.toInt()
                            )
                            viewModel.getAllItemsFilter(it, body)
                        }
                    }
                }
                itemAdapter.setRoleUser("user")
            }
        }

        viewModel.getUserId().observe(viewLifecycleOwner){
            userId = it
            itemAdapter.onItemClick = { data ->
                when (data.userId) {
                    userId -> {
                        val dataDetail = DataDetail(
                            "owner",
                            data.id,
                            null,
                            data.distance,
                            userId,
                            accessKey
                        )
                        val intent = Intent(requireActivity(), DetailActivity::class.java)
                        intent.putExtra("data", dataDetail)
                        startActivity(intent)
                    }
                    else -> {
                        val dataDetail = DataDetail(
                            "visit",
                            data.id,
                            null,
                            data.distance,
                            userId,
                            accessKey
                        )
                        val intent = Intent(requireActivity(), DetailActivity::class.java)
                        intent.putExtra("data", dataDetail)
                        startActivity(intent)
                    }
                }
            }
        }

        viewModel.resultLogin.observe(viewLifecycleOwner) {
            when(it) {
                is Result.Loading -> {
                    binding.pgHomeFragment.visibility = View.VISIBLE
                    binding.rvHomeFragment.visibility = View.GONE
                }
                is Result.Success -> {
                    if (it.data.data.size <= 0) {
                        binding.tvNoDataHome.visibility = View.VISIBLE
                    } else {
                        binding.tvNoDataHome.visibility = View.GONE
                    }
                    itemAdapter.setData(it.data.data)
                    binding.pgHomeFragment.visibility = View.GONE
                    binding.rvHomeFragment.visibility = View.VISIBLE

                }
                is Result.Error -> {
                    Toast.makeText(context, "An error occurred : ${it.errorMessage}", Toast.LENGTH_SHORT).show()
                    binding.pgHomeFragment.visibility = View.GONE
                    binding.rvHomeFragment.visibility = View.VISIBLE
                }
            }
        }

        viewModel.resultFilterLogin.observe(viewLifecycleOwner) {
            when(it) {
                is Result.Loading -> {
                    binding.pgHomeFragment.visibility = View.VISIBLE
                    binding.rvHomeFragment.visibility = View.GONE
                }
                is Result.Success -> {
                    if (it.data?.data?.size!! <= 0) {
                        binding.tvNoDataHome.visibility = View.VISIBLE
                    } else {
                        binding.tvNoDataHome.visibility = View.GONE
                    }
                    itemAdapter.setData(it.data.data)
                    binding.pgHomeFragment.visibility = View.GONE
                    binding.rvHomeFragment.visibility = View.VISIBLE

                }
                is Result.Error -> {
                    Toast.makeText(context, "An error occurred : ${it.errorMessage}", Toast.LENGTH_SHORT).show()
                    binding.pgHomeFragment.visibility = View.GONE
                    binding.rvHomeFragment.visibility = View.VISIBLE
                }
            }
        }

        viewModel.resultSearchItem.observe(viewLifecycleOwner) {
            when(it) {
                is Result.Loading -> {
                    binding.pgHomeFragment.visibility = View.VISIBLE
                    binding.rvHomeFragment.visibility = View.GONE
                }
                is Result.Success -> {
                    if (it.data?.data?.size!! <= 0) {
                        binding.tvNoDataHome.visibility = View.VISIBLE
                    } else {
                        binding.tvNoDataHome.visibility = View.GONE
                    }
                    itemAdapter.setData(it.data.data)
                    binding.pgHomeFragment.visibility = View.GONE
                    binding.rvHomeFragment.visibility = View.VISIBLE

                }
                is Result.Error -> {
                    Toast.makeText(context, "An error occurred : ${it.errorMessage}", Toast.LENGTH_SHORT).show()
                    binding.pgHomeFragment.visibility = View.GONE
                    binding.rvHomeFragment.visibility = View.VISIBLE
                }
            }
        }

        binding.rvHomeFragment.adapter = itemAdapter
        binding.rvHomeFragment.layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.rvHomeFragment.setHasFixedSize(true)

        binding.btnAddItemHome.setOnClickListener {
            val intent = Intent(requireActivity(), AddActivity::class.java)
            intent.putExtra(Constant.KEY_ACCESS_USER, accessKey)
            startActivity(intent)
        }

        binding.btnFilter.setOnClickListener {
            val frag = BottomSheetFragment(filterData)
            frag.show(requireActivity().supportFragmentManager, "Bottom Sheet")
        }

        binding.edtSearch.addTextChangedListener (object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                viewModel.getAllItemsSearch(accessKey, s.toString())
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFilter().observe(viewLifecycleOwner) { filter ->
            if (filter != null) {
                filterData = filter
                if (filter.index == -1){
                    if (binding.edtSearch.text.toString().isNotEmpty()) {
                        viewModel.getAllItemsSearch(accessKey, binding.edtSearch.text.toString())
                    } else {
                        viewModel.getAllItemsLogin(accessKey)
                    }
                }else {
                    val body = FilterRequest(
                        filter.category,
                        filter.radius.toInt()
                    )
                    viewModel.getAllItemsFilter(accessKey, body)
                }
            }
        }
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.d("DEBUGS", it)
            val body = UpdateTokenRequest(
                it
            )
            viewModel.updateToken(accessKey, body)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}