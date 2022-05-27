package com.vincent.givetake.ui.fragment.items.myitems

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vincent.givetake.R
import com.vincent.givetake.data.source.response.items.DataMyItem
import com.vincent.givetake.data.source.response.items.DataOffer
import com.vincent.givetake.data.source.response.rewards.MyRewardItem
import com.vincent.givetake.data.source.response.rewards.RewardItem
import com.vincent.givetake.databinding.ItemMyOfferBinding
import com.vincent.givetake.databinding.ItemRewardHistoryLayoutBinding
import com.vincent.givetake.databinding.ItemRewardLayoutBinding
import com.vincent.givetake.ui.activity.detail.DataDetail
import com.vincent.givetake.ui.activity.detail.DetailActivity
import com.vincent.givetake.ui.fragment.items.myoffers.MyOffersAdapter
import com.vincent.givetake.ui.fragment.rewards.catalogue.CatalogueRewardAdapter
import com.vincent.givetake.ui.fragment.rewards.history.HistoryRewardAdapter

class MyItemsAdapter: RecyclerView.Adapter<MyItemsAdapter.ViewHolder>() {

    var token = ""
    private var oldItemList = emptyList<DataMyItem>()

    class ViewHolder(val binding: ItemMyOfferBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMyOfferBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            Glide.with(itemView.context)
                .load(oldItemList[position].thumbnail)
                .placeholder(R.drawable.ic_load)
                .into(binding.imgItem)
            binding.tvName.text = oldItemList[position].name
            binding.tvStatus.text = if (oldItemList[position].status == 1) "Selesai" else "Diproses"
            val status = when(oldItemList[position].status) {
                0 -> oldItemList[position].total + " Tawaran"
                1 -> "Penerima Terpilih"
                else -> "Selesai"
            }
            binding.tvStatus.text = status
            itemView.setOnClickListener {
                val data = DataDetail(
                    role = "owner",
                    itemId = oldItemList[position].id,
                    radius = "",
                    distance = "",
                    userId = "",
                    accessKey = token
                )
                Intent(itemView.context, DetailActivity::class.java).run {
                    putExtra("data", data)
                    itemView.context.startActivity(this)
                }
            }
        }
    }

    override fun getItemCount(): Int = oldItemList.size

    fun setData(newList: List<DataMyItem>) {
        val diffUtil = StoriesDiffCallback(oldItemList, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        oldItemList = newList
        diffResult.dispatchUpdatesTo(this)
    }

}

class StoriesDiffCallback(
    private val oldList: List<DataMyItem>,
    private val newList: List<DataMyItem>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].id != newList[newItemPosition].id -> {
                false
            }
            oldList[oldItemPosition].status != newList[newItemPosition].status -> {
                false
            }
            else -> true
        }
    }


}