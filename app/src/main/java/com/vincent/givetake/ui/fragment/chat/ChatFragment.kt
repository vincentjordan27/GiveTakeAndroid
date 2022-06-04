package com.vincent.givetake.ui.fragment.chat

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide.init
import com.google.firebase.firestore.*
import com.vincent.givetake.R
import com.vincent.givetake.data.source.response.chat.ChatItemResponse
import com.vincent.givetake.databinding.ChatFragmentBinding
import com.vincent.givetake.factory.ChatPrefViewModelFactory
import com.vincent.givetake.factory.PrefViewModelFactory
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.activity.chat.ChatActivity
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Result

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class ChatFragment : Fragment() {

    private var _binding: ChatFragmentBinding?= null
    private val binding get() = _binding!!
    private lateinit var viewModel: ChatViewModel
    private var userId: String = ""
    private var token: String = ""
    private lateinit var conversations: ArrayList<ChatItemResponse>
    private lateinit var conversationAdapter: RecentConversationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = ChatFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val pref = UserPreferences.getInstance(requireActivity().dataStore)
        val factory = ChatPrefViewModelFactory.getInstance(pref)
        viewModel = ViewModelProvider(this, factory)[ChatViewModel::class.java]

        viewModel.getUserId().observe(viewLifecycleOwner) {
            if (it != null) {
                userId = it
                conversationAdapter
            }
        }

        viewModel.getAccessKey().observe(viewLifecycleOwner) {
            if (it != null) {
                token = it
                viewModel.getAllChatRoom(token)
            }
        }

        init()
        setObserver()

    }

    private fun setObserver() {
        viewModel.resultAllChat.observe(viewLifecycleOwner) {
            when(it) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    if (it.data!!.data.isEmpty()) {
                        binding.rv.visibility = View.GONE
                        binding.tvNoData.visibility = View.VISIBLE
                    }else {
                        binding.rv.visibility = View.VISIBLE
                        binding.tvNoData.visibility = View.GONE
                        conversations.clear()
                        conversations.addAll(it.data.data)
                        conversationAdapter.notifyDataSetChanged()
                    }

                }
                is Result.Error -> {
                    showLoading(false)
                }
            }
        }
    }

    private fun init() {
        conversations = ArrayList()
        conversationAdapter = RecentConversationAdapter(conversations)
        conversationAdapter.onConversionClicked = {
            val intent = Intent(requireActivity(), ChatActivity::class.java)
            intent.putExtra(Constant.CHAT_ITEM, it)
            intent.putExtra(Constant.KEY_ACCESS_USER, token)
            startActivity(intent)
        }
        binding.rv.adapter = conversationAdapter
        binding.rv.layoutManager = LinearLayoutManager(requireContext())
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pg.visibility = View.VISIBLE
        }else {
            binding.pg.visibility = View.GONE
        }
    }
}