package com.vincent.givetake.ui.fragment.chat


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.auth.User
import com.vincent.givetake.data.source.response.chat.ChatItemResponse
import com.vincent.givetake.databinding.ItemRecentConversationLayoutBinding

class RecentConversationAdapter(private val chatMessages: List<ChatItemResponse>) : RecyclerView.Adapter<RecentConversationAdapter.ViewHolder>() {

    var userId: String = ""
    var onConversionClicked : ((ChatItemResponse) -> Unit)? = null
    inner class ViewHolder (private val binding: ItemRecentConversationLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(chatMessage: ChatItemResponse) {
            binding.textName.text = chatMessage.name
            binding.root.setOnClickListener {
                onConversionClicked?.invoke(chatMessage)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecentConversationAdapter.ViewHolder {
        return ViewHolder(ItemRecentConversationLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecentConversationAdapter.ViewHolder, position: Int) {
        holder.setData(chatMessages[position])
    }

    override fun getItemCount(): Int = chatMessages.size

}