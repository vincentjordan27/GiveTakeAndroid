package com.vincent.givetake.ui.activity.receive

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.vincent.givetake.R
import com.vincent.givetake.data.source.request.DeleteItemImageRequest
import com.vincent.givetake.data.source.request.DeleteUlasanImageRequest
import com.vincent.givetake.data.source.request.FinishReceiveItemRequest
import com.vincent.givetake.databinding.ActivityReceiveBinding
import com.vincent.givetake.factory.ItemsRepositoryViewModelFactory
import com.vincent.givetake.ui.activity.items.add.adapter.AddImageAdapter
import com.vincent.givetake.ui.activity.items.add.model.ImageData
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Result
import com.vincent.givetake.utils.reduceFileImage
import com.vincent.givetake.utils.uriToFile
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReceiveActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiveBinding
    private lateinit var adapterImageUlasan: AddImageUlasanAdapter
    private var image: ArrayList<ImageData> = ArrayList()
    private var currentUri: Uri? = null
    private lateinit var viewModel: ReceiveViewModel
    private var token = ""
    private var itemId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = intent.getStringExtra(Constant.RECEIVE_TOKEN) ?: ""
        itemId = intent.getStringExtra(Constant.RECEIVE_ITEM_ID) ?: ""

        val factory = ItemsRepositoryViewModelFactory.getInstance()
        viewModel = ViewModelProvider(this, factory)[ReceiveViewModel::class.java]

        val uri = Uri.parse("android.resource://com.vincent.givetake/drawable/ic_add_image")
        val defaultImage = ImageData(
            uri,
            "",
            ""
        )
        image.add(defaultImage)

        adapterImageUlasan = AddImageUlasanAdapter {
            if(adapterImageUlasan.getRemovePosition() == image.size-1 ){
                if(image.size <= 4){
                    val intentImage = Intent(Intent.ACTION_PICK)
                    intentImage.type = "image/jpeg"
                    startGallery()
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
                    image.removeAt(adapterImageUlasan.getRemovePosition())
                    val bodyDelete = DeleteUlasanImageRequest(
                        it.imageId
                    )
                    viewModel.deleteImageUlasan(token, itemId, bodyDelete)
                    adapterImageUlasan.setImage(image)
                    adapterImageUlasan.notifyDataSetChanged()
                    dialogInterface.dismiss()
                }
                dialog.show()
            }
        }

        adapterImageUlasan.setImage(image)
        setObserver()

        binding.rvPhoto.apply {
            layoutManager = LinearLayoutManager(this@ReceiveActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterImageUlasan

        }

        binding.btnReceive.setOnClickListener {
            if (binding.edtUlasan.text.isNullOrEmpty()) {
                binding.edtUlasan.error = "Ulasan tidak boleh kosong"
            }
            if (image.size < 2) {
                Snackbar.make(binding.txtError, "Anda wajib menambahkan minimal 1 gambar", Snackbar.LENGTH_LONG).show()
            }
            if (!binding.edtUlasan.text.isNullOrEmpty() && image.size > 1) {
                val body = FinishReceiveItemRequest(
                    binding.edtUlasan.text.toString()
                )
                viewModel.finishReceiveItem(token, itemId, body)
            }
        }

    }

    private fun setObserver() {
        viewModel.uploadImageUlasanResult.observe(this) {
            when(it) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    val dataImageResponse = ImageData(
                        currentUri!!,
                        it.data!!.data.imageId,
                        it.data.data.fileLocation
                    )
                    val imageData = image[image.size-1]
                    image[image.size-1] = dataImageResponse
                    image.add(imageData)
                    adapterImageUlasan.setImage(image)
                    adapterImageUlasan.notifyDataSetChanged()
                }
                is Result.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.txtError, it.errorMessage, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        viewModel.deleteImageUlasanResult.observe(this) {
            when(it) {
                is Result.Loading -> {
                    showLoading(true)

                }
                is Result.Success -> {
                    showLoading(false)
                }
                is Result.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.txtError, "${it.errorMessage}", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        viewModel.finishReceiveItemResult.observe(this) {
            when(it) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    val dialog = AlertDialog.Builder(this, R.style.CostumDialog)
                    dialog.setMessage("Barang berhasil diterima")
                    dialog.setPositiveButton("Ok") {_, _ ->}
                    dialog.setOnDismissListener {
                        finish()
                    }
                    dialog.show()
                }
                is Result.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.txtError, "${it.errorMessage}", Snackbar.LENGTH_LONG).show()
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
        showLoading(false)
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            var file = uriToFile(selectedImg, this@ReceiveActivity)

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
                lifecycleScope.launch {
                    showLoading(true)
                    withContext(Dispatchers.IO) {
                        file = reduceFileImage(file)
                        viewModel.uploadImageUlasan(token ,itemId, file)
                    }
                }
            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pg.visibility = View.VISIBLE
            binding.btnReceive.isEnabled = false
            binding.edtUlasan.isFocusable = false
            binding.backBtn.isEnabled = false
        } else {
            binding.pg.visibility = View.GONE
            binding.btnReceive.isEnabled = true
            binding.edtUlasan.isFocusable = true
            binding.backBtn.isEnabled = true
        }
    }
}