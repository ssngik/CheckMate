package kr.co.company.capstone.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker
import kr.co.company.capstone.R
import kr.co.company.capstone.databinding.FragmentCreateGoalInfoBinding
import kr.co.company.capstone.dto.goal.GoalDetailResponse
import kr.co.company.capstone.dto.team_mate.TeamMatesResponse


class CreateNewGoalFirstPage : Fragment() {

    // 바인딩 객체 타입 null을 허용  ( onDestroy 시 완벽 제거 )
    private var _binding: FragmentCreateGoalInfoBinding? = null

    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = _binding!!

    private lateinit var goal: GoalDetailResponse<TeamMatesResponse>

    override fun onCreate(savedInstanceState: Bundle?) {
        if (arguments != null) {
            val bundle = arguments!!
            goal =
                bundle.getSerializable("goalDetailResponse") as GoalDetailResponse<TeamMatesResponse>
        }

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateGoalInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 목표 이름 Text Changed 리스너 설정
        setGoalTitleEditTextListener()

        // 스피너 초기화
        initializeSpinner()

        // 스피너 리스너 설정
        setSpinnerOnItemSelectedListener()

        // MaterialDatepicker 초기화 / 리스너 설정
        setDatepicker()
    }

    // 목표 이름 Text Changed 리스너 설정
    private fun setGoalTitleEditTextListener() {
        binding.title.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setButtonEnabledIfAllSet()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    // 스피너 리스너 설정
    private fun setSpinnerOnItemSelectedListener() {
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                setButtonEnabledIfAllSet()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    // 스피너 초기화
    private fun initializeSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.Category_Spinner,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinner.adapter = adapter
        }
    }

    // MaterialDatepicker 초기화 / 리스너 설정
    private fun setDatepicker() {
        binding.calendarIcon.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.dateRangePicker().build()
            datePicker.show(parentFragmentManager, "MaterialDatePicker")

            datePicker.addOnPositiveButtonClickListener {
                onDateSelected(datePicker.headerText)
                binding.datePickerData.setTextAppearance(R.style.fill_text)
                setButtonEnabledIfAllSet()
            }
        }
    }

    // 날짜 선택을 처리하는 함수
    private fun onDateSelected(selectedDate: String) {
        binding.datePickerData.text = selectedDate
    }

    // 필드 입력에 따른 버튼 활성화 변경
    private fun setButtonEnabledIfAllSet() {
        val fieldsAreSet = areRequiredFields()
        val drawable = if (fieldsAreSet) {
            R.drawable.btn_main
        } else {
            R.drawable.btn_main_disabled
        }

        setButtonDrawable(drawable, fieldsAreSet)
    }

    // 모든 필드 상태 확인
    private fun areRequiredFields(): Boolean {

        // 목표 이름을 작성하지 않은 경우
        if (binding.title.text.isEmpty()) {
            return false
        }
        // 목표 카테고리를 선택하지 않은 경우
        if (binding.spinner.selectedItem == null) {
            return false
        }
        // 날짜를 선택하지 않은 경우
        if (binding.datePickerData.text == "2023.00.00 ~ 2023.00.00") {
            return false
        }

        return true
    }

    // 필드 입력에 따른 버튼 색상 변경
    private fun setButtonDrawable(drawableRes: Int, isEnabled: Boolean) {
        val drawable = ResourcesCompat.getDrawable(resources, drawableRes, null)
        binding.saveBtn.background = drawable
        binding.saveBtn.isEnabled = isEnabled
    }


    override fun onDestroyView() {
        // binding class 인스턴스 참조 정리
        _binding = null
        super.onDestroyView()
    }


}