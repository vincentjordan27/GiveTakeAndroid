package com.vincent.givetake.ui.activity.reward.detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.vincent.givetake.R
import com.vincent.givetake.data.source.response.rewards.RewardDetail
import com.vincent.givetake.databinding.ActivityDetailRewardBinding
import com.vincent.givetake.factory.RewardsPrefFactory
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.activity.reward.redeem.RedeemRewardActivity
import com.vincent.givetake.ui.fragment.rewards.RewardsViewModel
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Result

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class DetailRewardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailRewardBinding
    private lateinit var viewModel: RewardsViewModel
    private var rewardId = ""
    private var accessKey = ""
    private var rewardDetail : RewardDetail? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRewardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rewardId = intent.getStringExtra(Constant.REWARD_ID) ?: ""

        val pref = UserPreferences.getInstance(dataStore)
        val factory = RewardsPrefFactory.getInstance(pref)
        viewModel = ViewModelProvider(this, factory)[RewardsViewModel::class.java]

        setListener()
        setObserver()

    }

    private fun setListener() {
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.btnRedeem.setOnClickListener {
            if (rewardDetail?.data?.price!! > rewardDetail?.point!!) {
                Snackbar.make(binding.txtSnackbar, "Poin anda tidak mencukupi", Snackbar.LENGTH_LONG).show()
            } else {
                Log.d("DEBUGS", "${rewardDetail == null}")
                val intent = Intent(this, RedeemRewardActivity::class.java)
                intent.putExtra(Constant.REWARD_DETAIL, rewardDetail!!.data)
                intent.putExtra(Constant.USER_POINT, rewardDetail!!.point)
                resultLauncher.launch(intent)
            }
        }

    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                if (data.getBooleanExtra(Constant.IS_CHOOSE_RECEIVER, false)) {
                    finish()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun setObserver() {
        viewModel.getAccessKey().observe(this) {
            if (it != null && it != "") {
                accessKey = it
                viewModel.getRewardById(accessKey, rewardId)
            }
        }
        viewModel.resultDetailReward.observe(this) {
            when(it) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    setData(it.data!!)
                    rewardDetail = it.data
                }
                is Result.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.txtSnackbar, it.errorMessage, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setData(data: RewardDetail) {
        binding.txtName.text = data.data.name
        binding.txtDesc.text = data.data.desc
        binding.txtPoint.text = data.data.price.toString()
        binding.txtStock.text = data.data.stock.toString()
        Glide.with(this)
            .load(data.data.photo)
            .placeholder(R.drawable.ic_load)
            .into(binding.img)
        Log.d("DEBUSG DETAIL" ,data.toString())
        if (data.valid) {
            binding.btnRedeem.isEnabled = false
            binding.btnRedeem.text = "Menunggu"
        } else if (data.data.stock <= 0) {
            binding.btnRedeem.isEnabled = false
            binding.btnRedeem.text = "Stok Habis"
        } else {
            binding.btnRedeem.isEnabled = true
            binding.btnRedeem.text = "Tukar"
        }


    }

    override fun onResume() {
        super.onResume()
        if (accessKey != "") {
            viewModel.getRewardById(accessKey, rewardId)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pg.visibility = View.VISIBLE
            binding.btnRedeem.isEnabled = false
        } else {
            binding.pg.visibility = View.GONE
            binding.btnRedeem.isEnabled = true
        }
    }
}