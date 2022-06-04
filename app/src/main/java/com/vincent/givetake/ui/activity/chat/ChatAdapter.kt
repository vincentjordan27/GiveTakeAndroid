package com.vincent.givetake.ui.activity.chat

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vincent.givetake.databinding.ItemChatReceivedLayoutBinding
import com.vincent.givetake.databinding.ItemChatSentLayoutBinding
import com.vincent.givetake.ui.fragment.chat.ChatMessage

class ChatAdapter(private val chatMessages: List<ChatMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var senderId: String = ""
    inner class SentMessageViewHolder(private val binding: ItemChatSentLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(chatMessage: ChatMessage) {
            with(binding) {
                textMessage.text = chatMessage.message
                textDateTime.text = chatMessage.dateTime
            }
        }
    }

    inner class ReceivedMessageViewHolder(private val binding: ItemChatReceivedLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(chatMessage: ChatMessage) {
            with(binding) {
                textMessage.text = chatMessage.message
                textDateTime.text = chatMessage.dateTime
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            SentMessageViewHolder(
                ItemChatSentLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }else {
            ReceivedMessageViewHolder(
                ItemChatReceivedLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            (holder as SentMessageViewHolder).setData(chatMessages[position])
        }else {
            (holder as ReceivedMessageViewHolder).setData(chatMessages[position])
        }
    }


    override fun getItemCount(): Int {
        return chatMessages.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatMessages[position].senderId == senderId) {
            VIEW_TYPE_SENT
        }else {
            Log.d("DEBUGS", "${chatMessages[position].senderId} ${senderId}")
            VIEW_TYPE_RECEIVED
        }
    }

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

}