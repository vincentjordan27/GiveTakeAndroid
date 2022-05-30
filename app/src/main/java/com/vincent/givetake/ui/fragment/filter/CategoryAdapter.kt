package com.vincent.givetake.ui.fragment.filter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vincent.givetake.R
import com.vincent.givetake.databinding.ItemFilterLayoutBinding
import kotlinx.android.synthetic.main.item_filter_layout.view.*

class CategoryAdapter: RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    var index = -1
    private var oldItemList = emptyList<String>()

    class ViewHolder(val binding: ItemFilterLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemFilterLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            Glide.with(itemView.context)
            binding.name.text = oldItemList[position]
            itemView.setOnClickListener {
                index = position
                notifyDataSetChanged()
            }
            if (position == index) {
                itemView.setBackgroundColor(Color.parseColor("#FF000000"))
                itemView.name.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
            } else {
                itemView.setBackgroundColor(Color.parseColor("#FFFFFF"))
                itemView.name.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
            }
        }
    }

    override fun getItemCount(): Int = oldItemList.size

    fun setData(newList: List<String>) {
        oldItemList = newList
        notifyDataSetChanged()
    }

}