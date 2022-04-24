package com.vincent.givetake.ui.activity.add.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.vincent.givetake.R
import com.vincent.givetake.data.source.request.AddItemRequest
import com.vincent.givetake.data.source.request.DeleteItemImageRequest
import com.vincent.givetake.databinding.ActivityAddBinding
import com.vincent.givetake.ui.activity.add.adapter.AddImageAdapter
import com.vincent.givetake.ui.activity.add.model.ImageData
import com.vincent.givetake.ui.activity.add.viewmodel.AddViewModel
import com.vincent.givetake.ui.activity.add.viewmodel.AddViewModelFactory
import com.vincent.givetake.ui.activity.home.MainActivity
import com.vincent.givetake.ui.activity.map.AddressResult
import com.vincent.givetake.ui.activity.map.MapsActivity
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Constant.PICK_IMAGE
import com.vincent.givetake.utils.Result
import com.vincent.givetake.utils.Utils
import kotlinx.android.synthetic.main.activity_register.*
import java.io.File

class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding
    private var image: ArrayList<ImageData> = ArrayList()
    private lateinit var imageAdapter: AddImageAdapter
    private var itemId = ""
    private var token: String = ""
    private lateinit var viewModel: AddViewModel
    private var currentUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkStoragePermission()

        val factory = AddViewModelFactory.getInstance()
        viewModel = ViewModelProvider(this, factory)[AddViewModel::class.java]
        observerViewModel()

        token = intent.getStringExtra(Constant.KEY_ACCESS_USER).toString()
        viewModel.getItemId()

        var dataAddress: AddressResult? = null
        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    dataAddress = data.getParcelableExtra("data")
                    binding.edtAddressAddItem.setText(dataAddress?.address)
                }
            }
        }
        val intent = Intent(this@AddActivity, MapsActivity::class.java)

        binding.btnAddressAddItem.setOnClickListener {
            resultLauncher.launch(intent)
        }

        binding.btnSaveAddItem.setOnClickListener {
            if (binding.edtNameAddItem.text.toString().isEmpty()) {
                binding.edtNameAddItem.error = "Nama barang harus diisi"
            }
            if (binding.edtCategoryAddItem.text.toString().isEmpty()) {
                binding.edtCategoryAddItem.error = "Kategori tidak boleh kosong"
            }
            if (binding.edtDescAddItem.text.toString().isEmpty()) {
                binding.edtDescAddItem.error = "Deskripsi barang tidak boleh kosong"
            }
            if (binding.edtMaxRadiusAddItem.text.toString().isEmpty()){
                binding.edtMaxRadiusAddItem.error = "Batas radius tidak boleh kosong"
            }
            if (binding.edtAddressAddItem.text.toString().isEmpty()){
                binding.edtAddressAddItem.error = "Batas radius tidak boleh kosong"
            }
            if (image.size <= 1) {
                Snackbar.make(binding.txtErrorAddItem, "Wajib menambahkan minimal 1 gambar ", Snackbar.LENGTH_LONG).show()
            }
            if (binding.edtNameAddItem.text.toString().isNotEmpty() && binding.edtCategoryAddItem.text.toString().isNotEmpty()
                && binding.edtDescAddItem.text.toString().isNotEmpty() && binding.edtMaxRadiusAddItem.text.toString().isNotEmpty()
                && binding.edtAddressAddItem.text.toString().isNotEmpty() && image.size > 1) {
                val data = AddItemRequest(
                    itemId,
                    binding.edtNameAddItem.text.toString(),
                    binding.edtDescAddItem.text.toString(),
                    binding.edtCategoryAddItem.text.toString(),
                    dataAddress?.latlang?.latitude.toString(),
                    dataAddress?.latlang?.longitude.toString(),
                    binding.edtMaxRadiusAddItem.text.toString(),
                    0
                )

                viewModel.addItem(token, data)
            }
        }

        val uri = Uri.parse("android.resource://com.vincent.givetake/drawable/ic_add_image")
        val defaultImage = ImageData(
            uri,
            ""
        )
        image.add(defaultImage)

        imageAdapter = AddImageAdapter {
            if(imageAdapter.getRemovePosition() == image.size-1 ){
                if(image.size <= 4){
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            val dialog = AlertDialog.Builder(this, R.style.CostumDialog)
                            dialog.setCancelable(false)
                            dialog.setTitle("Silahkan izinkan akses penyimpanan untuk menambahkan gambar")
                            dialog.setPositiveButton("Ya"){ dialogInterface, _ ->
                                val i = Intent()
                                i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                i.addCategory(Intent.CATEGORY_DEFAULT)
                                i.data = Uri.parse("package:$packageName")
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                                dialogInterface.dismiss()
                                startActivity(i)
                            }
                            dialog.setNegativeButton("Tidak"){dialogInterface, _ ->
                                dialogInterface.dismiss()
                            }
                            dialog.show()
                        }else {
                            val intent = Intent(Intent.ACTION_PICK)
                            intent.type = "image/jpeg"
                            startActivityForResult(intent, PICK_IMAGE)
                        }


                }else {
                    Toast.makeText(this, "Maksimal 5 Gambar", Toast.LENGTH_LONG).show()
                }
            } else if(image.size != 1) {
                val imageView = ImageView(this)
                imageView.scaleType = ImageView.ScaleType.FIT_XY
                val container = FrameLayout(this)
                val param = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                param.setMargins(30,30,30,30)
                imageView.layoutParams = param
                imageView.setImageURI(it.uri)
                container.addView(imageView)

                val dialog = AlertDialog.Builder(this, R.style.CostumDialog)
                dialog.setView(container)
                dialog.setPositiveButton("OK"){ dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                dialog.setNegativeButton("Hapus"){dialogInterface, _ ->
                    image.removeAt(imageAdapter.getRemovePosition())
                    val bodyDelete = DeleteItemImageRequest(
                        it.imageId
                    )
                    viewModel.deleteImageItem(token, itemId, bodyDelete)
                    imageAdapter.setImage(image)
                    imageAdapter.notifyDataSetChanged()
                    dialogInterface.dismiss()
                }
                dialog.show()
            }
        }
        imageAdapter.setImage(image)

        binding.rvPhotoAddItem.apply {
            layoutManager = LinearLayoutManager(this@AddActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter
        }
    }

    private fun observerViewModel() {
        viewModel.resultAddItem.observe(this) {
            when(it) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    if (it.data?.status != "success") {
                        Toast.makeText(this, "An error occurred : ${it.data?.message}", Toast.LENGTH_SHORT).show()
                    }else {
                        val alertDialog = AlertDialog.Builder(this, R.style.CostumDialog)
                            .setTitle("Berhasil menambahkan item")
                            .setCancelable(false)
                            .setPositiveButton("Ok"){_ , _ ->
                                finish()
                            }
                        alertDialog.show()
                    }
                }
                is Result.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.txtErrorAddItem, "Terjadi Error: ${it.errorMessage}", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        viewModel.resultItemId.observe(this){
            when(it) {
                is Result.Loading -> {
                    showLoading(true)
                    binding.viewPgBackground.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    showLoading(false)
                    binding.viewPgBackground.visibility = View.GONE
                    itemId = it.data.data.itemId
                    Log.d("DEBUGS", itemId)
                }
                is Result.Error -> {
                    showLoading(false)
                    binding.viewPgBackground.visibility = View.GONE
                    Snackbar.make(binding.txtErrorAddItem, "Terjadi Error: ${it.errorMessage}", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        viewModel.resultUploadImage.observe(this){
            when(it) {
                is Result.Loading -> {
                    showLoading(true)
                    binding.viewPgBackground.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    showLoading(false)
                    binding.viewPgBackground.visibility = View.GONE
                    val dataImageResponse = ImageData(
                        currentUri!!,
                        it.data!!.data.imageId
                    )
                    val imageData = image[image.size-1]
                    image[image.size-1] = dataImageResponse
                    image.add(imageData)
                    imageAdapter.setImage(image)
                    imageAdapter.notifyDataSetChanged()
                    Log.d("DEBUG", image.toString())
                    Log.d("DEBUG SIZE", image.size.toString())
                }
                is Result.Error -> {
                    showLoading(false)
                    binding.viewPgBackground.visibility = View.GONE
                    Snackbar.make(binding.txtErrorAddItem, "Terjadi Error: ${it.errorMessage}", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        viewModel.resultDeleteImage.observe(this) {
            when(it) {
                is Result.Loading -> {
                    showLoading(true)
                    binding.viewPgBackground.visibility = View.VISIBLE

                }
                is Result.Success -> {
                    showLoading(false)
                    binding.viewPgBackground.visibility = View.GONE
                }
                is Result.Error -> {
                    showLoading(false)
                    binding.viewPgBackground.visibility = View.GONE
                    Snackbar.make(binding.txtErrorAddItem, "Terjadi Error: ${it.errorMessage}", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, dataImage: Intent?) {
        super.onActivityResult(requestCode, resultCode, dataImage)
        if(requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && dataImage != null){
            if (dataImage.data != null){
                currentUri = dataImage.data!!
                val path = Utils.getRealPathFromURI(this, currentUri!!)
                val file = File(path!!)
                Log.d("DEBUG","${file.length() / 1048576}")
                if(file.length() / 1048576F > 2F){
                    val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
                    dialog.setCancelable(false)
                    dialog.setTitle("Ukuran Gambar Harus Dibawah 2 MB")
                    dialog.setPositiveButton("Ok"){ dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    dialog.show()
                }else {
                    viewModel.uploadImageItem(token, itemId, path)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this@AddActivity,
                            Manifest.permission.READ_EXTERNAL_STORAGE) ==
                                PackageManager.PERMISSION_GRANTED)) {
                    }
                } else {
                    finish()
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun checkStoragePermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val dialog = AlertDialog.Builder(this, R.style.CostumDialog)
            dialog.setCancelable(false)
            dialog.setTitle("Silahkan izinkan akses penyimpanan untuk menambahkan gambar")
            dialog.setPositiveButton("Ya"){ dialogInterface, _ ->
                val i = Intent()
                i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                i.addCategory(Intent.CATEGORY_DEFAULT)
                i.data = Uri.parse("package:$packageName")
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                dialogInterface.dismiss()
                startActivity(i)
            }
            dialog.setNegativeButton("Tidak"){dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            dialog.show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pgAddItem.visibility = View.VISIBLE
        } else {
            binding.pgAddItem.visibility = View.GONE
        }
    }
}