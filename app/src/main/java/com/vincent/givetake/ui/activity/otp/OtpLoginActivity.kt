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
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.vincent.givetake.R
import com.vincent.givetake.data.source.request.OtpRequest
import com.vincent.givetake.databinding.ActivityOtpLoginBinding
import com.vincent.givetake.factory.OtpPrefViewModelFactory
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.activity.home.MainActivity
import com.vincent.givetake.ui.activity.resetphone.ResetPhoneActivity
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.random.nextInt

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class OtpLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpLoginBinding
    private var accessKey: String = ""
    private var userId: String = ""
    private var phoneNumber: String = ""
    private var key: String = ""
    private lateinit var viewModel: OtpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreferences.getInstance(dataStore)
        val factory = OtpPrefViewModelFactory.getInstance(pref)
        viewModel = ViewModelProvider(this, factory)[OtpViewModel::class.java]
        accessKey = intent.getStringExtra(Constant.KEY_ACCESS_USER) ?: ""
        userId = intent.getStringExtra(Constant.KEY_USER_ID) ?: ""
        phoneNumber = intent.getStringExtra(Constant.KEY_PHONE) ?: ""

        binding.tvPhone.text = phoneNumber

        init()
        setListener()
        setObserver()
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
            val inputUser = binding.otp1.text.toString() + binding.otp2.text.toString() + binding.otp3.text.toString() + binding.otp4.text.toString()
            if (inputUser == key) {
                binding.pg.visibility = View.VISIBLE
                lifecycleScope.launch {
                    viewModel.saveUserAccessKey("Bearer $accessKey")
                    viewModel.saveUserId(userId)
                    delay(2L)
                    binding.pg.visibility = View.VISIBLE
                    val intent = Intent(this@OtpLoginActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            } else {
                Snackbar.make(binding.txtError, "OTP tidak sesuai", Snackbar.LENGTH_LONG).show()
            }
        }

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.tvReset.setOnClickListener {
            val intent = Intent(this, ResetPhoneActivity::class.java)
            intent.putExtra(Constant.KEY_ACCESS_USER, "Bearer $accessKey")
            startActivity(intent)
        }
        binding.tvResend.setOnClickListener {
            key = Random(System.currentTimeMillis()).nextInt(1000..9999).toString()
            val body = OtpRequest(
                phoneNumber,
                "otp",
                getString(R.string.otp_text, key)
            )
            viewModel.sendOtp(body)
        }
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
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}