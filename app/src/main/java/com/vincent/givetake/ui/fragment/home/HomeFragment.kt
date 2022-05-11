package com.vincent.givetake.ui.fragment.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.vincent.givetake.databinding.FragmentHomeBinding
import com.vincent.givetake.factory.ItemsPrefViewModelFactory
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.activity.add.AddActivity
import com.vincent.givetake.ui.activity.detail.DataDetail
import com.vincent.givetake.ui.activity.detail.DetailActivity
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Result

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user" +
        "")

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var accessKey = ""
    private var userId = ""
    private lateinit var itemAdapter: ItemAdapter
    private var isDelete = false

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
        val factory = ItemsPrefViewModelFactory.getInstance(pref)
        val viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    isDelete = data.getBooleanExtra("deleted", false)
                    if (accessKey != "" && isDelete) {
                        binding.btnAddItemHome.visibility = View.VISIBLE
                        viewModel.getAllItemsLogin(accessKey)
                        itemAdapter.setRoleUser("user")
                    }
                    isDelete = false
                }
            }
        }

        itemAdapter = ItemAdapter()
        viewModel.getAccessKey().observe(viewLifecycleOwner) {
            if (it != null) {
                accessKey = it
                binding.btnAddItemHome.visibility = View.VISIBLE
                viewModel.getAllItemsLogin(it)
                itemAdapter.setRoleUser("user")
            }
        }

        viewModel.getUserId().observe(viewLifecycleOwner){
            userId = it
            itemAdapter.onItemClick = { data ->
                Log.d("DEBUGS", "$userId ${data.userId}")
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
                        resultLauncher.launch(intent)
            //                        startActivity(intent)
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
                        resultLauncher.launch(intent)
            //                        startActivity(intent)
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
                    itemAdapter.setData(it.data.data)
                    binding.pgHomeFragment.visibility = View.GONE
                    binding.rvHomeFragment.visibility = View.VISIBLE
                }
                is Result.Error -> {
                    Toast.makeText(context, "An error occured : ${it.errorMessage}", Toast.LENGTH_SHORT).show()
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
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}