package kr.co.company.capstone.fragment

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.Navigation
import kr.co.company.capstone.R
import kr.co.company.capstone.databinding.FragmentCreateNewGoalFinalPageBinding
import kr.co.company.capstone.dto.ErrorMessage
import kr.co.company.capstone.dto.goal.MakeGoalResponse
import kr.co.company.capstone.dto.goal.MakeGoalRequest
import kr.co.company.capstone.service.GoalCreateService
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import java.util.Calendar

class CreateNewGoalFinalPage : Fragment() {

    private var _binding: FragmentCreateNewGoalFinalPageBinding? = null

    private val binding get() = _binding!!

    private val weekDaysList = mutableListOf<String>()
    private lateinit var weekDays : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateNewGoalFinalPageBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 인증시간 Timepicker
        showTimePicker()

        // 요일 선택 체크박스
        setWeekdaysCheckBox()

        // 생성 완료 버튼
        binding.btnGoalCreationComplete.setOnClickListener {
            callMakeGoalRequest()
        }

    }

    // 요일 선택 체크박스
    private fun setWeekdaysCheckBox() {
        val checkBoxes = listOf(
            binding.monday, binding.tuesday, binding.wednesday, binding.thursday,
            binding.friday, binding.saturday, binding.sunday
        )


        for (checkbox in checkBoxes) {
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                val day = checkbox.text.toString()
                if (isChecked) {
                    weekDaysList.add(day)
                } else {
                    weekDaysList.remove(day)
                }
                checkNecessaryInputFields()
            }
        }
    }

    // 필수 입력 항목 체크
    private fun checkNecessaryInputFields():Boolean{

        if (binding.timeText.text.isEmpty() || weekDaysList.isEmpty()) {
            // 버튼 비활성화
            setButtonStatus(R.drawable.btn_main_disabled, false)

            return false
        }

        // 버튼 활성화
        setButtonStatus(R.drawable.btn_main, true)

        // 요일 정보
        weekDays = weekDaysList.joinToString("")

        return true
    }

    // 버튼 상태 변경
    private fun setButtonStatus(drawableRes: Int, isEnabled: Boolean){
        val drawable = ResourcesCompat.getDrawable(resources, drawableRes, null)
        binding.btnGoalCreationComplete.background = drawable
        binding.btnGoalCreationComplete.isEnabled = isEnabled
    }

    // 목표 완료 안내 페이지 이동
    private fun actionToGoalCreationComplete(makeGoalRequest: MakeGoalRequest, goalId:Long){

        if (checkNecessaryInputFields()){

            val finalPageBundle = Bundle().apply{
                putSerializable("makeGoalRequest", makeGoalRequest)
                putLong("goalId", goalId)
            }

            Navigation.findNavController(binding.root).navigate(R.id.action_createNewGoalFinalPage_to_goalCreateCompleteActivity, finalPageBundle)
        }
    }


    private fun callMakeGoalRequest(){
        val title = arguments?.getString("title") ?: ""
        val category = arguments?.getString("category") ?: ""
        val startDate = arguments?.getString("startDate") ?: ""
        val endDate = arguments?.getString("endDate") ?: ""

        val makeGoalRequest = MakeGoalRequest(title, category, startDate, endDate, weekDays, "00:00:00")

        GoalCreateService.getService().saveGoal(makeGoalRequest)
            .enqueue(object : Callback<MakeGoalResponse>{
                override fun onResponse(call: Call<MakeGoalResponse>, response : Response<MakeGoalResponse>){
                    if (response.isSuccessful){
                        actionToGoalCreationComplete(makeGoalRequest, response.body()?.goalId?:0L)
                    }else{
                        // TODO: 다이어로그 디자인 확정시 에러 코드별 에러처리
                        Log.d("day", ErrorMessage.getErrorByResponse(response).code)
                    }
                }
                override fun onFailure(call : Call<MakeGoalResponse>, t:Throwable){
                    Log.d("day", t.message.toString())
                }
            })
    }

    private fun initTimePickerDialog(){
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerListener =
            TimePickerDialog.OnTimeSetListener { p0, p1, p2 -> binding.timeText.text = "${p1}시 ${p2}분"

                checkNecessaryInputFields()
            }
        // TODO: 타임피커 테마 확정되면 변경
        val pickerDialog = TimePickerDialog(context, android.R.style.Theme_Dialog, timePickerListener,
            hour, minute, false)
        pickerDialog.show()
    }

    private fun showTimePicker() {
        binding.clockTime.setOnClickListener {
            initTimePickerDialog()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}