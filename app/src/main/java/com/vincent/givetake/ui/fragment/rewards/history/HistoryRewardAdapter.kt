package com.vincent.givetake.ui.fragment.rewards.history

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vincent.givetake.R
import com.vincent.givetake.data.source.response.rewards.MyRewardItem
import com.vincent.givetake.data.source.response.rewards.RewardItem
import com.vincent.givetake.databinding.ItemRewardHistoryLayoutBinding
import com.vincent.givetake.databinding.ItemRewardLayoutBinding
import com.vincent.givetake.ui.fragment.rewards.catalogue.CatalogueRewardAdapter

class HistoryRewardAdapter: RecyclerView.Adapter<HistoryRewardAdapter.ViewHolder>() {

    private var oldItemList = emptyList<MyRewardItem>()

    class ViewHolder(val binding: ItemRewardHistoryLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRewardHistoryLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            Glide.with(itemView.context)
                .load(oldItemList[position].photo)
                .placeholder(R.drawable.ic_load)
                .into(binding.imgItemReward)
            binding.tvDateRewardHistory.text = oldItemList[position].date
            binding.tvStatusRewardHistory.text = if (oldItemList[position].status == 1) "Selesai" else "Diproses"
            binding.tvNameRewardHistory.text = oldItemList[position].name
            itemView.setOnClickListener {
                Toast.makeText(itemView.context, oldItemList[position].name, Toast.LENGTH_SHORT ).show()
//                Intent(itemView.context, DetailActivity::class.java).run {
//                    putExtra(DetailActivity.EXTRA_ITEM, oldItemList[position])
//                    itemView.context.startActivity(this, optionsCompat.toBundle())
//                }
            }
        }
    }

    override fun getItemCount(): Int = oldItemList.size

    fun setData(newList: List<MyRewardItem>) {
        val diffUtil = StoriesDiffCallback(oldItemList, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        oldItemList = newList
        diffResult.dispatchUpdatesTo(this)
    }

}

class StoriesDiffCallback(
    private val oldList: List<MyRewardItem>,
    private val newList: List<MyRewardItem>
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
            oldList[oldItemPosition].date != newList[newItemPosition].date -> {
                false
            }
            else -> true
        }
    }


}