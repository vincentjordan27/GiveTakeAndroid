package com.vincent.givetake.ui.activity.register

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.vincent.givetake.databinding.ActivityRegisterBinding
import com.vincent.givetake.ui.activity.map.AddressResult
import com.vincent.givetake.ui.activity.map.MapsActivity
import com.vincent.givetake.data.source.request.RegisterRequest
import com.vincent.givetake.ui.activity.login.LoginActivity
import com.vincent.givetake.utils.Result
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.delay

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerBinding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = registerBinding.root
        setContentView(view)

        val factory = RegisterViewModelFactory.getInstance()
        val viewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]

        var dataAddress: AddressResult? = null

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    dataAddress = data.getParcelableExtra("data")
                    registerBinding.edtAddressRegister.setText(dataAddress?.address)
                }
            }
        }
        val intent = Intent(this@RegisterActivity, MapsActivity::class.java)

        registerBinding.btnAddressRegister.setOnClickListener {
            resultLauncher.launch(intent)
        }

        registerBinding.btnRegister.setOnClickListener {
            if (registerBinding.edtNameRegister.text?.isEmpty() == true) {
                registerBinding.edtNameRegister.error = "Nama Wajib Diisi"
            }
            if (registerBinding.edtEmailRegister.text?.isEmpty() == true) {
                registerBinding.edtEmailRegister.error = "Email Wajib Diisi"
            }
            if (registerBinding.edtPasswordRegister.text?.isEmpty() == true) {
                registerBinding.edtPasswordRegister.error = "Password Wajib Diisi"
            }
            if (registerBinding.edtPhoneRegister.text?.isEmpty() == true) {
                registerBinding.edtPhoneRegister.error = "Nomor Telephone Wajib Diisi"
            }
            if (registerBinding.edtAddressRegister.text?.isEmpty() == true) {
                registerBinding.edtAddressRegister.error = "Alamat Wajib Diisi"
            }
            if (registerBinding.edtUsernameRegister.text?.isEmpty() == true) {
                registerBinding.edtUsernameRegister.error = "Username Wajib Diisi"
            }
            if (registerBinding.edtNameRegister.text?.isNotEmpty() == true && registerBinding.edtEmailRegister.text?.isNotEmpty() == true
                && registerBinding.edtUsernameRegister.text?.isNotEmpty() == true && registerBinding.edtPasswordRegister.text?.isNotEmpty() == true
                && registerBinding.edtPhoneRegister.text?.isNotEmpty() == true && registerBinding.edtAddressRegister.text?.isNotEmpty() == true) {
                val data = RegisterRequest(
                    registerBinding.edtNameRegister.text.toString(),
                    registerBinding.edtEmailRegister.text.toString(),
                    registerBinding.edtUsernameRegister.text.toString(),
                    registerBinding.edtPasswordRegister.text.toString(),
                    dataAddress?.latlang?.latitude.toString(),
                    dataAddress?.latlang?.longitude.toString(),
                    registerBinding.edtPhoneRegister.text.toString(),
                    registerBinding.edtAddressRegister.text.toString()
                )

                viewModel.registerUser(data)
                viewModel.result.observe(this) {
                    when(it) {
                        is Result.Loading -> {
                            registerBinding.pgRegister.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            registerBinding.pgRegister.visibility = View.GONE
                            if (it.data?.status != "success") {
                                Toast.makeText(this, "An error occurred : ${it.data?.message}", Toast.LENGTH_SHORT).show()
                            }else {
                                val alertDialog = AlertDialog.Builder(this)
                                    .setTitle("Pendaftaran berhasil")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok"){_ , _ ->
                                        startActivity(Intent(this, LoginActivity::class.java))
                                        finish()
                                    }
                                alertDialog.show()
                            }
                        }
                        is Result.Error -> {
                            registerBinding.pgRegister.visibility = View.GONE
                            Snackbar.make(txt_error_register, "Terjadi Error: ${it.errorMessage}", Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }

        }
    }

}