package kr.co.company.capstone.createGoal.first.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import kr.co.company.capstone.R
import kr.co.company.capstone.createGoal.first.contract.CreateGoalFirstPageContract
import kr.co.company.capstone.createGoal.first.model.CreateGoalFirstPageModel
import kr.co.company.capstone.createGoal.first.presenter.CreateGoalFirstPagePresenter
import kr.co.company.capstone.databinding.FragmentCreateNewGoalFirstPageBinding


class CreateGoalFirstPageFragment : Fragment(), CreateGoalFirstPageContract.View {

    private var _binding: FragmentCreateNewGoalFirstPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter : CreateGoalFirstPageContract.Presenter
    private lateinit var categoryPicker: CustomCategoryPickerBottomSheet

    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState)}
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateNewGoalFirstPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = CreateGoalFirstPagePresenter(CreateGoalFirstPageModel())
        presenter.attachView(this)


        initCategoryPicker()
        initListener() // Listener 초기화
    }


    private fun initListener(){
        binding.calendarIcon.setOnClickListener { showDatePicker() } // 날짜 선택 버튼 클릭
        binding.cateogryDropdown.setOnClickListener { showCategoryPicker() } // 카테고리 버튼 클릭
        binding.btnNext.setOnClickListener { presenter.onNextButtonClicked() } // 다음 버튼 클릭

        binding.inputTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                presenter.onTitleChanged(s.toString())
                updateTitleStyle(s) // 표시될 문자열 스타일 지정
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }


    // 필드 입력에 따른 버튼 색상 변경
    override fun setButtonEnabled(isEnabled: Boolean) {
        binding.btnNext.isEnabled = isEnabled
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.dateRangePicker().build()
        datePicker.show(parentFragmentManager, "MaterialDatePicker")

        datePicker.addOnPositiveButtonClickListener { dateRange ->
            presenter.onDateSelected(Pair(dateRange.first, dateRange.second))
        }
    }

    // 선택된 날짜 UI 표시
    override fun setSelectedDate(selection: Pair<String, String>) {
        binding.selectedDate.text = "${selection.first} ~ ${selection.second}"
        updateDateStyle(selection)
    }

    // Category Picker 초기화
    private fun initCategoryPicker() {
        categoryPicker = CustomCategoryPickerBottomSheet { selectedCategory ->
            presenter.onCategorySelected(selectedCategory)
            binding.selectedCategory.text = selectedCategory
            updateCategoryStyle(selectedCategory.isNotBlank())
        }
    }

    private fun showCategoryPicker() {
        categoryPicker.show(parentFragmentManager, "CategoryPicker")
    }

    override fun navigateToSecondPage(
        startDate: String,
        endDate: String,
        category: String,
        title: String
    ) {
        val action = CreateGoalFirstPageFragmentDirections.actionCreateGoalFirstPageFragmentToCreateGoalFinalPageFragment(
            startDate = startDate,
            endDate = endDate,
            category = category,
            title = title
        )
        findNavController().navigate(action)
    }

    // 제목 스타일 업데이트
    private fun updateTitleStyle(input: CharSequence?) {
        updateTextStyle(binding.inputTitle, !input.isNullOrBlank())
    }

    // 카테고리 스타일 업데이트
    private fun updateCategoryStyle(isSelected: Boolean) {
        updateTextStyle(binding.selectedCategory, isSelected)
    }

    // 날짜 스타일 업데이트
    private fun updateDateStyle(selection: Pair<String, String>) {
        val isDateSelected = selection.first.isNotBlank() && selection.second.isNotBlank()
        updateTextStyle(binding.selectedDate, isDateSelected)
    }

    private fun updateTextStyle(view: TextView, isInputValid: Boolean) {
        if (isInputValid) {
            view.setTextAppearance(R.style.create_goal_page_contents)
        } else {
            view.setTextAppearance(R.style.hint_text)
        }
    }

    override fun onDestroyView() {
        presenter.detachView()
        _binding = null
        super.onDestroyView()
    }
}
