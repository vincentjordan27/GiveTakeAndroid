package com.vincent.givetake.ui.activity.request

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.vincent.givetake.R
import com.vincent.givetake.data.source.request.ItemRequestBody
import com.vincent.givetake.databinding.ActivityRequestBinding
import com.vincent.givetake.factory.ItemsRepositoryViewModelFactory
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Result

class RequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestBinding
    private lateinit var viewModel: RequestViewModel
    private var token = ""
    private var itemId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ItemsRepositoryViewModelFactory.getInstance()
        viewModel = ViewModelProvider(this, factory)[RequestViewModel::class.java]
        token = intent.getStringExtra(Constant.REQUEST_ACCESS) ?: ""
        itemId = intent.getStringExtra(Constant.REQUEST_ITEM_ID) ?: ""

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
                            finish()
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