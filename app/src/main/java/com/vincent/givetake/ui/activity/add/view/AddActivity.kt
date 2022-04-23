package com.vincent.givetake.ui.activity.add.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.vincent.givetake.R
import com.vincent.givetake.data.source.request.AddItemRequest
import com.vincent.givetake.databinding.ActivityAddBinding
import com.vincent.givetake.ui.activity.add.adapter.AddImageAdapter
import com.vincent.givetake.ui.activity.add.viewmodel.AddViewModel
import com.vincent.givetake.ui.activity.add.viewmodel.AddViewModelFactory
import com.vincent.givetake.ui.activity.map.AddressResult
import com.vincent.givetake.ui.activity.map.MapsActivity
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Result
import com.vincent.givetake.utils.Utils
import kotlinx.android.synthetic.main.activity_register.*
import java.io.File

class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding
    private var image: ArrayList<Uri> = ArrayList()
    private lateinit var imageAdapter: AddImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val token = intent.getStringExtra(Constant.KEY_ACCESS_USER)

        val factory = AddViewModelFactory.getInstance()
        val viewModel = ViewModelProvider(this, factory)[AddViewModel::class.java]

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
            if (binding.edtNameAddItem.text.toString().isNotEmpty() && binding.edtCategoryAddItem.text.toString().isNotEmpty()
                && binding.edtDescAddItem.text.toString().isNotEmpty() && binding.edtMaxRadiusAddItem.text.toString().isNotEmpty()
                && binding.edtAddressAddItem.text.toString().isNotEmpty()) {
                val data = AddItemRequest(
                    binding.edtNameAddItem.text.toString(),
                    binding.edtDescAddItem.text.toString(),
                    binding.edtCategoryAddItem.text.toString(),
                    dataAddress?.latlang?.latitude.toString(),
                    dataAddress?.latlang?.longitude.toString(),
                    binding.edtMaxRadiusAddItem.text.toString(),
                    0
                )

                viewModel.addItem(token!!, data)
            }
        }

        viewModel.resultAddItem.observe(this) {
            when(it) {
                is Result.Loading -> {
                    binding.pgAddItem.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.pgAddItem.visibility = View.GONE
                    if (it.data?.status != "success") {
                        Toast.makeText(this, "An error occurred : ${it.data?.message}", Toast.LENGTH_SHORT).show()
                    }else {
                        val alertDialog = AlertDialog.Builder(this)
                            .setTitle("Berhasil menambahkan item")
                            .setCancelable(false)
                            .setPositiveButton("Ok"){_ , _ ->
                                finish()
                            }
                        alertDialog.show()
                    }
                }
                is Result.Error -> {
                    binding.pgAddItem.visibility = View.GONE
                    Snackbar.make(txt_error_register, "Terjadi Error: ${it.errorMessage}", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        val uri = Uri.parse("android.resource://com.vincent.givetake/drawable/ic_add_image")
        image.add(uri)

        imageAdapter = AddImageAdapter {
            if(imageAdapter.getRemovePosition() == image.size-1 ){
                if(image.size <= 4){
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/jpeg"
                    startActivityForResult(intent, Constant.PICK_IMAGE)
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
                imageView.setImageURI(it)
                container.addView(imageView)

                val dialog = androidx.appcompat.app.AlertDialog.Builder(this, R.style.CostumDialog)
                dialog.setView(container)
                dialog.setPositiveButton("OK"){ dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                dialog.setNegativeButton("Delete"){dialogInterface, _ ->
                    image.removeAt(imageAdapter.getRemovePosition())
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, dataImage: Intent?) {
        super.onActivityResult(requestCode, resultCode, dataImage)
        if(requestCode == Constant.PICK_IMAGE && resultCode == Activity.RESULT_OK && dataImage != null){
            if (dataImage.data != null){
                val img: Uri = dataImage.data!!
                val path = Utils.getRealPathFromURI(this, img)
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
                    val uri = image[image.size-1]
                    image[image.size-1] = img
                    image.add(uri)
                    imageAdapter.setImage(image)
                    imageAdapter.notifyDataSetChanged()
                    Log.d("DEBUG", image.toString())
                    Log.d("DEBUG SIZE", image.size.toString())
                }
            }
        }

    }
}