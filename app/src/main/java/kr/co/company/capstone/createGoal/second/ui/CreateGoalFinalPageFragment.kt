package kr.co.company.capstone.createGoal.second.ui

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kr.co.company.capstone.createGoal.second.contract.CreateGoalFinalContract
import kr.co.company.capstone.createGoal.second.model.GoalCreateRepositoryImpl
import kr.co.company.capstone.createGoal.second.presenter.CreateGoalFinalPresenter
import kr.co.company.capstone.databinding.FragmentCreateGoalFinalPageBinding
import kr.co.company.capstone.dto.goal.MakeGoalRequest
import kr.co.company.capstone.fragment.ErrorDialogFragment
import kr.co.company.capstone.service.GoalInquiryService
import java.util.Calendar

class CreateGoalFinalPageFragment : Fragment(), CreateGoalFinalContract.View {

    private var _binding: FragmentCreateGoalFinalPageBinding? = null

    private val binding get() = _binding!!

    private val args: CreateGoalFinalPageFragmentArgs by navArgs()

    private lateinit var presenter : CreateGoalFinalContract.Presenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = GoalCreateRepositoryImpl(GoalInquiryService.getService())
        presenter = CreateGoalFinalPresenter(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateGoalFinalPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.attachView(this)
        initListener()

        // 요일 선택 체크박스
        initWeekdaysCheckBox()
    }

    // 요일 선택 체크박스
    private fun initWeekdaysCheckBox() {
        val checkBoxes = listOf(
            binding.monday, binding.tuesday, binding.wednesday, binding.thursday,
            binding.friday, binding.saturday, binding.sunday
        )
        for (checkbox in checkBoxes) {
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                val day = checkbox.text.toString()
                presenter.onWeekdayChecked(day, isChecked)
            }
        }
    }

    private fun initListener() {
        binding.clockTime.setOnClickListener { initTimePickerDialog() }

        // 목표 생성 버튼
        binding.btnGoalCreationComplete.setOnClickListener {
            val title = args.title
            val category = args.category
            val startDate = args.startDate
            val endDate = args.endDate

            presenter.onCreateGoalButtonClicked(title, category, startDate, endDate)
        }
    }

    private fun initTimePickerDialog(){
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerListener = TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
            presenter.onTimeSelected(selectedHour, selectedMinute)
            binding.timeText.text = String.format("%02d:%02d", selectedHour, selectedMinute)
        }
        TimePickerDialog(
            requireContext(),
            timePickerListener,
            hour,
            minute,
            false
        ).show()
    }

    // 목표 생성 버튼 상태 설정 ( Selector 사용)
    override fun setButtonState(isEnabled: Boolean) {
        binding.btnGoalCreationComplete.isEnabled = isEnabled
    }

    override fun navigateToGoalCreationComplete(goalRequest : MakeGoalRequest, goalId: Long) {
        val action = CreateGoalFinalPageFragmentDirections
            .actionCreateGoalFinalPageFragmentToGoalCreateCompleteFragment(goalRequest, goalId)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        presenter.detachView()
        _binding = null
        super.onDestroyView()
    }

    override fun showError(errorMessage: String) {
        val errorDialog = ErrorDialogFragment.getErrorMessage(errorMessage)
        errorDialog.show(childFragmentManager, "error_dialog")
    }
}