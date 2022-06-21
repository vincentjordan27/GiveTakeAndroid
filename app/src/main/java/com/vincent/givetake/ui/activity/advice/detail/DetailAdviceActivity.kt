package com.vincent.givetake.ui.activity.advice.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vincent.givetake.R
import com.vincent.givetake.databinding.ActivityDetailAdviceBinding
import com.vincent.givetake.utils.Constant

class DetailAdviceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailAdviceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailAdviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra(Constant.ADVICE_DETAIL_TITLE) ?: ""
        val type = intent.getStringExtra(Constant.ADVICE_DETAIL_TYPE) ?: ""
        val desc = intent.getStringExtra(Constant.ADVICE_DETAIL_DESC) ?: ""
        val reply = intent.getStringExtra(Constant.ADVICE_DETAIL_REPLY) ?: ""

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.title.text = title
        binding.type.text = type
        binding.desc.text = desc
        binding.reply.text = if (reply == "") "Belum ada balasan" else reply
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}