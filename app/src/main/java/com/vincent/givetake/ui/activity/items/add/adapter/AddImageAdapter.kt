package com.vincent.givetake.ui.activity.items.add.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vincent.givetake.R
import com.vincent.givetake.ui.activity.items.add.model.ImageData

var positionRemove : Int? = null

class AddImageAdapter(private val listener: (ImageData) -> Unit) : RecyclerView.Adapter<AddImageAdapter.ViewHolder>() {
    private var listData = ArrayList<ImageData>()

    fun setImage(dataImage : List<ImageData>?){
        if(dataImage == null) return
        listData.clear()
        listData.addAll(dataImage)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.img_list)
        fun bind(data: ImageData, listener: (ImageData) -> Unit, position: Int){
            itemView.apply {
                Glide.with(this).load(data.uri).into(imageView)
                setOnClickListener {
                    positionRemove = position
                    listener(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_add_image_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position],listener,position)
    }

    override fun getItemCount() = listData.size

    fun getRemovePosition() : Int {
        return positionRemove as Int
    }
}