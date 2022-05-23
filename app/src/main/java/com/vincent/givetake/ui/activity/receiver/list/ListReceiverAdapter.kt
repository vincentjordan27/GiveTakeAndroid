package com.vincent.givetake.ui.activity.receiver.list

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vincent.givetake.R
import com.vincent.givetake.data.source.response.items.DataOffer
import com.vincent.givetake.data.source.response.items.ReceiverItem
import com.vincent.givetake.data.source.response.rewards.MyRewardItem
import com.vincent.givetake.data.source.response.rewards.RewardItem
import com.vincent.givetake.databinding.ItemMyOfferBinding
import com.vincent.givetake.databinding.ItemRewardHistoryLayoutBinding
import com.vincent.givetake.databinding.ItemRewardLayoutBinding
import com.vincent.givetake.databinding.ItemUserLayoutBinding
import com.vincent.givetake.ui.activity.detail.DataDetail
import com.vincent.givetake.ui.activity.detail.DetailActivity
import com.vincent.givetake.ui.fragment.items.myoffers.MyOffersAdapter
import com.vincent.givetake.ui.fragment.rewards.catalogue.CatalogueRewardAdapter
import com.vincent.givetake.ui.fragment.rewards.history.HistoryRewardAdapter

class ListReceiverAdapter(private val listener: (ReceiverItem) -> Unit): RecyclerView.Adapter<ListReceiverAdapter.ViewHolder>() {

    var token = ""
    private var oldItemList = emptyList<ReceiverItem>()

    class ViewHolder(val binding: ItemUserLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemUserLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            Glide.with(itemView.context)
                .load(oldItemList[position].photo)
                .placeholder(R.drawable.ic_load)
                .into(binding.imgItem)
            binding.tvName.text = oldItemList[position].name
            binding.tvSecond.text = oldItemList[position].distance + " KM"
            itemView.setOnClickListener {
                listener(oldItemList[position])
            }
        }
    }

    override fun getItemCount(): Int = oldItemList.size

    fun setData(newList: List<ReceiverItem>) {
        val diffUtil = StoriesDiffCallback(oldItemList, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        oldItemList = newList
        diffResult.dispatchUpdatesTo(this)
    }

}

class StoriesDiffCallback(
    private val oldList: List<ReceiverItem>,
    private val newList: List<ReceiverItem>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].requestId == newList[newItemPosition].requestId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].requestId != newList[newItemPosition].requestId -> {
                false
            }
            else -> true
        }
    }


}