package com.vincent.givetake.ui.activity.detail

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Result

class DetailActivity : AppCompatActivity() {

    private lateinit var detailBinding: ActivityDetailBinding
    private var delete = false
    private val imageList = ArrayList<SlideModel>()
    private var itemId = ""
    private var token = ""
    private var isWishlist = false
    private lateinit var viewModel: DetailViewModel

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
        Log.d("DEBUGS", data.toString())
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
            }
            else -> {
                viewModel.getDetailLogin(data!!.itemId, data.accessKey)
                detailBinding.viewOwnerButtonDetail.visibility = View.GONE
                detailBinding.editDetail.visibility = View.GONE
                detailBinding.favDetail.visibility = View.VISIBLE

            }
        }

        detailBinding.editDetail.setOnClickListener {
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra(Constant.KEY_ACCESS_EDIT, token)
            intent.putExtra(Constant.EDIT_ITEM_ID, itemId)

            startActivity(intent)
        }

        viewModel.resultLogin.observe(this) {
            when(it) {
                is Result.Loading -> detailBinding.pgDetail.visibility = View.VISIBLE
                is Result.Success -> {
                    if (it.data != null) {
                        detailBinding.pgDetail.visibility = View.GONE
                        for (image in it.data.data.images) {
                            imageList.add(SlideModel("https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg&w=400&fit=max", ScaleTypes.CENTER_CROP))
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
                            when(it.data.data.request){
                                0 -> {
                                    detailBinding.btnReceiveDetail.visibility = View.GONE
                                    detailBinding.btnRequestDetail.visibility = View.VISIBLE
                                    detailBinding.btnRequestDetail.text = "Batalkan"
                                }
                                1 -> {
                                    detailBinding.btnReceiveDetail.visibility = View.VISIBLE
                                    detailBinding.btnRequestDetail.visibility = View.GONE
                                }
                                2 -> {
                                    detailBinding.btnReceiveDetail.visibility = View.GONE
                                    detailBinding.btnRequestDetail.visibility = View.GONE
                                }
                                3 -> {
                                    //Nambah ulasan
                                }
                                else -> {
                                    detailBinding.btnReceiveDetail.visibility = View.GONE
                                    detailBinding.btnRequestDetail.visibility = View.VISIBLE
                                    detailBinding.btnRequestDetail.text = "Ajukan"
                                    if (it.data.data.items[0].maxRadius.toDouble() > data.distance!!.toDouble()) {
                                        detailBinding.btnRequestDetail.isEnabled = false
                                    }
                                }
                            }
                        }
                    }
                }
                is Result.Error -> {
                    detailBinding.pgDetail.visibility = View.GONE
                }

            }
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