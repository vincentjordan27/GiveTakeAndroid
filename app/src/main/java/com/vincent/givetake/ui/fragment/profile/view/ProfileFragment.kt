package com.vincent.givetake.ui.fragment.profile.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.bumptech.glide.Glide
import com.vincent.givetake.data.source.request.UpdateTokenRequest
import com.vincent.givetake.data.source.response.users.UserData
import com.vincent.givetake.databinding.ProfileFragmentBinding
import com.vincent.givetake.factory.UsersPrefViewModelFactory
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.activity.advice.list.AdviceActivity
import com.vincent.givetake.ui.activity.login.LoginActivity
import com.vincent.givetake.ui.fragment.profile.edit.EditProfileActivity
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Result

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class ProfileFragment : Fragment() {

    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileViewModel
    private var needRefresh = false
    private var accessKey = ""
    private var dataUser: UserData? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (needRefresh) {
            needRefresh = false
            viewModel.getUserData(accessKey)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pref = UserPreferences.getInstance(requireActivity().dataStore)
        val factory = UsersPrefViewModelFactory.getInstance(pref)
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]
        showLoading(true)
        observeViewModel()
        binding.btnEditProfile.setOnClickListener {
            needRefresh = true
            val intent = Intent(requireActivity(), EditProfileActivity::class.java)
            intent.putExtra(EditProfileActivity.PROFILE_DATA, dataUser)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            val body = UpdateTokenRequest(
                ""
            )
            viewModel.logout(accessKey, body)
        }

        binding.txtComplaintProfile.setOnClickListener {
            val intent = Intent(requireActivity(), AdviceActivity::class.java)
            intent.putExtra(Constant.KEY_ACCESS_USER, accessKey)
            startActivity(intent)
        }

    }


    private fun observeViewModel() {
        viewModel.getAccessKey().observe(viewLifecycleOwner) {
            if (it != null && it != "") {
                accessKey = it
                showLoading(false)
                viewModel.getUserData(accessKey)
            }
        }

        viewModel.resultUserData.observe(viewLifecycleOwner) {
            when(it) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    if (it.data != null) {
                        dataUser = it.data.data
                        dataUser!!.accessKey = accessKey
                        with(it.data.data) {
                            binding.txtNameProfile.text = name
                            binding.txtEmailProfile.text = email
                            binding.txtPhoneProfile.text = phone
                            binding.txtPointProfile.text = "$point pts"
                            Glide.with(requireActivity())
                                .load(photo)
                                .into(binding.imgProfile)
                        }
                    }
                }
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(context, "Terjadi error : ${it.errorMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.updateToken.observe(viewLifecycleOwner) {
            when(it) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    val intent = Intent(requireActivity(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    activity?.startActivity(intent)
                    activity?.finish()
                }
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(context, it.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pgProfile.visibility = View.VISIBLE
            binding.pgBgProfile.visibility = View.VISIBLE
        } else {
            binding.pgProfile.visibility = View.GONE
            binding.pgBgProfile.visibility = View.GONE
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}