package com.vincent.givetake.ui.activity.resetpass

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.vincent.givetake.R
import com.vincent.givetake.data.source.request.ResetRequest
import com.vincent.givetake.data.source.request.UpdatePassRequest
import com.vincent.givetake.data.source.request.UpdatePhoneRequest
import com.vincent.givetake.databinding.ActivityUpdatePassBinding
import com.vincent.givetake.factory.UserEmailRepoViewModelFactory
import com.vincent.givetake.ui.activity.login.LoginActivity
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Result
import kotlin.random.Random
import kotlin.random.nextInt

class UpdatePassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdatePassBinding
    private lateinit var viewModel: ResetPassViewModel
    private var key : String = ""
    private var username: String = ""
    private var pg: ProgressBar? = null
    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = UserEmailRepoViewModelFactory.getInstance()
        viewModel = ViewModelProvider(this, factory)[ResetPassViewModel::class.java]

        key = intent.getStringExtra(Constant.UPDATE_PASS_KEY) ?: ""
        username = intent.getStringExtra(Constant.UPDATE_PASS_USERNAME) ?: ""

        setListener()
        setObserver()
    }

    private fun setListener() {
        binding.tvResend.setOnClickListener {
            key = Random(System.currentTimeMillis()).nextInt(1000..9999).toString()
            val body = ResetRequest(
                key
            )
            viewModel.resetPass(username, body)
        }

        binding.btnVerify.setOnClickListener {
            val inputUser = binding.otp1.text.toString() + binding.otp2.text.toString() + binding.otp3.text.toString() + binding.otp4.text.toString()
            if (inputUser != key) {
                Snackbar.make(binding.txtSnackbar, "OTP tidak sesuai", Snackbar.LENGTH_LONG).show()
            } else {
                dialog = Dialog(this)
                dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
                dialog?.setContentView(R.layout.dialog_reset_pass)

                val inputPass = dialog?.findViewById<TextInputEditText>(R.id.edt_pass)
                val btnSave = dialog?.findViewById<Button>(R.id.btn_save)
                pg = dialog?.findViewById(R.id.pg)

                btnSave?.setOnClickListener {
                    if (inputPass?.text?.trim().toString().isEmpty()) {
                        inputPass?.error = "Password tidak boleh kosong"
                    } else {
                        val updatePass = UpdatePassRequest(
                            inputPass?.text?.trim().toString()
                        )
                        viewModel.updatePass(username, updatePass)
                    }
                }
                dialog?.show()
            }
        }
    }


    private fun setObserver() {
        viewModel.updatePassResult.observe(this) {
            when(it) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Berhasil memperbarui password. Silahkan masuk kembali", Toast.LENGTH_SHORT).show()
                    dialog?.dismiss()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                is Result.Error -> {
                    showLoading(true)
                    Toast.makeText(this, it.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            pg?.visibility = View.VISIBLE
        } else {
            pg?.visibility = View.GONE
        }
    }
}