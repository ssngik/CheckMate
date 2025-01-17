package kr.co.company.capstone.createGoal.first.ui

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.co.company.capstone.R
import kr.co.company.capstone.databinding.LayoutCategoryPickerBottomSheetBinding
import kr.co.company.capstone.util.picker.PickerUtil

class CustomCategoryPickerBottomSheet (
    private val onCategorySelected: (category: String) -> Unit
): BottomSheetDialogFragment() {
    private var _binding: LayoutCategoryPickerBottomSheetBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheet = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme)
        _binding = LayoutCategoryPickerBottomSheetBinding.inflate(LayoutInflater.from(requireContext()))
        bottomSheet.setContentView(binding.root)

        initView()
        return bottomSheet
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initView() {
        PickerUtil.setupNumberPicker(binding.spinnerCategory, resources.getStringArray(R.array.Category_Spinner))

        binding.btnTimePickerOk.setOnClickListener {
            val selectedCategory = binding.spinnerCategory
                .displayedValues[binding.spinnerCategory.value]
            onCategorySelected(selectedCategory)
            dismiss()
        }

        binding.btnXClose.setOnClickListener { dismiss() }
    }
}