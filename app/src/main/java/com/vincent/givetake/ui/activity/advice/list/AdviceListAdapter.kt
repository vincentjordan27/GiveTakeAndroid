package com.vincent.givetake.ui.activity.advice.list

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vincent.givetake.R
import com.vincent.givetake.data.source.response.advice.AdviceItemResponse
import com.vincent.givetake.data.source.response.items.DataOffer
import com.vincent.givetake.data.source.response.rewards.MyRewardItem
import com.vincent.givetake.data.source.response.rewards.RewardItem
import com.vincent.givetake.databinding.ItemAdviceLayoutBinding
import com.vincent.givetake.databinding.ItemMyOfferBinding
import com.vincent.givetake.databinding.ItemRewardHistoryLayoutBinding
import com.vincent.givetake.databinding.ItemRewardLayoutBinding
import com.vincent.givetake.ui.activity.detail.DataDetail
import com.vincent.givetake.ui.activity.detail.DetailActivity
import com.vincent.givetake.ui.fragment.items.myoffers.MyOffersAdapter
import com.vincent.givetake.ui.fragment.rewards.catalogue.CatalogueRewardAdapter
import com.vincent.givetake.ui.fragment.rewards.history.HistoryRewardAdapter

class AdviceListAdapter: RecyclerView.Adapter<AdviceListAdapter.ViewHolder>() {

    var token = ""
    private var oldItemList = emptyList<AdviceItemResponse>()

    class ViewHolder(val binding: ItemAdviceLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAdviceLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            binding.title.text = oldItemList[position].title
            binding.status.text = if (oldItemList[position].reply == "") "Menunggu" else "Selesai"
            binding.type.text = oldItemList[position].category
            itemView.setOnClickListener {

            }
        }
    }

    override fun getItemCount(): Int = oldItemList.size

    fun setData(newList: List<AdviceItemResponse>) {
        val diffUtil = StoriesDiffCallback(oldItemList, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        oldItemList = newList
        diffResult.dispatchUpdatesTo(this)
    }

}

class StoriesDiffCallback(
    private val oldList: List<AdviceItemResponse>,
    private val newList: List<AdviceItemResponse>
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
            oldList[oldItemPosition].reply != newList[newItemPosition].reply -> {
                false
            }
            else -> true
        }
    }


}