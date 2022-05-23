package com.vincent.givetake.ui.activity.receiver.list

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vincent.givetake.databinding.ActivityListReceiverBinding
import com.vincent.givetake.factory.ItemsRepositoryViewModelFactory
import com.vincent.givetake.ui.activity.receiver.ReceiverViewModel
import com.vincent.givetake.ui.activity.receiver.choose.ChooseUserActivity
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Result

class ListReceiverActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListReceiverBinding
    private lateinit var viewModel: ReceiverViewModel
    private var token: String = ""
    private var itemId: String = ""
    private lateinit var adapterReceiver: ListReceiverAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListReceiverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ItemsRepositoryViewModelFactory.getInstance()
        viewModel = ViewModelProvider(this, factory)[ReceiverViewModel::class.java]
        token = intent.getStringExtra(Constant.LIST_RECEIVER_ACCESS)?: ""
        itemId = intent.getStringExtra(Constant.LIST_RECEIVER_ITEM_ID)?: ""
        adapterReceiver = ListReceiverAdapter {
            val intent = Intent(this@ListReceiverActivity, ChooseUserActivity::class.java)
            intent.putExtra(Constant.CHOOSE_RECEIVER_NAME, it.name)
            intent.putExtra(Constant.CHOOSE_RECEIVER_ACCESS, token)
            intent.putExtra(Constant.CHOOSE_RECEIVER_REASON, it.reason)
            intent.putExtra(Constant.CHOOSE_RECEIVER_ITEM_ID, itemId)
            intent.putExtra(Constant.CHOOSE_RECEIVER_REQUEST_ID, it.requestId)
            resultLauncher.launch(intent)
        }
        adapterReceiver.token = token
        binding.rv.apply {
            adapter = adapterReceiver
            layoutManager = LinearLayoutManager(this@ListReceiverActivity)
            setHasFixedSize(true)
        }

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        setObserver()

        viewModel.getReceiverList(token, itemId)
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

    private fun setObserver() {
        viewModel.receiverResult.observe(this) {
            when (it) {
                is Result.Loading -> {
                    showError("", false)
                    showLoading(true)
                }
                is Result.Success -> {
                    showError("", false)
                    showLoading(false)
                    if (it.data?.data?.isEmpty() == true) {
                        showNoData(true)
                    }else {
                        showNoData(false)
                        adapterReceiver.setData(it.data?.data!!)
                    }
                }
                is Result.Error -> {
                    showNoData(true)
                    showError(it.errorMessage, true)
                }
            }
        }
    }

    private fun showError(message: String, isError: Boolean) {
        if (isError) {
            binding.tvError.text = message
            binding.tvError.visibility = View.VISIBLE
        } else {
            binding.tvError.visibility = View.GONE
        }
    }

    private fun showNoData(isNoData: Boolean) {
        if (isNoData) {
            binding.tvNoData.visibility = View.VISIBLE
            binding.rv.visibility = View.GONE
        }else {
            binding.tvNoData.visibility = View.GONE
            binding.rv.visibility = View.VISIBLE
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pg.visibility = View.VISIBLE
        } else {
            binding.pg.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}