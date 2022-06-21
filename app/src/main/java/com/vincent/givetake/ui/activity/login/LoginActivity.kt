package com.vincent.givetake.ui.activity.login

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.vincent.givetake.R
import com.vincent.givetake.data.source.request.LoginRequest
import com.vincent.givetake.databinding.ActivityLoginBinding
import com.vincent.givetake.factory.UsersPrefViewModelFactory
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.activity.home.MainActivity
import com.vincent.givetake.ui.activity.otp.OtpLoginActivity
import com.vincent.givetake.ui.activity.register.RegisterActivity
import com.vincent.givetake.ui.activity.resetpass.ResetPasswordActivity
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Result
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class LoginActivity : AppCompatActivity() {

    private lateinit var loginBinding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        val pref = UserPreferences.getInstance(dataStore)
        val factory = UsersPrefViewModelFactory.getInstance(pref)
        val viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        loginBinding.btnLogin.setOnClickListener {
            if (loginBinding.edtUsernameLogin.text?.isEmpty() == true) {
                loginBinding.edtUsernameLogin.error = "Username harus diisi"
            }
            if (loginBinding.edtPasswordLogin.text?.isEmpty() == true) {
                loginBinding.edtPasswordLogin.error = "Password harus diisi"
            }
            if (loginBinding.edtPasswordLogin.text?.isNotEmpty() == true && loginBinding.edtUsernameLogin.text?.isNotEmpty() == true) {
                val loginRequest = LoginRequest(
                    loginBinding.edtUsernameLogin.text.toString(),
                    loginBinding.edtPasswordLogin.text.toString()
                )
                viewModel.loginUser(loginRequest)
                viewModel.result.observe(this) {
                    when(it) {
                        is Result.Loading -> {
                            loginBinding.pgLogin.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            loginBinding.pgLogin.visibility = View.GONE
                            if (it.data?.status != "success") {
                                Toast.makeText(this, "An error occurred : ${it.data?.message}", Toast.LENGTH_SHORT).show()
                            }else {
                                val intent = Intent(this, OtpLoginActivity::class.java)
                                intent.putExtra(Constant.KEY_ACCESS_USER, it.data.data!!.accessToken)
                                intent.putExtra(Constant.KEY_USER_ID, it.data.data.userId)
                                intent.putExtra(Constant.KEY_PHONE, it.data.data.phone)
                                startActivity(intent)
                            }
                        }
                        is Result.Error -> {
                            loginBinding.pgLogin.visibility = View.GONE
                            Snackbar.make(txt_error_login, "Terjadi Error: ${it.errorMessage}", Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        loginBinding.txtToRegister.setOnClickListener {
            Intent(this@LoginActivity, RegisterActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        loginBinding.txtForgetPassLogin.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}