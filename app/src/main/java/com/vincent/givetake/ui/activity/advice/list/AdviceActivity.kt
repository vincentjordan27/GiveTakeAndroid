package com.vincent.givetake.ui.activity.advice.list

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.vincent.givetake.R
import com.vincent.givetake.databinding.ActivityAdviceBinding
import com.vincent.givetake.factory.AdvicesRepositoryViewModelFactory
import com.vincent.givetake.ui.activity.advice.AdviceViewModel
import com.vincent.givetake.ui.activity.advice.add.AddAdviceActivity
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Result

class AdviceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdviceBinding
    private lateinit var viewModel: AdviceViewModel
    private lateinit var adviceAdapter: AdviceListAdapter
    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = intent.getStringExtra(Constant.KEY_ACCESS_USER) ?: ""

        val factory = AdvicesRepositoryViewModelFactory.getInstance()
        viewModel = ViewModelProvider(this, factory)[AdviceViewModel::class.java]
        adviceAdapter = AdviceListAdapter()

        setObserver()

        viewModel.myAdvices(token)

        binding.rv.apply {
            adapter = adviceAdapter
            layoutManager = LinearLayoutManager(this@AdviceActivity)
        }

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.addBtn.setOnClickListener {
            val intent = Intent(this, AddAdviceActivity::class.java)
            intent.putExtra(Constant.KEY_ACCESS_USER, token)
            startActivity(intent)
        }
    }

    private fun setObserver() {
        viewModel.myAdviceResult.observe(this) {
            when (it) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    if (it.data?.data!!.isEmpty()) {
                        binding.tvNoData.visibility = View.VISIBLE
                        binding.rv.visibility = View.GONE
                    } else {
                        binding.tvNoData.visibility = View.GONE
                        binding.rv.visibility = View.VISIBLE
                        adviceAdapter.setData(it.data.data)
                    }
                }
                is Result.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.txtError, it.errorMessage, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pg.visibility = View.VISIBLE
        }else {
            binding.pg.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onResume() {
        super.onResume()
        viewModel.myAdvices(token)
    }
}