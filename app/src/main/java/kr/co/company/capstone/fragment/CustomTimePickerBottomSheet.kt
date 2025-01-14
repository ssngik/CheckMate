package kr.co.company.capstone.fragment

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.co.company.capstone.R
import kr.co.company.capstone.databinding.LayoutBottomSheetTimepickerBinding
import kr.co.company.capstone.util.picker.PickerUtil

class CustomTimePickerBottomSheet(
    private val onTimeSelected: (selectedAmPm: Int, hour :Int, minute: Int) -> Unit
): BottomSheetDialogFragment() {
    private var _binding: LayoutBottomSheetTimepickerBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheet = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme)
        _binding = LayoutBottomSheetTimepickerBinding.inflate(LayoutInflater.from(requireContext()))
        bottomSheet.setContentView(binding.root)

        // NumberPicker 설정
        initView()
        return bottomSheet
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initView() {
        PickerUtil.setupNumberPicker(binding.spinnerAmPm, arrayOf("오전", "오후"))
        PickerUtil.setupNumberPicker(
            binding.spinnerHour,
            (1..12).map { it.toString() }.toTypedArray()
        )
        PickerUtil.setupNumberPicker(binding.spinnerMinute, arrayOf("00", "30"))

        binding.btnTimePickerOk.setOnClickListener {
            val selectedAmPm = binding.spinnerAmPm.value // 0: 오전, 1: 오후
            val selectedHour = binding.spinnerHour // 시간 1 ~ 12
                .displayedValues[binding.spinnerHour.value]
                .toInt()
            val selectedMinute = // // 선택된 분
                binding.spinnerMinute.displayedValues[binding.spinnerMinute.value].toInt()

            val hour24Format = convertTo24HourFormat(selectedAmPm, selectedHour)
            onTimeSelected(selectedAmPm, hour24Format, selectedMinute)
            dismiss()
        }

        // x 버튼 클릭
        binding.btnXClose.setOnClickListener { dismiss() }
    }

    private fun convertTo24HourFormat(selectedAmPm: Int, selectedHour: Int): Int {
        return if (selectedAmPm == 1) { // 오후
            if (selectedHour == 12) 12 else selectedHour + 12
        } else { // 오전
            if (selectedHour == 12) 0 else selectedHour
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}