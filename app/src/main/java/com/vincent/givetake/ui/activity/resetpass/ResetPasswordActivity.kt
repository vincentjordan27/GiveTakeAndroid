package com.vincent.givetake.ui.activity.resetpass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.vincent.givetake.R
import com.vincent.givetake.data.source.request.ResetRequest
import com.vincent.givetake.databinding.ActivityResetPasswordBinding
import com.vincent.givetake.factory.UserEmailRepoViewModelFactory
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Result
import kotlin.random.Random
import kotlin.random.nextInt

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var viewModel: ResetPassViewModel
    private var key = ""
    private var username = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = UserEmailRepoViewModelFactory.getInstance()
        viewModel = ViewModelProvider(this, factory)[ResetPassViewModel::class.java]

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.btnSend.setOnClickListener {
            when {
                binding.edtUsername.text.toString().isEmpty() -> {
                    binding.edtUsername.error = "Kata sandi baru harus diisi"
                }
                binding.edtUsername.text.toString().isNotEmpty() -> {
                    key = Random(System.currentTimeMillis()).nextInt(1000..9999).toString()
                    username = binding.edtUsername.text.toString()
                    val body = ResetRequest(
                        key
                    )
                    viewModel.resetPass(binding.edtUsername.text.toString(), body)
                }
            }
        }
        setObserver()
    }

    private fun setObserver() {
        viewModel.resetPassResult.observe(this) {
            when(it) {
                is Result.Loading -> {
                    showLoading(true)
                    binding.btnSend.isEnabled = false
                }
                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Berhasil mengirim OTP ke email", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, UpdatePassActivity::class.java)
                    intent.putExtra(Constant.UPDATE_PASS_KEY, key)
                    intent.putExtra(Constant.UPDATE_PASS_USERNAME, username)
                    startActivity(intent)
                    binding.btnSend.isEnabled = true

                }
                is Result.Error -> {
                    showLoading(false)
                    binding.btnSend.isEnabled = true
                    Snackbar.make(binding.txtSnackbar, it.errorMessage, Snackbar.LENGTH_LONG).show()
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}