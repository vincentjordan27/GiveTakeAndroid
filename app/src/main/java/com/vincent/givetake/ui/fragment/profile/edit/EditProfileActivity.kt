package com.vincent.givetake.ui.fragment.profile.edit

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.vincent.givetake.data.source.request.UpdateProfileRequest
import com.vincent.givetake.data.source.response.users.UserData
import com.vincent.givetake.databinding.ActivityEditProfileBinding
import com.vincent.givetake.factory.UsersRepositoryViewModelFactory
import com.vincent.givetake.ui.activity.map.AddressResult
import com.vincent.givetake.ui.activity.map.MapsActivity
import com.vincent.givetake.utils.Result
import com.vincent.givetake.utils.uriToFile

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var viewModel: EditProfileViewModel
    private lateinit var userData: UserData
    private var currentUri: Uri? = null
    private lateinit var body: UpdateProfileRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = UsersRepositoryViewModelFactory.getInstance()
        viewModel = ViewModelProvider(this, factory)[EditProfileViewModel::class.java]

        val data = intent.getParcelableExtra<UserData>(PROFILE_DATA)
        if (data != null) {
            userData = data
        }

        body = UpdateProfileRequest(
            userData.name,
            "",
            "",
            userData.latitude,
            userData.longitude,
            userData.address,
            userData.photo
        )

        setOnClickListener()
        setData()
        setObserver()
    }

    private fun setOnClickListener() {
        binding.editProfileBackBtn.setOnClickListener {
            onBackPressed()
        }
        binding.btnUpdateProfile.setOnClickListener {
            with(binding) {
                when{
                    edtOldPasswordEditProfile.text.isNullOrEmpty() -> {
                        edtOldPasswordEditProfile.error = "Silahkan masukkan password lama anda"
                    }
                    edtNewPasswordEditProfile.text.isNullOrEmpty() -> {
                        edtOldPasswordEditProfile.error = "Silahkan masukkan password baru anda"
                    }
                    edtNameEditProfile.text.isNullOrEmpty() -> {
                        edtNameEditProfile.error = "Nama tidak boleh kosong"
                    }
                    !edtOldPasswordEditProfile.text.isNullOrEmpty() && !edtNewPasswordEditProfile.text.isNullOrEmpty()
                            && !edtNameEditProfile.text.isNullOrEmpty() -> {
                        body.name = edtNameEditProfile.text.toString()
                        body.oldpassword = edtOldPasswordEditProfile.text.toString()
                        body.newpassword = edtNewPasswordEditProfile.text.toString()
                        viewModel.updateProfile(userData.accessKey, body)
                    }
                }
            }
        }
        binding.imgEditProfile.setOnClickListener {
            startGallery()
        }

        binding.btnAddressEditProfile.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                val dataAddress = data.getParcelableExtra<AddressResult>("data")
                if (dataAddress != null) {
                    body.address = dataAddress.address.toString()
                    body.latitude = dataAddress.latlang?.latitude.toString()
                    body.longitude = dataAddress.latlang?.longitude.toString()
                }

            }
        }
    }

    private fun setObserver() {
        viewModel.updateProfileResult.observe(this) {
            if (it != null) {
                when(it) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        Toast.makeText(this, "Berhasil memperbaharui data profil", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Snackbar.make(binding.txtErrorEditProfile, "Terjadi Error: ${it.errorMessage}", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
        viewModel.imageUploadResult.observe(this) {
            if (it != null) {
                when(it) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        body.photo = it.data!!.url
                        setData()
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Snackbar.make(binding.txtErrorEditProfile, "Terjadi Error: ${it.errorMessage}", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setData() {
        binding.edtNameEditProfile.setText(userData.name)
        binding.edtAddressEditProfile.setText(userData.address)
        if (currentUri == null) {
            Glide.with(this)
                .load(userData.photo)
                .into(binding.imgEditProfile)
        }else {
            Glide.with(this)
                .load(currentUri!!)
                .into(binding.imgEditProfile)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Pilih Gambar")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val file = uriToFile(selectedImg, this@EditProfileActivity)

            currentUri = selectedImg

            if(file.length() / 1048576F > 2F){
                val dialog = AlertDialog.Builder(this)
                dialog.setCancelable(false)
                dialog.setTitle("Ukuran Gambar Harus Dibawah 2 MB")
                dialog.setPositiveButton("Ok"){ dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                dialog.show()
            }else {
                viewModel.uploadImageUser(file)
            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pgProfileEdit.visibility = View.VISIBLE
            binding.btnAddressEditProfile.isEnabled  = false
            binding.btnUpdateProfile.isEnabled = false
        } else {
            binding.pgProfileEdit.visibility = View.GONE
            binding.btnAddressEditProfile.isEnabled  = true
            binding.btnUpdateProfile.isEnabled = true
        }
    }

    companion object {
        const val PROFILE_DATA = "profile_data"
    }
}