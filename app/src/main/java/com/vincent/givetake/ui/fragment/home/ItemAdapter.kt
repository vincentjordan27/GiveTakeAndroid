package com.vincent.givetake.ui.fragment.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vincent.givetake.R
import com.vincent.givetake.data.source.response.items.ItemResponse
import kotlinx.android.synthetic.main.item_home_layout.view.*

class ItemAdapter : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    private var listData = ArrayList<ItemResponse>()
    var onItemClick: ((ItemResponse) -> Unit)? = null
    var role: String = ""

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newData: List<ItemResponse>?){
        if(newData == null) return
        listData.clear()
        listData.addAll(newData)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData(){
        listData.clear()
        notifyDataSetChanged()
    }

    fun setRoleUser(roleValue: String) {
        role = roleValue
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: ItemResponse){
            with(itemView){
                txt_name_home_rv.text = data.name
                txt_radius_home_rv.text = "${data.distance} KM"
                Glide.with(context)
                    .load(data.thumbnail)
                    .placeholder(R.drawable.ic_load)
                    .into(img_item_home)
            }
        }

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(listData[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val data = listData[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}