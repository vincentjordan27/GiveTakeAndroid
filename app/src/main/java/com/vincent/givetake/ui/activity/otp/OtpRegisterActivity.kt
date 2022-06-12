package com.vincent.givetake.ui.activity.otp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.vincent.givetake.R
import com.vincent.givetake.data.source.request.LoginRequest
import com.vincent.givetake.data.source.request.OtpRequest
import com.vincent.givetake.databinding.ActivityOtpRegisterBinding
import com.vincent.givetake.factory.OtpPrefViewModelFactory
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.activity.home.MainActivity
import com.vincent.givetake.ui.activity.login.LoginActivity
import com.vincent.givetake.ui.activity.resetphone.ResetPhoneActivity
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Result
import kotlin.random.Random
import kotlin.random.nextInt

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class OtpRegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpRegisterBinding
    private var key = ""
    private var phoneNumber = ""
    private var inputUser = ""
    private var username = ""
    private var pass = ""
    private lateinit var viewModel: OtpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phoneNumber = intent.getStringExtra(Constant.KEY_PHONE) ?: ""
        username = intent.getStringExtra(Constant.KEY_USERNAME) ?: ""
        pass = intent.getStringExtra(Constant.KEY_PASSWORD) ?: ""
        val pref = UserPreferences.getInstance(dataStore)
        val factory = OtpPrefViewModelFactory.getInstance(pref)
        viewModel = ViewModelProvider(this, factory)[OtpViewModel::class.java]

        binding.tvPhone.text = phoneNumber
        init()
        setListener()
        setObserver()
    }

    private fun setObserver() {
        viewModel.otpSendResult.observe(this) {
            when(it) {
                is Result.Loading -> {
                    binding.pg.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.pg.visibility = View.GONE
                    Snackbar.make(binding.txtError, "OTP berhasil dikirim", Snackbar.LENGTH_LONG).show()
                }
                is Result.Error -> {
                    binding.pg.visibility = View.GONE
                    Snackbar.make(binding.txtError, it.errorMessage, Snackbar.LENGTH_LONG).show()
                }
            }
        }
        viewModel.loginResult.observe(this) {
            when(it) {
                is Result.Loading -> {
                    binding.pg.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.pg.visibility = View.GONE
                    viewModel.saveUserAccessKey("Bearer ${it.data?.data!!.accessToken}")
                    viewModel.saveUserId(it.data.data.userId)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                is Result.Error -> {
                    binding.pg.visibility = View.GONE
                    Snackbar.make(binding.txtError, it.errorMessage, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun init() {
        key = Random(System.currentTimeMillis()).nextInt(1000..9999).toString()
        val body = OtpRequest(
            phoneNumber,
            "otp",
            getString(R.string.otp_text, key)
        )
        viewModel.sendOtp(body)
    }

    private fun setListener() {
        binding.btnVerify.setOnClickListener {
            inputUser += binding.otp1.text.toString() + binding.otp2.text.toString() + binding.otp3.text.toString() + binding.otp4.text.toString()
            if (inputUser != key) {
                Snackbar.make(binding.txtError, "OTP tidak sesuai", Snackbar.LENGTH_LONG).show()
            }else {
                val loginRequest = LoginRequest(
                    username,
                    pass
                )
                viewModel.loginUser(loginRequest)
            }
        }
        binding.tvResend.setOnClickListener {
            key = (1000..9999).random().toString()
            val body = OtpRequest(
                phoneNumber,
                "otp",
                getString(R.string.otp_text, key)
            )
            viewModel.sendOtp(body)
        }

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}