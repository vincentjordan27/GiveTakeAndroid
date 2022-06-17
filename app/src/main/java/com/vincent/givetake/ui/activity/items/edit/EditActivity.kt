package com.vincent.givetake.ui.activity.items.edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.vincent.givetake.R
import com.vincent.givetake.data.source.request.AddItemRequest
import com.vincent.givetake.data.source.request.DeleteItemImageRequest
import com.vincent.givetake.data.source.request.EditItemRequest
import com.vincent.givetake.databinding.ActivityEditBinding
import com.vincent.givetake.factory.ItemsPrefViewModelFactory
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.activity.items.add.adapter.AddImageAdapter
import com.vincent.givetake.ui.activity.items.add.model.ImageData
import com.vincent.givetake.ui.activity.map.AddressResult
import com.vincent.givetake.ui.activity.map.MapsActivity
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Result
import com.vincent.givetake.utils.uriToFile

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class EditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBinding
    private lateinit var viewModel: EditViewModel
    private var imageList: ArrayList<ImageData> = ArrayList()
    private var token = ""
    private var itemId = ""
    private lateinit var adapterEdit: EditItemImageAdapter
    private var currentUri: Uri? = null
    private var addressResult: AddressResult? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreferences.getInstance(dataStore)
        val factory = ItemsPrefViewModelFactory.getInstance(pref)
        viewModel = ViewModelProvider(this, factory)[EditViewModel::class.java]
        token = intent.getStringExtra(Constant.KEY_ACCESS_EDIT).toString()
        itemId = intent.getStringExtra(Constant.EDIT_ITEM_ID).toString()
        addressResult = intent.getParcelableExtra(Constant.EDIT_ITEM_ADDRESS)

        setObserver()
        setOnClickListener()

        viewModel.getDetail(itemId, token)
        adapterEdit = EditItemImageAdapter {
            if(adapterEdit.getRemovePosition() == imageList.size-1 ){
                if(imageList.size <= 4){
                    val intentImage = Intent(Intent.ACTION_PICK)
                    intentImage.type = "image/jpeg"
                    startGallery()
                }else {
                    Toast.makeText(this, "Maksimal 5 Gambar", Toast.LENGTH_LONG).show()
                }
            } else if(imageList.size != 1) {
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
                    imageList.removeAt(adapterEdit.getRemovePosition())
                    val bodyDelete = DeleteItemImageRequest(
                        it.imageId
                    )
                    viewModel.deleteImageItem(token, itemId, bodyDelete)
                    adapterEdit.setImage(imageList)
                    adapterEdit.notifyDataSetChanged()
                    dialogInterface.dismiss()
                }
                dialog.show()
            }
        }
        adapterEdit.setImage(imageList)

        binding.rvPhotoEditItem.apply {
            layoutManager = LinearLayoutManager(this@EditActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterEdit
        }

        binding.editItemBackBtn.setOnClickListener {
            onBackPressed()
        }

    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                addressResult = data.getParcelableExtra("data")
                binding.edtAddressEditItem.setText(addressResult?.address)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun setOnClickListener() {
        binding.apply {
            btnAddressEditItem.setOnClickListener {
                val intent = Intent(this@EditActivity, MapsActivity::class.java)
                resultLauncher.launch(intent)
            }
            btnSaveEditItem.setOnClickListener {
                if (binding.edtNameEditItem.text.toString().isEmpty()) {
                    binding.edtNameEditItem.error = "Nama barang harus diisi"
                }
                if (binding.edtCategoryEditItem.text.toString().isEmpty()) {
                    binding.edtCategoryEditItem.error = "Kategori tidak boleh kosong"
                }
                if (binding.edtDescEditItem.text.toString().isEmpty()) {
                    binding.edtDescEditItem.error = "Deskripsi barang tidak boleh kosong"
                }
                if (binding.edtMaxRadiusEditItem.text.toString().isEmpty()){
                    binding.edtMaxRadiusEditItem.error = "Batas radius tidak boleh kosong"
                }
                if (binding.edtAddressEditItem.text.toString().isEmpty()){
                    binding.edtAddressEditItem.error = "Batas radius tidak boleh kosong"
                }
                if (imageList.size <= 1) {
                    Snackbar.make(binding.txtErrorEditItem, "Wajib menambahkan minimal 1 gambar ", Snackbar.LENGTH_LONG).show()
                }
                if (binding.edtNameEditItem.text.toString().isNotEmpty() && binding.edtCategoryEditItem.text.toString().isNotEmpty()
                    && binding.edtDescEditItem.text.toString().isNotEmpty() && binding.edtMaxRadiusEditItem.text.toString().isNotEmpty()
                    && binding.edtAddressEditItem.text.toString().isNotEmpty() && imageList.size > 1) {
                    val data = EditItemRequest(
                        itemId,
                        binding.edtNameEditItem.text.toString(),
                        binding.edtDescEditItem.text.toString(),
                        binding.edtCategoryEditItem.text.toString(),
                        addressResult?.address.toString(),
                        addressResult?.latlang?.latitude.toString(),
                        addressResult?.latlang?.longitude.toString(),
                        binding.edtMaxRadiusEditItem.text.toString(),
                        0,
                        imageList[0].url
                    )

                    viewModel.updateItem(token, itemId, data)
                }
            }
        }
    }

    private fun setObserver() {
        viewModel.resultDetail.observe(this) {
            when(it) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    binding.apply {
                        edtNameEditItem.setText(it.data!!.data.items[0].name)
                        edtAddressEditItem.setText(it.data.data.items[0].address)
                        edtCategoryEditItem.setText(it.data.data.items[0].category)
                        edtDescEditItem.setText(it.data.data.items[0].desc)
                        edtMaxRadiusEditItem.setText(it.data.data.items[0].maxRadius)
                    }
                    val images = ArrayList<ImageData>()
                    it.data!!.data.images.forEach { image ->
                        val data = ImageData(
                            imageId = image.id,
                            url = image.url,
                            uri = null
                        )
                        images.add(data)
                    }
                    imageList.addAll(images)
                    val uri = Uri.parse("android.resource://com.vincent.givetake/drawable/ic_add_image")
                    val defaultImage = ImageData(
                        uri,
                        "",
                        ""
                    )
                    imageList.add(defaultImage)
                    adapterEdit.setImage(imageList)
                    adapterEdit.notifyDataSetChanged()
                }
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Gagal mendapatkan data barang", Toast.LENGTH_SHORT).show()
                    finish()
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
                        it.data!!.data.imageId,
                        it.data.data.fileLocation
                    )
                    val imageData = imageList[imageList.size-1]
                    imageList[imageList.size-1] = dataImageResponse
                    imageList.add(imageData)
                    adapterEdit.setImage(imageList)
                    adapterEdit.notifyDataSetChanged()
                }
                is Result.Error -> {
                    showLoading(false)
                    binding.viewPgBackground.visibility = View.GONE
                    Snackbar.make(binding.txtErrorEditItem, "Terjadi Error: ${it.errorMessage}", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        viewModel.resultDeleteImage.observe(this) {
            when(it) {
                is Result.Loading -> {
                    showLoading(true)

                }
                is Result.Success -> {
                    showLoading(false)
                }
                is Result.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.txtErrorEditItem, "Terjadi Error: ${it.errorMessage}", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        viewModel.resultUpdateItem.observe(this) {
            when(it) {
                is Result.Loading -> {
                    showLoading(true)

                }
                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Berhasil mengubah barang", Toast.LENGTH_SHORT).show()
                    finish()

                }
                is Result.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.txtErrorEditItem, "Terjadi Error: ${it.errorMessage}", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun startGallery() {
        showLoading(true)
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

            val file = uriToFile(selectedImg, this@EditActivity)

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
                viewModel.uploadImageItem(token, itemId, file)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pgEditItem.visibility = View.VISIBLE
            binding.viewPgBackground.visibility = View.VISIBLE
        } else {
            binding.pgEditItem.visibility = View.GONE
            binding.viewPgBackground.visibility = View.GONE
        }
    }

}