package com.vincent.givetake.ui.activity.reward.redeem

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.vincent.givetake.R
import com.vincent.givetake.data.source.request.ChooseReceiverRequest
import com.vincent.givetake.data.source.request.RedeemRequest
import com.vincent.givetake.data.source.response.rewards.RewardDetail
import com.vincent.givetake.data.source.response.rewards.RewardItem
import com.vincent.givetake.databinding.ActivityRedeemRewardBinding
import com.vincent.givetake.factory.RewardsPrefFactory
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.fragment.rewards.RewardsViewModel
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Result

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class RedeemRewardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRedeemRewardBinding
    private lateinit var viewModel: RewardsViewModel
    private var accessKey = ""
    private var rewardDetail: RewardItem? = null
    private var redeem = false
    private var point = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRedeemRewardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showLoading(true)
        rewardDetail = intent.getParcelableExtra(Constant.REWARD_DETAIL)
        point = intent.getIntExtra(Constant.USER_POINT, 0)
        if (rewardDetail == null) {
            Toast.makeText(this, "Terjadi kesalahan. Silahkan coba lagi", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            setData(rewardDetail!!)
        }

        val pref = UserPreferences.getInstance(dataStore)
        val factory = RewardsPrefFactory.getInstance(pref)

        viewModel = ViewModelProvider(this, factory)[RewardsViewModel::class.java]

        setObserver()
        setListener()
    }

    private fun setData(data: RewardItem) {
        Glide.with(this)
            .load(data.photo)
            .placeholder(R.drawable.ic_load)
            .into(binding.img)
        binding.txtName.text = data.name
        binding.txtDesc.text = data.desc
    }

    private fun setListener() {
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.btnRedeem.setOnClickListener {
            if (binding.edtInfo.text.toString().isEmpty()) {
                binding.edtInfo.error = "Keterangan harus diisi"
            } else {

                val alertDialog = AlertDialog.Builder(this, R.style.CostumDialog)
                    .setMessage("Konfirmasi penukaran hadiah ?")
                    .setCancelable(false)
                    .setPositiveButton("Ya"){_ , _ ->
                        redeem = true
                    }
                    .setNegativeButton("Tidak"){dialog, _ ->
                        dialog.dismiss()
                    }
                    .setOnDismissListener {
                        if (redeem) {
                            val point = point - rewardDetail?.price!!
                            val body = RedeemRequest(
                                binding.edtInfo.text.toString(),
                                point
                            )
                            viewModel.redeemReward(accessKey, rewardDetail?.id!!, body)
                        }
                    }
                alertDialog.show()
            }
        }
    }

    private fun setObserver() {
        viewModel.getAccessKey().observe(this) {
            if (it != null && it != "") {
                accessKey = it
                showLoading(false)
            }
        }
        viewModel.resultRedeemReward.observe(this) {
            when (it) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    val intent = Intent()
                    intent.putExtra(Constant.IS_REDEEM_REWARD, true)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                is Result.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.txtSnackbar, it.errorMessage, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        if(isLoading) {
            binding.pg.visibility = View.VISIBLE
            binding.btnRedeem.isEnabled = false
        } else {
            binding.pg.visibility = View.GONE
            binding.btnRedeem.isEnabled = true
        }
    }
}