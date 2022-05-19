package com.vincent.givetake.ui.fragment.items.wishlist

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vincent.givetake.R
import com.vincent.givetake.data.source.response.items.DataOffer
import com.vincent.givetake.data.source.response.items.WishlistItem
import com.vincent.givetake.data.source.response.rewards.MyRewardItem
import com.vincent.givetake.data.source.response.rewards.RewardItem
import com.vincent.givetake.databinding.ItemMyOfferBinding
import com.vincent.givetake.databinding.ItemRewardHistoryLayoutBinding
import com.vincent.givetake.databinding.ItemRewardLayoutBinding
import com.vincent.givetake.databinding.ItemWishlistLayoutBinding
import com.vincent.givetake.ui.activity.detail.DataDetail
import com.vincent.givetake.ui.activity.detail.DetailActivity
import com.vincent.givetake.ui.fragment.items.myoffers.MyOffersAdapter
import com.vincent.givetake.ui.fragment.rewards.catalogue.CatalogueRewardAdapter
import com.vincent.givetake.ui.fragment.rewards.history.HistoryRewardAdapter

class WishlistAdapter: RecyclerView.Adapter<WishlistAdapter.ViewHolder>() {

    var token = ""
    private var oldItemList = emptyList<WishlistItem>()

    class ViewHolder(val binding: ItemWishlistLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemWishlistLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            Glide.with(itemView.context)
                .load(oldItemList[position].thumbnail)
                .placeholder(R.drawable.ic_load)
                .into(binding.imgWishlist)
            itemView.setOnClickListener {
                val data = DataDetail(
                    role = "visit",
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

    fun setData(newList: List<WishlistItem>) {
        val diffUtil = StoriesDiffCallback(oldItemList, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        oldItemList = newList
        diffResult.dispatchUpdatesTo(this)
    }

}

class StoriesDiffCallback(
    private val oldList: List<WishlistItem>,
    private val newList: List<WishlistItem>
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
            else -> true
        }
    }


}