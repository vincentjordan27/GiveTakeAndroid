package com.vincent.givetake.ui.activity.chat

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.vincent.givetake.data.source.response.chat.ChatItemResponse
import com.vincent.givetake.databinding.ActivityChatBinding
import com.vincent.givetake.factory.AllRepoViewModelFactory
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.fragment.chat.ChatMessage
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Result
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class ChatActivity : AppCompatActivity(){

    private lateinit var binding: ActivityChatBinding
    private lateinit var viewModel: ChatRoomViewModel
    private lateinit var chatMessages: ArrayList<ChatMessage>
    private lateinit var chatAdapter: ChatAdapter
    private var userId: String = ""
    private var token: String = ""
    private lateinit var database: FirebaseFirestore
    private var fcm: String = ""
    private var receiverId: String = ""
    private lateinit var item: ChatItemResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showLoading(true)

        val pref = UserPreferences.getInstance(dataStore)
        val factory = AllRepoViewModelFactory.getInstance(pref)
        viewModel = ViewModelProvider(this, factory)[ChatRoomViewModel::class.java]

        token = intent.getStringExtra(Constant.KEY_ACCESS_USER) ?: ""
        item = intent.getParcelableExtra(Constant.CHAT_ITEM)!!


        viewModel.getUserId().observe(this) {
            if (it != null) {
                userId = it
                chatAdapter.senderId = userId
                chatAdapter.notifyDataSetChanged()
            }
        }

        viewModel.getUserToken(token, item.receiverId)
        viewModel.getDetailLogin(item.itemId, token)
        setObserver()
        init()
        listenMessage()
        setListener()
    }

    private fun setListener() {
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.layoutSend.setOnClickListener {
            sendMessage()
        }
    }

    private fun setObserver() {
        viewModel.resultUserData.observe(this) {
            when(it) {
                is Result.Success -> {
                    fcm = it.data?.token.toString()
                }
                is Result.Error -> {
                    Toast.makeText(this, "Terjadi kesalahan dalam memuat chat1", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else -> {}
            }
        }
        viewModel.resultLogin.observe(this) {
            when (it) {
                is Result.Success -> {
                    binding.name.text = it.data!!.data.items[0].name
                }
                is Result.Error -> {
                    Toast.makeText(this, "Terjadi kesalahan dalam memuat chat2", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else -> {}
            }
        }
        viewModel.resultUpdateChat.observe(this) {
            when (it) {
                is Result.Error -> {
                    Toast.makeText(this, "Terjadi kesalahan dalam memuat chat3", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else -> {}
            }
        }
    }

    private fun sendMessage() {
        receiverId = if (chatMessages.size == 0) {
            item.receiverId
        } else {
            if (chatMessages[0].receiverId == userId) item.senderId else userId
        }
        val message = HashMap<String, Any>()
        message[Constant.KEY_SENDER_ID] = userId
        message[Constant.KEY_RECEIVER_ID] = item.receiverId
        message[Constant.KEY_ITEM_ID] = item.itemId
        message[Constant.KEY_CHAT_ID] = item.chatId
        message[Constant.KEY_MESSAGE] = binding.inputMessage.text.toString()
        message[Constant.KEY_TIME_SEND] = Calendar.getInstance().timeInMillis
        database.collection(Constant.KEY_COLLECTION_CHAT).add(message)

        viewModel.validateChat(token, item.chatId)

        binding.inputMessage.text = null

        Log.d("USERS", userId)
        // FCM
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pg.visibility = View.VISIBLE
            binding.inputMessage.isEnabled = false
        } else {
            binding.pg.visibility = View.GONE
            binding.inputMessage.isEnabled = true
        }
    }

    private fun init() {
        chatMessages = ArrayList()
        chatAdapter = ChatAdapter(
            chatMessages,
        )
        database = FirebaseFirestore.getInstance()
        binding.rv.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this@ChatActivity)
        }
    }

    private fun listenMessage() {
        database.collection(Constant.KEY_COLLECTION_CHAT)
            .whereEqualTo(Constant.KEY_CHAT_ID, item.chatId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null) {
                    for (documentChange in value.documentChanges) {
                        if (documentChange.type == DocumentChange.Type.ADDED) {
                            val chatMessage = ChatMessage(
                                documentChange.document.getString(Constant.KEY_SENDER_ID)!!,
                                documentChange.document.getString(Constant.KEY_RECEIVER_ID)!!,
                                documentChange.document.getString(Constant.KEY_MESSAGE)!!,
                                getReadableDateTime(documentChange.document.getLong(Constant.KEY_TIME_SEND)!!),
                                documentChange.document.getLong(Constant.KEY_TIME_SEND)!!,
                            )
                            chatMessages.add(chatMessage)
                        }
                    }
                    chatMessages.sortWith(compareBy { it.dateObject })
                    chatAdapter.notifyItemRangeChanged(chatMessages.size, chatMessages.size)
                    binding.rv.smoothScrollToPosition(chatMessages.size - 1)
                    Log.d("MESSAE", chatMessages[chatMessages.size - 1].toString())
                    Log.d("MESSAE", "User id ${userId}")
                    binding.rv.visibility = View.VISIBLE
                }
                showLoading(false)
            }

    }

    private fun getReadableDateTime(timemillis: Long) : String {
        val formatter = SimpleDateFormat("dd-MM-yyyy hh:mm:ss a")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timemillis
        return formatter.format(calendar.time)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}