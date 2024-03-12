package kr.co.company.capstone.fragment

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import kr.co.company.capstone.R
import kr.co.company.capstone.databinding.FragmentMainPageBinding
import kr.co.company.capstone.dto.ErrorMessage
import kr.co.company.capstone.dto.goal.GoalInfoListResponse
import kr.co.company.capstone.dto.goal.OngoingGoalInfoResponse
import kr.co.company.capstone.dto.goal.TodayGoalInfoResponse
import kr.co.company.capstone.dto.goal.UserWeeklySchedule
import kr.co.company.capstone.service.GoalInquiryService
import kr.co.company.capstone.util.SharedPreferenceUtil
import kr.co.company.capstone.util.adapter.OngoingGoalRecyclerViewAdapter
import kr.co.company.capstone.util.adapter.TodayGoalRecyclerViewAdapter
import lombok.NoArgsConstructor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

@NoArgsConstructor
class MainPageFragment : Fragment() {

    private var _binding: FragmentMainPageBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainPageBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰 초기화
        initView()

        callOngoingGoalsApi()
        callTodayGoalsApi()
    }

    // 뷰 초기화
    private fun initView() {
        val userName = SharedPreferenceUtil.getString(context, "nickName")
        val greetingText = "$userName 님\n오늘도 파이팅하세요!"
        binding.currentWeek.text = getCurrentWeekString()

        val spannable = SpannableString(greetingText)
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.checkmate_color)),
            0, userName.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE // 왼쪽, 오른쪽 제거
        )
        binding.mainGreeting.text = spannable
    }

    private fun callTodayGoalsApi() {
        GoalInquiryService.getService().todayGoals()
            .enqueue(object : Callback<GoalInfoListResponse<TodayGoalInfoResponse?>?> {
                override fun onResponse(
                    call: Call<GoalInfoListResponse<TodayGoalInfoResponse?>?>,
                    response: Response<GoalInfoListResponse<TodayGoalInfoResponse?>?>
                ) {
                    if (response.isSuccessful) {
                        val goalInfoListResponse = response.body()

                        if (goalInfoListResponse != null) {
                            val goals = goalInfoListResponse.goals
                            val todayRecyclerView = binding.recyclerToday
                            todayRecyclerView.adapter = TodayGoalRecyclerViewAdapter(context, goals)
                            todayRecyclerView.layoutManager = LinearLayoutManager(context)
                        }
                    } else {
                        Log.d(LOG_TAG, "Error in callTodayGoalApi " + ErrorMessage.getErrorByResponse(response))
                    }
                }

                override fun onFailure(
                    call: Call<GoalInfoListResponse<TodayGoalInfoResponse?>?>,
                    t: Throwable
                ) {
                    Log.d(LOG_TAG, "fail to load todayGoals " + t.message)
                }
            })
    }

//    // 진행중인 목표
    private fun callOngoingGoalsApi() {
        GoalInquiryService.getService().ongoingGoals()
            .enqueue(object : Callback<GoalInfoListResponse<OngoingGoalInfoResponse?>?> {
                override fun onResponse(
                    call: Call<GoalInfoListResponse<OngoingGoalInfoResponse?>?>,
                    response: Response<GoalInfoListResponse<OngoingGoalInfoResponse?>?>
                ) {
                    if (response.isSuccessful) {
                        val goalInfoListResponse = response.body()
                        if (goalInfoListResponse != null) {
                            val goals = goalInfoListResponse.goals
                            val ongoinGoalRecyclerView = binding.recyclerOngoing
                            ongoinGoalRecyclerView.adapter = OngoingGoalRecyclerViewAdapter(context, goals)
                            ongoinGoalRecyclerView.layoutManager = LinearLayoutManager(context)
                        }
                    } else {
                        val onErrorFragment = OnErrorFragment()
                        onErrorFragment.show(childFragmentManager, "error")
                    }
                }

                override fun onFailure(
                    call: Call<GoalInfoListResponse<OngoingGoalInfoResponse?>?>,
                    t: Throwable
                ) {
                    Log.d(LOG_TAG, "fail to load ongoingGoals " + t.message)
                }
            })
    }

    fun getWeekNumber(): Int {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.SUNDAY // 일요일이 주의 첫

        return calendar.get(Calendar.WEEK_OF_MONTH)
    }

    fun getCurrentWeekString() : String{
        val currentWeekNum = getWeekNumber()
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        return "$currentYear 년  $currentMonth 월 $currentWeekNum 주차"
    }
    companion object {
        private const val LOG_TAG = "MainPageFragment"
    }
}