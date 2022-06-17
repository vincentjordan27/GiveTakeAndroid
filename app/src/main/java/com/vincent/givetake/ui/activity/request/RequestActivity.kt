package com.vincent.givetake.ui.activity.request

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.vincent.givetake.R
import com.vincent.givetake.data.source.ApiClient
import com.vincent.givetake.data.source.request.ItemRequestBody
import com.vincent.givetake.data.source.response.fcm.FcmResponse
import com.vincent.givetake.databinding.ActivityRequestBinding
import com.vincent.givetake.factory.ItemsRepositoryViewModelFactory
import com.vincent.givetake.firebase.DataNotification
import com.vincent.givetake.firebase.NotificationData
import com.vincent.givetake.ui.activity.home.MainActivity
import com.vincent.givetake.ui.activity.login.LoginActivity
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Result
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestBinding
    private lateinit var viewModel: RequestViewModel
    private var token = ""
    private var itemId = ""
    private var itemName = ""
    private var tokenNotification = ""
    private var myToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ItemsRepositoryViewModelFactory.getInstance()
        viewModel = ViewModelProvider(this, factory)[RequestViewModel::class.java]
        token = intent.getStringExtra(Constant.REQUEST_ACCESS) ?: ""
        itemId = intent.getStringExtra(Constant.REQUEST_ITEM_ID) ?: ""
        tokenNotification = intent.getStringExtra(Constant.REQUEST_TOKEN_NOTIF) ?: ""
        itemName = intent.getStringExtra(Constant.KEY_ITEM_NAME) ?: ""
        myToken = intent.getStringExtra(Constant.MY_TOKEN) ?: ""

        binding.btnRequest.setOnClickListener {
            if (binding.edtRequest.text.isNullOrEmpty()) {
                binding.edtRequest.error = "Alasan tidak boleh kosong"
            } else {
                val body = ItemRequestBody(
                    binding.edtRequest.text.toString()
                )
                viewModel.requestItem(token, itemId, body)
            }
        }

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        setObserver()

    }

    private fun setObserver() {
        viewModel.requestItemResult.observe(this) {
            when (it) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    val alertDialog = AlertDialog.Builder(this, R.style.CostumDialog)
                        .setTitle("Berhasil")
                        .setMessage("Berhasil mengajukan permintaan sebagai penerima")
                        .setCancelable(false)
                        .setPositiveButton("Ok"){_, _ -> }
                        .setOnDismissListener {
                            sendNotification()
                        }
                    alertDialog.show()
                }
                is Result.Error -> {
                    showLoading(false)
                    showSnackBar(it.errorMessage)
                }
            }
        }
    }


    private fun sendNotification() {
        val token = listOf(tokenNotification)
        val body = NotificationData(
            DataNotification(
                Constant.FCM_TYPE_ITEM,
                "",
                itemName
            ),
            token
        )

        ApiClient.getNotificationService().sendMessage(
            Constant.getterRemoteMsgHeaders(),
            body
        ).enqueue(object: Callback<FcmResponse> {
            override fun onResponse(call: Call<FcmResponse>, response: Response<FcmResponse>) {
                finish()
            }

            override fun onFailure(call: Call<FcmResponse>, t: Throwable) {
                Toast.makeText(this@RequestActivity, t.localizedMessage, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pg.visibility = View.VISIBLE
        } else {
            binding.pg.visibility = View.GONE
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.txtError, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}