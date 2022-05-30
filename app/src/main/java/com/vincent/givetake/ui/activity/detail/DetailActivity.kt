package com.vincent.givetake.ui.activity.detail

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build.VERSION_CODES.P
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.snackbar.Snackbar
import com.vincent.givetake.R
import com.vincent.givetake.data.source.request.WishlistRequest
import com.vincent.givetake.databinding.ActivityDetailBinding
import com.vincent.givetake.factory.ItemsRepositoryViewModelFactory
import com.vincent.givetake.ui.activity.items.edit.EditActivity
import com.vincent.givetake.ui.activity.receive.ReceiveActivity
import com.vincent.givetake.ui.activity.receiver.list.ListReceiverActivity
import com.vincent.givetake.ui.activity.request.RequestActivity
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Result

class DetailActivity : AppCompatActivity() {

    private lateinit var detailBinding: ActivityDetailBinding
    private var delete = false
    private var cancel = false
    private val imageList = ArrayList<SlideModel>()
    private val imageUlasanList = ArrayList<SlideModel>()
    private var itemId = ""
    private var token = ""
    private var isWishlist = false
    private lateinit var viewModel: DetailViewModel
    private var request = 0
    private var status = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        detailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(detailBinding.root)

        val factory = ItemsRepositoryViewModelFactory.getInstance()
        viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]

        detailBinding.detailItemBackBtn.setOnClickListener {
            onBackPressed()
        }


        val data = intent.getParcelableExtra<DataDetail>("data")
        itemId = data?.itemId.toString()
        token = data?.accessKey.toString()

        when(data?.role) {
            "owner" -> {
                viewModel.getDetailLogin(data.itemId, data.accessKey)
                detailBinding.viewOwnerButtonDetail.visibility = View.VISIBLE
                detailBinding.editDetail.visibility = View.VISIBLE
                detailBinding.favDetail.visibility = View.GONE
                detailBinding.btnReceiveDetail.visibility = View.GONE
                detailBinding.btnRequestDetail.visibility = View.GONE
                detailBinding.btnDirection.visibility = View.GONE
                detailBinding.imageUlasanCardView.visibility = View.GONE
                detailBinding.txtDetail4.visibility = View.GONE
                detailBinding.txtDetail3.visibility = View.GONE
                detailBinding.txtUlasan.visibility = View.GONE

            }
            else -> {
                viewModel.getDetailLogin(data!!.itemId, data.accessKey)
                detailBinding.viewOwnerButtonDetail.visibility = View.GONE
                detailBinding.editDetail.visibility = View.GONE
                detailBinding.favDetail.visibility = View.VISIBLE
                detailBinding.btnDirection.visibility = View.VISIBLE
            }
        }

        detailBinding.editDetail.setOnClickListener {
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra(Constant.KEY_ACCESS_EDIT, token)
            intent.putExtra(Constant.EDIT_ITEM_ID, itemId)

            startActivity(intent)
        }

        detailBinding.btnDeleteDetail.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this, R.style.CostumDialog)
                .setTitle("Hapus Item")
                .setMessage("Anda yakin ingin menghapus item ini ?")
                .setCancelable(false)
                .setPositiveButton("Ok"){_ , _ ->
                    delete = true
                }
                .setNegativeButton("Cancel"){dialog, _ ->
                    dialog.dismiss()
                }
                .setOnDismissListener {
                    if (delete) {
                        viewModel.deleteItem(data.itemId, data.accessKey)
                    }
                }
            alertDialog.show()
        }

        detailBinding.favDetail.setOnClickListener {
            val body = WishlistRequest(
                itemId
            )
            if (!isWishlist) {
                viewModel.addWishlist(token, body)
                detailBinding.favDetail.setImageResource(R.drawable.ic_fav)
            }else {
                viewModel.deleteWishlist(token, body)
                detailBinding.favDetail.setImageResource(R.drawable.ic_fav_border)
            }

        }

        detailBinding.btnRequestDetail.setOnClickListener {
            if (request == -1) {
                val intent = Intent(this@DetailActivity, RequestActivity::class.java)
                intent.putExtra(Constant.REQUEST_ACCESS, token)
                intent.putExtra(Constant.REQUEST_ITEM_ID, itemId)
                startActivity(intent)
            } else if(request == 0) {
                val alertDialog = AlertDialog.Builder(this, R.style.CostumDialog)
                    .setTitle("Batalkan ?")
                    .setMessage("Anda yakin ingin membatalkan permintaan ?")
                    .setCancelable(false)
                    .setPositiveButton("Ya"){_ , _ ->
                        cancel = true
                    }
                    .setNegativeButton("Tidak"){dialog, _ ->
                        dialog.dismiss()
                    }
                    .setOnDismissListener {
                        if (cancel) {
                            viewModel.deleteRequestItem(data.accessKey, data.itemId)
                        }
                    }
                alertDialog.show()
            }
        }

        detailBinding.btnReceiveDetail.setOnClickListener {
            val intent = Intent(this@DetailActivity, ReceiveActivity::class.java)
            intent.putExtra(Constant.RECEIVE_TOKEN, token)
            intent.putExtra(Constant.RECEIVE_ITEM_ID, itemId)
            startActivity(intent)
        }

        detailBinding.btnReceiverDetail.setOnClickListener {
            val intent = Intent(this@DetailActivity, ListReceiverActivity::class.java)
            intent.putExtra(Constant.LIST_RECEIVER_ITEM_ID, itemId)
            intent.putExtra(Constant.LIST_RECEIVER_ACCESS, token)
            startActivity(intent)
        }

        viewModel.resultLogin.observe(this) {
            when(it) {
                is Result.Loading -> detailBinding.pgDetail.visibility = View.VISIBLE
                is Result.Success -> {
                    if (it.data != null) {
                        detailBinding.pgDetail.visibility = View.GONE
                        imageList.clear()
                        for (image in it.data.data.images) {
                            imageList.add(SlideModel(image.url, ScaleTypes.CENTER_CROP))
                        }
                        detailBinding.imgItemDetail.setImageList(imageList)
                        detailBinding.txtNameDetail.text = it.data.data.items[0].name
                        detailBinding.txtDescDetail.setText(it.data.data.items[0].desc)
                        detailBinding.txtCategoryDetail.setText(it.data.data.items[0].category)
                        isWishlist = it.data.data.wish
                        if (it.data.data.wish) {
                            detailBinding.favDetail.setImageResource(R.drawable.ic_fav)
                        }else {
                            detailBinding.favDetail.setImageResource(R.drawable.ic_fav_border)
                        }
                        if (data.role == "visit") {
                            request = it.data.data.request
                            when(request){
                                0 -> {
                                    detailBinding.btnReceiveDetail.visibility = View.GONE
                                    detailBinding.btnRequestDetail.visibility = View.VISIBLE
                                    detailBinding.btnRequestDetail.text = "Batalkan"
                                    detailBinding.btnDirection.visibility = View.VISIBLE
                                    detailBinding.imageUlasanCardView.visibility = View.GONE
                                    detailBinding.txtDetail4.visibility = View.GONE
                                    detailBinding.txtDetail3.visibility = View.GONE
                                    detailBinding.txtUlasan.visibility = View.GONE
                                }
                                1 -> {
                                    detailBinding.btnReceiveDetail.visibility = View.VISIBLE
                                    detailBinding.btnRequestDetail.visibility = View.GONE
                                    detailBinding.btnDirection.visibility = View.VISIBLE
                                    detailBinding.imageUlasanCardView.visibility = View.GONE
                                    detailBinding.txtDetail4.visibility = View.GONE
                                    detailBinding.txtDetail3.visibility = View.GONE
                                    detailBinding.txtUlasan.visibility = View.GONE
                                }
                                2 -> {
                                    detailBinding.btnReceiveDetail.visibility = View.GONE
                                    detailBinding.btnRequestDetail.visibility = View.GONE
                                    detailBinding.btnDirection.visibility = View.GONE
                                    detailBinding.imageUlasanCardView.visibility = View.GONE
                                    detailBinding.txtDetail4.visibility = View.GONE
                                    detailBinding.txtDetail3.visibility = View.GONE
                                    detailBinding.txtUlasan.visibility = View.GONE
                                }
                                3 -> {
                                    imageUlasanList.clear()
                                    detailBinding.btnReceiveDetail.visibility = View.GONE
                                    detailBinding.btnRequestDetail.visibility = View.GONE
                                    detailBinding.btnDirection.visibility = View.GONE
                                    detailBinding.txtUlasan.text = it.data.data.ulasan
                                    for (image in it.data.data.ulasanImage) {
                                        imageUlasanList.add(SlideModel(image.url, ScaleTypes.CENTER_CROP))
                                    }
                                    detailBinding.imgUlasanDetail.setImageList(imageUlasanList)
                                    detailBinding.imageUlasanCardView.visibility = View.VISIBLE
                                    detailBinding.txtDetail4.visibility = View.VISIBLE
                                    detailBinding.txtDetail3.visibility = View.VISIBLE
                                    detailBinding.txtUlasan.visibility = View.VISIBLE

                                }
                                else -> {
                                    detailBinding.btnReceiveDetail.visibility = View.GONE
                                    detailBinding.btnRequestDetail.visibility = View.VISIBLE
                                    detailBinding.btnDirection.visibility = View.VISIBLE
                                    detailBinding.btnRequestDetail.text = "Ajukan"
                                    if (it.data.data.items[0].maxRadius.toDouble() < data.distance!!.toDouble()) {
                                        detailBinding.btnRequestDetail.isEnabled = false
                                        detailBinding.btnRequestDetail.text = "Tidak Memenuhi Syarat"
                                    } else {
                                        detailBinding.btnRequestDetail.isEnabled = true
                                        detailBinding.btnRequestDetail.text = "Ajukan"
                                    }
                                    detailBinding.imageUlasanCardView.visibility = View.GONE
                                    detailBinding.txtDetail4.visibility = View.GONE
                                    detailBinding.txtDetail3.visibility = View.GONE
                                    detailBinding.txtUlasan.visibility = View.GONE
                                }
                            }
                        } else {
                            status = it.data.data.items[0].itemStatus
                            if (status == 1) {
                                detailBinding.viewOwnerButtonDetail.visibility = View.GONE
                                detailBinding.editDetail.visibility = View.GONE
                                detailBinding.favDetail.visibility = View.GONE
                            } else {
                                detailBinding.viewOwnerButtonDetail.visibility = View.VISIBLE
                                detailBinding.editDetail.visibility = View.VISIBLE
                                detailBinding.favDetail.visibility = View.GONE
                            }
                        }
                    }
                }
                is Result.Error -> {
                    detailBinding.pgDetail.visibility = View.GONE
                }

            }
        }

        viewModel.resultDelete.observe(this){
            when(it) {
                is Result.Loading -> detailBinding.pgDetail.visibility = View.VISIBLE
                is Result.Success -> {
                    Log.d("DEBUGS", it.data.toString())
                    detailBinding.pgDetail.visibility = View.GONE
                    val intent = Intent()
                    intent.putExtra("deleted", true)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                is Result.Error -> detailBinding.pgDetail.visibility = View.GONE
            }
        }

        viewModel.resultAddWishlist.observe(this) {
            when(it) {
                is Result.Loading -> detailBinding.pgDetail.visibility = View.VISIBLE
                is Result.Success -> {
                    isWishlist = true
                    detailBinding.pgDetail.visibility = View.GONE
                }
                is Result.Error -> {
                    detailBinding.pgDetail.visibility = View.GONE
                    Snackbar.make(detailBinding.txtErrorDetail, it.errorMessage, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        viewModel.resultDeleteWishlist.observe(this) {
            when(it) {
                is Result.Loading -> detailBinding.pgDetail.visibility = View.VISIBLE
                is Result.Success -> {
                    isWishlist = false
                    detailBinding.pgDetail.visibility = View.GONE
                }
                is Result.Error -> {
                    detailBinding.pgDetail.visibility = View.GONE
                    Snackbar.make(detailBinding.txtErrorDetail, it.errorMessage, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        viewModel.resultDeleteRequest.observe(this) {
            when (it) {
                is Result.Loading -> detailBinding.pgDetail.visibility = View.VISIBLE
                is Result.Success -> {
                    detailBinding.pgDetail.visibility = View.GONE
                    detailBinding.btnRequestDetail.text = "Ajukan"
                }
                is Result.Error -> {
                    detailBinding.pgDetail.visibility = View.GONE
                    Snackbar.make(detailBinding.txtErrorDetail, it.errorMessage, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getDetailLogin(itemId, token)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}