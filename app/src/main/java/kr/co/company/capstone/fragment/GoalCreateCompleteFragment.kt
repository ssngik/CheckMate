package kr.co.company.capstone.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import kr.co.company.capstone.R
import kr.co.company.capstone.databinding.FragmentGoalCreationCompleteBinding
import kr.co.company.capstone.dto.goal.MakeGoalRequest
import java.io.Serializable

class GoalCreateCompleteFragment : Fragment() {

    private var _binding: FragmentGoalCreationCompleteBinding? = null

    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGoalCreationCompleteBinding.inflate(inflater, container, false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 목표 관련 데이터 UI상 표시
        showGoalInfo()
        // 친구 초대 버튼 클릭
        binding.btnInviteFriend.setOnClickListener { actionToInvitePage() }
        // 메인화면으로 이동 버튼 클릭
        binding.actionToMainPage.setOnClickListener { actionToMainPage() }
    }

    private fun showGoalInfo() {

        val goalInfo = arguments?.bundleSerializable("makeGoalRequest", MakeGoalRequest::class.java)

        goalInfo?.let {
            with(binding){
                contentCategory.text = it.category
                contentTitle.text = it.title
                contentTime.text = it.appointmentTime
                contentWeekdays.text = sortCheckDays(it.checkDays)
                val rangeDate = "${it.startDate} ~ ${it.endDate}"
                contentPeriod.text = rangeDate
                // TODO: 인증시간 상의 후 제거
            }
        }
    }

    // 요일 정렬
    private fun sortCheckDays(days : String): String{
        val order = mapOf(
            '일' to 1,
            '월' to 2,
            '화' to 3,
            '수' to 4,
            '목' to 5,
            '금' to 6,
            '토' to 7
        )
        val sortedDays = days.toCharArray().sortedBy { order[it] }
        return sortedDays.joinToString(",")
    }

    // Serializable deprecated
    fun <T: Serializable> Bundle.bundleSerializable(key: String, clazz: Class<T>): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.getSerializable(key, clazz)
        } else {
            this.getSerializable(key) as T?
        }
    }

    // 친구 초대 버튼 클릭
    private fun actionToInvitePage() {
        val bundle = Bundle().apply { this.putLong("goalId", arguments?.getLong("goalId") ?: 0L) }
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_goalCreateCompleteActivity_to_fragmentInviteUser, bundle)
    }

    // 메인화면으로 이동 버튼 클릭
    private fun actionToMainPage() {
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_goalCreateCompleteActivity_to_navigation_home)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}