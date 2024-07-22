package kr.co.company.capstone.createGoal.first

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.datepicker.MaterialDatePicker
import kr.co.company.capstone.R
import kr.co.company.capstone.databinding.FragmentCreateNewGoalFirstPageBinding


class CreateGoalFirstPageFragment : Fragment(), CreateGoalFirstPageContract.View{

    private var _binding: FragmentCreateNewGoalFirstPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter : CreateGoalFirstPageContract.Presenter
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

        presenter = CreateGoalFirstPagePresenter(this, CreateGoalFirstPageModel())

        // Listener 초기화
        initListener()

        // 목표 이름 Text Changed 리스너 설정
        setGoalTitleEditTextListener()

        // 스피너 초기화
        initSpinner()

        // 스피너 리스너 설정
        setSpinnerOnItemSelectedListener()
    }


    private fun initListener(){
        binding.calendarIcon.setOnClickListener { showDatePicker() }
        binding.btnNext.setOnClickListener { presenter.onNextButtonClicked() }
    }

    // 목표 이름 Text Changed 리스너 설정
    private fun setGoalTitleEditTextListener() {
        binding.inputTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { presenter.onTitleTextChanged() }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    // 필드 입력에 따른 버튼 색상 변경
    override fun setButtonDrawable(drawableRes: Int, isEnabled: Boolean) {
        val drawable = ResourcesCompat.getDrawable(resources, drawableRes, null)
        binding.btnNext.background = drawable
        binding.btnNext.isEnabled = isEnabled
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.dateRangePicker().build()
        datePicker.show(parentFragmentManager, "MaterialDatePicker")

        datePicker.addOnPositiveButtonClickListener {dateRange ->
            binding.selectedDate.setTextAppearance(R.style.create_goal_page_contents)
            presenter.onDateSelected(Pair(dateRange.first, dateRange.second))
        }
    }

    // 입력된 목표 이름
    override fun getEnteredTitle(): String = binding.inputTitle.text.toString()
    // 선택된 기간
    override fun getSelectedDate(): String = binding.selectedDate.text.toString()
    // 선택된 카테고리
    override fun getSelectedCategory(): String = binding.spinner.selectedItem.toString()

    // 선택된 날짜 UI 표시
    override fun setSelectedDate(selection: Pair<String, String>) {
        val selectedRangeDate = "${selection.first} ~ ${selection.second}"
        binding.selectedDate.text = selectedRangeDate
    }

    // 스피너 리스너 설정
    private fun setSpinnerOnItemSelectedListener() {
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                if (position != 0){
                    presenter.onCategorySelected()
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    // 스피너 초기화
    private fun initSpinner() {
        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.Category_Spinner).toMutableList()
        ){
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                if (position == 0) {
                    view.setTextAppearance(R.style.hint_text)
                    view.setTextColor(ContextCompat.getColor(context, R.color.hint))
                }else{
                    view.setTextAppearance(R.style.create_goal_page_contents)
                }
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                if (position == 0){
                    view.visibility = View.GONE
                }else{
                    view.visibility = View.VISIBLE
                }
                return view
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter
        binding.spinner.setSelection(0)
    }

    override fun navigateToSecondPage(
        startDate: String,
        endDate: String,
        category: String,
        title: String
    ) {
            val firstPageBundle = Bundle().apply {
                putString("startDate", startDate)
                putString("endDate", endDate)
                putString("category", binding.spinner.selectedItem.toString())
                putString("title", binding.inputTitle.text.toString())
            }
            Navigation.findNavController(binding.root).navigate(R.id.action_createGoalFirstPage_to_createGoalFinalPage, firstPageBundle)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}
