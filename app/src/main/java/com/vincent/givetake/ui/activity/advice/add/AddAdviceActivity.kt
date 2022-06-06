package com.vincent.givetake.ui.activity.advice.add

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.vincent.givetake.R
import com.vincent.givetake.data.source.request.AddAdviceRequest
import com.vincent.givetake.databinding.ActivityAddAdviceBinding
import com.vincent.givetake.factory.AdvicesRepositoryViewModelFactory
import com.vincent.givetake.ui.activity.advice.AdviceViewModel
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.Result

class AddAdviceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddAdviceBinding
    private lateinit var viewModel: AdviceViewModel
    private var accessKey = ""
    private var type = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAdviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val categoryData = resources.getStringArray(R.array.type)
        val arrayAdapter = ArrayAdapter(this, R.layout.category_item, categoryData)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)

        binding.autoCompleteTextView.setOnItemClickListener { _, _, i, _ ->
            type = categoryData[i]
        }

        accessKey = intent.getStringExtra(Constant.KEY_ACCESS_USER) ?: ""

        val factory = AdvicesRepositoryViewModelFactory.getInstance()
        viewModel = ViewModelProvider(this, factory)[AdviceViewModel::class.java]

        setListener()
        setObserver()
    }

    private fun setListener() {
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.btnSend.setOnClickListener {
            when {
                binding.edtTitle.text.toString().isEmpty() -> {
                    binding.edtTitle.error = "Judul tidak boleh kosong"
                }
                type == "" -> {
                    binding.edtCategory.error = "Tipe harus dipilih"
                }
                binding.edtDesc.text.toString().trim().isEmpty() -> {
                    binding.edtDesc.error = "Deskripsi tidak boleh kosong"
                }
                binding.edtTitle.text.toString().trim().isNotEmpty() && type != "" &&
                        binding.edtDesc.text.toString().trim().isNotEmpty() -> {
                            val body =  AddAdviceRequest(
                                binding.edtTitle.text.toString(),
                                type,
                                binding.edtDesc.text.toString().trim()
                            )
                    viewModel.addAdvice(accessKey, body)
                        }
            }
        }
    }

    private fun setObserver() {
        viewModel.addAdviceResult.observe(this) {
            when(it) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Berhasil mengirimkan $type", Toast.LENGTH_SHORT).show()
                    finish()
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
        } else {
            binding.pg.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}