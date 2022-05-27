package com.vincent.givetake.ui.fragment.items.myoffers

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vincent.givetake.R
import com.vincent.givetake.data.source.response.items.DataOffer
import com.vincent.givetake.data.source.response.rewards.MyRewardItem
import com.vincent.givetake.data.source.response.rewards.RewardItem
import com.vincent.givetake.databinding.ItemMyOfferBinding
import com.vincent.givetake.databinding.ItemRewardHistoryLayoutBinding
import com.vincent.givetake.databinding.ItemRewardLayoutBinding
import com.vincent.givetake.ui.activity.detail.DataDetail
import com.vincent.givetake.ui.activity.detail.DetailActivity
import com.vincent.givetake.ui.fragment.rewards.catalogue.CatalogueRewardAdapter
import com.vincent.givetake.ui.fragment.rewards.history.HistoryRewardAdapter

class MyOffersAdapter: RecyclerView.Adapter<MyOffersAdapter.ViewHolder>() {

    var token = ""
    private var oldItemList = emptyList<DataOffer>()

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
                0 -> "Menunggu"
                1 -> "Terpilih"
                2 -> "Ditolak"
                else -> "Selesai"
            }
            binding.tvStatus.text = status
            itemView.setOnClickListener {
                val data = DataDetail(
                    role = "visit",
                    itemId = oldItemList[position].itemId,
                    radius = "",
                    distance = "",
                    userId = oldItemList[position].userId,
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

    fun setData(newList: List<DataOffer>) {
        val diffUtil = StoriesDiffCallback(oldItemList, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        oldItemList = newList
        diffResult.dispatchUpdatesTo(this)
    }

}

class StoriesDiffCallback(
    private val oldList: List<DataOffer>,
    private val newList: List<DataOffer>
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