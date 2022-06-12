package com.vincent.givetake.ui.activity.resetphone

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.vincent.givetake.R
import com.vincent.givetake.data.source.request.LoginRequest
import com.vincent.givetake.data.source.request.ResetPhoneRequest
import com.vincent.givetake.data.source.request.UpdatePhoneRequest
import com.vincent.givetake.databinding.ActivityResetPhoneBinding
import com.vincent.givetake.factory.EmailRepositoryViewModelFactory
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.activity.home.MainActivity
import com.vincent.givetake.ui.activity.login.LoginActivity
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Result
import java.util.*
import kotlin.random.Random
import kotlin.random.nextInt

class ResetPhoneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPhoneBinding
    private lateinit var viewModel: ResetPhoneViewModel
    private var accessKey: String = ""
    private var key: String = ""
    private var pg: ProgressBar? = null
    private var editText: TextInputEditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        accessKey = intent.getStringExtra(Constant.KEY_ACCESS_USER) ?: ""

        val factory = EmailRepositoryViewModelFactory.getInstance()
        viewModel = ViewModelProvider(this, factory)[ResetPhoneViewModel::class.java]
        init()
        setListener()
        setObserver()
    }

    private fun init() {
        key = Random(System.currentTimeMillis()).nextInt(1000..9999).toString()
        val body = ResetPhoneRequest(
            key
        )
        viewModel.sendResetOtp(accessKey, body)
    }

    private fun setObserver() {
        viewModel.phoneResetOtp.observe(this) {
            when (it) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    Snackbar.make(binding.txtSnackbar, "Berhasil mengirim OTP ke email", Snackbar.LENGTH_LONG).show()
                }
                is Result.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.txtSnackbar, it.errorMessage, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        viewModel.updatePhoneResult.observe(this) {
            when (it) {
                is Result.Loading -> {
                    pg?.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    pg?.visibility = View.GONE
                    Toast.makeText(this, "Berhasil memperbarui nomor telepon. Silahkan masuk kembali", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                is Result.Error -> {
                    pg?.visibility = View.GONE
                    editText?.error = it.errorMessage
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pg.visibility = View.VISIBLE
        }else {
            binding.pg.visibility = View.GONE
        }
    }

    private fun setListener() {
        binding.btnVerify.setOnClickListener {
            val inputUser = binding.otp1.text.toString() + binding.otp2.text.toString() + binding.otp3.text.toString() + binding.otp4.text.toString()
            if (inputUser != key) {
                Snackbar.make(binding.txtSnackbar, "OTP tidak sesuai", Snackbar.LENGTH_LONG).show()
            }else {
                val dialog = Dialog(this)
                dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
                dialog.setContentView(R.layout.dialog_reset_phone)

                val inputPhone = dialog.findViewById<TextInputEditText>(R.id.edt_phone)
                val btnSave = dialog.findViewById<Button>(R.id.btn_save)
                editText = inputPhone
                pg = dialog.findViewById(R.id.pg)

                btnSave.setOnClickListener {
                    if (inputPhone.text?.trim().toString().isEmpty()) {
                        inputPhone.error = "Nomor telepon tidak boleh kosong"
                    } else if (inputPhone.text?.trim().toString().length > 15) {
                        inputPhone.error = "Nomor telepon tidak boleh lebih dari 15"
                    } else {
                        val updatePhone = UpdatePhoneRequest(
                            inputPhone.text?.trim().toString()
                        )
                        viewModel.updatePhone(accessKey, updatePhone)
                    }
                }
                dialog.setOnDismissListener {
                    finish()
                }
                dialog.show()
            }
        }

        binding.tvResend.setOnClickListener {
            key = Random(System.currentTimeMillis()).nextInt(1000..9999).toString()
            val body = ResetPhoneRequest(
                key
            )
            viewModel.sendResetOtp(accessKey, body)
        }
    }


}