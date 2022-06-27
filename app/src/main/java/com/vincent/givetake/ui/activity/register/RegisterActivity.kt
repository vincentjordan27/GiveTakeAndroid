package com.vincent.givetake.ui.activity.register

import android.app.Activity
import androidx.appcompat.app.AlertDialog

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.vincent.givetake.R
import com.vincent.givetake.databinding.ActivityRegisterBinding
import com.vincent.givetake.ui.activity.map.AddressResult
import com.vincent.givetake.ui.activity.map.MapsActivity
import com.vincent.givetake.data.source.request.RegisterRequest
import com.vincent.givetake.factory.UsersRepositoryViewModelFactory
import com.vincent.givetake.ui.activity.login.LoginActivity
import com.vincent.givetake.ui.activity.otp.OtpRegisterActivity
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Result
import com.vincent.givetake.utils.uriToFile
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.delay
import java.io.File

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerBinding: ActivityRegisterBinding
    private var file: File ?= null
    private var currentUri : Uri? = null
    private lateinit var viewModel: RegisterViewModel
    private var url: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = registerBinding.root
        setContentView(view)

        val factory = UsersRepositoryViewModelFactory.getInstance()
        viewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]
        setObserver()

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

        registerBinding.imgRegister.setOnClickListener {
            startGallery()
        }


        registerBinding.btnRegister.setOnClickListener {
            when {
                currentUri == null -> {
                    Snackbar.make(registerBinding.txtErrorRegister, "Silahkan pilih gambar profil dahulu", Snackbar.LENGTH_LONG).show()
                }
                registerBinding.edtEmailRegister.text?.isEmpty() == true -> {
                    registerBinding.edtEmailRegister.error = "Email Wajib Diisi"
                }
                registerBinding.edtPasswordRegister.text?.isEmpty() == true -> {
                    registerBinding.edtPasswordRegister.error = "Password Wajib Diisi"
                }
                registerBinding.edtPasswordRegister.text?.length!! < 8 -> {
                    registerBinding.edtPasswordRegister.error = "Password minimal sebanyak 8 karakter"
                }
                registerBinding.edtPhoneRegister.text?.isEmpty() == true -> {
                    registerBinding.edtPhoneRegister.error = "Nomor Telephone Wajib Diisi"
                }
                registerBinding.edtNameRegister.text?.isEmpty() ==  true -> {
                    registerBinding.edtNameRegister.error = "Nama Wajib Diisi"
                }
                registerBinding.edtUsernameRegister.text?.isEmpty() == true -> {
                    registerBinding.edtUsernameRegister.error = "Username Wajib Diisi"
                }
                registerBinding.edtAddressRegister.text?.isEmpty() == true -> {
                    registerBinding.edtAddressRegister.error = "Alamat Wajib Diisi"
                }
                registerBinding.edtPhoneRegister.text?.substring(0, 3) != "628" -> {
                    registerBinding.edtPhoneRegister.error = "Nomor Telephone Wajib Dimulai Dengan 628"
                }
                registerBinding.edtNameRegister.text?.isNotEmpty() == true && registerBinding.edtEmailRegister.text?.isNotEmpty() == true
                        && registerBinding.edtUsernameRegister.text?.isNotEmpty() == true && registerBinding.edtPasswordRegister.text?.isNotEmpty() == true
                        && registerBinding.edtPhoneRegister.text?.isNotEmpty() == true && registerBinding.edtAddressRegister.text?.isNotEmpty() == true
                        && currentUri != null && registerBinding.edtPhoneRegister.text?.substring(0, 3) == "628" && registerBinding.edtPasswordRegister.text?.length!! >= 8->
                {
                    val data = RegisterRequest(
                        registerBinding.edtNameRegister.text.toString(),
                        registerBinding.edtEmailRegister.text.toString(),
                        registerBinding.edtUsernameRegister.text.toString(),
                        registerBinding.edtPasswordRegister.text.toString(),
                        dataAddress?.latlang?.latitude.toString(),
                        dataAddress?.latlang?.longitude.toString(),
                        registerBinding.edtPhoneRegister.text.toString(),
                        registerBinding.edtAddressRegister.text.toString(),
                        url
                    )
                    viewModel.registerUser(data)
                }
            }
        }
    }

    private fun setObserver() {
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
                        val alertDialog = AlertDialog.Builder(this, R.style.CostumDialog)
                            .setTitle("Pendaftaran berhasil")
                            .setCancelable(false)
                            .setPositiveButton("Ok"){_ , _ ->
                                val intent = Intent(this, OtpRegisterActivity::class.java)
                                intent.putExtra(Constant.KEY_PHONE, registerBinding.edtPhoneRegister.text.toString())
                                intent.putExtra(Constant.KEY_USERNAME, registerBinding.edtUsernameRegister.text.toString())
                                intent.putExtra(Constant.KEY_PASSWORD, registerBinding.edtPasswordRegister.text.toString())

                                startActivity(intent)
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

        viewModel.imageUploadResult.observe(this) {
            when(it) {
                is Result.Loading -> {
                    registerBinding.pgRegister.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    registerBinding.pgRegister.visibility = View.GONE
                    url = it.data!!.url
                    Glide.with(this@RegisterActivity)
                        .load(currentUri!!)
                        .into(registerBinding.imgRegister)
                }
                is Result.Error -> {
                    registerBinding.pgRegister.visibility = View.GONE
                    Snackbar.make(txt_error_register, "Terjadi Error: ${it.errorMessage}", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Pilih Gambar")
        launcherIntentGallery.launch(chooser)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val file = uriToFile(selectedImg, this@RegisterActivity)

            currentUri = selectedImg

            if(file.length() / 1048576F > 5F){
                val dialog = AlertDialog.Builder(this)
                dialog.setCancelable(false)
                dialog.setTitle("Ukuran Gambar Harus Dibawah 5 MB")
                dialog.setPositiveButton("Ok"){ dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                dialog.show()
            }else {
                viewModel.uploadImageUser(file)
            }
        }
    }

}