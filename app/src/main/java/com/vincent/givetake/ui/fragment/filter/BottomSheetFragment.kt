package com.vincent.givetake.ui.fragment.filter

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.vincent.givetake.R
import com.vincent.givetake.databinding.FragmentBottomSheetBinding
import com.vincent.givetake.factory.ItemsPrefViewModelFactory
import com.vincent.givetake.factory.PrefViewModelFactory
import com.vincent.givetake.preference.FilterData
import com.vincent.givetake.preference.UserPreferences

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class BottomSheetFragment(val filterData: FilterData) : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var viewModel: BottomSheetViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = UserPreferences.getInstance(requireActivity().dataStore)
        val factory = PrefViewModelFactory.getInstance(pref)
        viewModel = ViewModelProvider(this, factory)[BottomSheetViewModel::class.java]

        categoryAdapter = CategoryAdapter()
        val categoryData = requireActivity().resources.getStringArray(R.array.kategori).toList()
        categoryAdapter.setData(categoryData)

        binding.rvCategory.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        categoryAdapter.index = filterData.index
        categoryAdapter.notifyDataSetChanged()

        binding.edtMaxRadius.setText(filterData.radius)

        binding.btnApply.setOnClickListener {
            if (categoryAdapter.index == -1) {
                Snackbar.make(binding.txtError, "Kategori wajib dipilih", Snackbar.LENGTH_LONG).show()
            }
            if (binding.edtMaxRadius.text.toString().isEmpty()) {
                binding.edtMaxRadius.error = "Batas radius harus diisi"
            }
            if (categoryAdapter.index != -1 && binding.edtMaxRadius.text.toString().isNotEmpty()) {
                val data = FilterData(
                    binding.edtMaxRadius.text.toString(),
                    categoryAdapter.index,
                    categoryData[categoryAdapter.index]
                )
                viewModel.saveFilter(data)
                dismiss()
            }
        }

        binding.btnReset.setOnClickListener {
            viewModel.reset()
            dismiss()
        }
    }
}