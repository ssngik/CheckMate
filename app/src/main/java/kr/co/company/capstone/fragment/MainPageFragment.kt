package kr.co.company.capstone.fragment

import OngoingGoalRecyclerViewAdapter
import TodayGoalRecyclerViewAdapter
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kr.co.company.capstone.R
import kr.co.company.capstone.databinding.FragmentMainPageBinding
import kr.co.company.capstone.dto.ErrorMessage
import kr.co.company.capstone.dto.goal.GoalInfoListResponse
import kr.co.company.capstone.dto.goal.OngoingGoalInfoResponse
import kr.co.company.capstone.dto.goal.TodayGoalInfoResponse
import kr.co.company.capstone.service.GoalInquiryService
import kr.co.company.capstone.util.NavigationUtil
import kr.co.company.capstone.util.SharedPreferenceUtil
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
        initView()
        setupButtonClickListener()
        fetchTodayGoals() // 오늘 진행할 목표 RecyclerView
        fetchOngoingGoals() // 진행 중인 목표 RecyclerView
    }

    private fun setupButtonClickListener() {
        binding.setNewGoalBtn.setOnClickListener {
            NavigationUtil.navigateTo(binding.root, R.id.action_navigation_home_to_createNewGoalFirstPage) }
    }

    // 뷰 초기화
    private fun initView() {
        // 사용자 이름
        val userName = SharedPreferenceUtil.getString(context, "nickName")
        // 인사 문구 설정
        val greetingText = "$userName 님\n오늘도 파이팅하세요!"
        val spannable = SpannableString(greetingText)
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.checkmate_color)),
            0, userName.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE // 왼쪽, 오른쪽 제거
        )
        binding.mainGreeting.text = spannable
        binding.currentWeek.text = getCurrentWeekString()
    }

    private fun fetchTodayGoals() {
        GoalInquiryService.getService().todayGoals()
            .enqueue(object : Callback<GoalInfoListResponse<TodayGoalInfoResponse>> {
                override fun onResponse(
                    call: Call<GoalInfoListResponse<TodayGoalInfoResponse>>,
                    response: Response<GoalInfoListResponse<TodayGoalInfoResponse>>
                ) {
                    if (response.isSuccessful) {
                        val todayGoals = response.body()?.goals ?: emptyList()
                        setupTodayGoalsAdapter(todayGoals)
                    } else {
                        showErrorFragment(ErrorMessage.getErrorByResponse(response).toString())
                    }
                }
                override fun onFailure(call: Call<GoalInfoListResponse<TodayGoalInfoResponse>>, t: Throwable) {
                    showErrorFragment("네트워크 오류: ${t.message}")
                }
            })
    }

    private fun setupTodayGoalsAdapter(todayGoals: List<TodayGoalInfoResponse>) {
        val todayGoalsAdapter = TodayGoalRecyclerViewAdapter(todayGoals)
        binding.recyclerToday.adapter = todayGoalsAdapter
        binding.recyclerToday.layoutManager = LinearLayoutManager(context)

        todayGoalsAdapter.itemClick = object : TodayGoalRecyclerViewAdapter.ItemClick {
            override fun onClick(view: View, position: Int, goalId: Long) {
                val goalIdBundle = Bundle().apply { putLong("goalId", goalId) }
                NavigationUtil.navigateTo(
                    binding.root,
                    R.id.action_navigation_home_to_goalDetailFragment,
                    goalIdBundle
                )
            }
        }
    }

    // 진행중인 목표
    private fun fetchOngoingGoals() {
        GoalInquiryService.getService().ongoingGoals()
            .enqueue(object : Callback<GoalInfoListResponse<OngoingGoalInfoResponse>> {
                override fun onResponse(
                    call: Call<GoalInfoListResponse<OngoingGoalInfoResponse>>,
                    response: Response<GoalInfoListResponse<OngoingGoalInfoResponse>>
                ) {
                    if (response.isSuccessful) {
                        setupOngoingGoalsAdapter(response)
                    } else {
                        showErrorFragment(ErrorMessage.getErrorByResponse(response).toString())
                    }
                }

                override fun onFailure(call: Call<GoalInfoListResponse<OngoingGoalInfoResponse>>, t: Throwable) {
                    showErrorFragment("네트워크 오류: ${t.message}")
                }
            })
    }

    private fun setupOngoingGoalsAdapter(response: Response<GoalInfoListResponse<OngoingGoalInfoResponse>>) {
        val ongoingGoals = response.body()?.goals ?: emptyList()
        val ongoingGoalsAdapter = OngoingGoalRecyclerViewAdapter(ongoingGoals)
        binding.recyclerOngoing.adapter = ongoingGoalsAdapter
        binding.recyclerOngoing.layoutManager = LinearLayoutManager(context)

        ongoingGoalsAdapter.itemClick = object : OngoingGoalRecyclerViewAdapter.ItemClick {
                override fun onClick(view: View, position: Int, goalId: Long) {
                    val goalIdBundle = Bundle().apply { putLong("goalId", goalId) }
                    NavigationUtil.navigateTo(binding.root, R.id.action_navigation_home_to_goalDetailFragment, goalIdBundle)
                }
            }
    }

    private fun showErrorFragment(errorMessage : String) {
        val errorDialog = ErrorDialogFragment.newInstance(errorMessage)
        errorDialog.show(childFragmentManager, "error_dialog")
    }

    private fun getWeekNumber(): Int {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.SUNDAY // 일요일이 주의 첫 요일

        return calendar.get(Calendar.WEEK_OF_MONTH)
    }

    private fun getCurrentWeekString(): String {
        val currentWeekNum = getWeekNumber()
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        return "$currentYear 년  $currentMonth 월 $currentWeekNum 주차"
    }

    companion object {
        private const val LOG_TAG = "MainPageFragment"
    }
}