package com.vincent.givetake.ui.fragment.rewards.catalogue

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vincent.givetake.R
import com.vincent.givetake.data.source.response.rewards.RewardItem
import com.vincent.givetake.databinding.ItemRewardLayoutBinding
import com.vincent.givetake.ui.activity.reward.detail.DetailRewardActivity
import com.vincent.givetake.utils.Constant

class CatalogueRewardAdapter: RecyclerView.Adapter<CatalogueRewardAdapter.ViewHolder>() {

    private var oldItemList = emptyList<RewardItem>()

    class ViewHolder(val binding: ItemRewardLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRewardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            Glide.with(itemView.context)
                .load(oldItemList[position].photo)
                .placeholder(R.drawable.ic_load)
                .into(binding.imgItemReward)
            binding.txtNameRewardRv.text = oldItemList[position].name
            binding.txtPriceRewardRv.text = oldItemList[position].price.toString() + " Pts"
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailRewardActivity::class.java)
                intent.putExtra(Constant.REWARD_ID, oldItemList[position].id)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = oldItemList.size

    fun setData(newList: List<RewardItem>) {
        val diffUtil = StoriesDiffCallback(oldItemList, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        oldItemList = newList
        diffResult.dispatchUpdatesTo(this)
    }

}

class StoriesDiffCallback(
    private val oldList: List<RewardItem>,
    private val newList: List<RewardItem>
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
            oldList[oldItemPosition].name != newList[newItemPosition].name -> {
                false
            }
            oldList[oldItemPosition].photo != newList[newItemPosition].photo -> {
                false
            }
            oldList[oldItemPosition].price != newList[newItemPosition].price -> {
                false
            }
            oldList[oldItemPosition].stock != newList[newItemPosition].stock -> {
                false
            }
            else -> true
        }
    }

}