package com.vincent.givetake.ui.activity.receiver.choose

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.vincent.givetake.R
import com.vincent.givetake.data.source.request.ChooseReceiverRequest
import com.vincent.givetake.databinding.ActivityChooseUserBinding
import com.vincent.givetake.factory.ItemsRepositoryViewModelFactory
import com.vincent.givetake.ui.activity.receiver.ReceiverViewModel
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Result

class ChooseUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseUserBinding
    private var token: String = ""
    private var itemId: String = ""
    private var reason: String = ""
    private var requestId: String = ""
    private var name: String = ""
    private var choose: Boolean = false
    private lateinit var viewModel: ReceiverViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = intent.getStringExtra(Constant.CHOOSE_RECEIVER_ACCESS) ?: ""
        itemId = intent.getStringExtra(Constant.CHOOSE_RECEIVER_ITEM_ID) ?: ""
        reason = intent.getStringExtra(Constant.CHOOSE_RECEIVER_REASON) ?: ""
        requestId = intent.getStringExtra(Constant.CHOOSE_RECEIVER_REQUEST_ID) ?: ""
        name = intent.getStringExtra(Constant.CHOOSE_RECEIVER_NAME) ?: ""

        val factory = ItemsRepositoryViewModelFactory.getInstance()
        viewModel = ViewModelProvider(this, factory)[ReceiverViewModel::class.java]

        binding.btnChoose.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this, R.style.CostumDialog)
                .setTitle("Konfirmasi")
                .setMessage("Anda yakin ingin memilih ${name} sebagai penerima ?")
                .setCancelable(false)
                .setPositiveButton("Ya"){_ , _ ->
                    choose = true
                }
                .setNegativeButton("Tidak"){dialog, _ ->
                    dialog.dismiss()
                }
                .setOnDismissListener {
                    if (choose) {
                        val body = ChooseReceiverRequest(
                            requestId
                        )
                        viewModel.chooseReceiver(token, itemId, body)
                    }
                }
            alertDialog.show()
        }

        binding.btnCancel.setOnClickListener {
            onBackPressed()
        }

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        setUpView()
        setObserver()
    }

    private fun setObserver() {
        viewModel.chooseReceiverResult.observe(this) {
            when(it) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    val intent = Intent()
                    intent.putExtra(Constant.IS_CHOOSE_RECEIVER, true)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                is Result.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.txtError, it.errorMessage, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun setUpView() {
        binding.apply {
            tvName.text = name
            txtReason.text = reason
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pg.visibility = View.VISIBLE
        } else {
            binding.pg.visibility = View.GONE
        }
    }

}