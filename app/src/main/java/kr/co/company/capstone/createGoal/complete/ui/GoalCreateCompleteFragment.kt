package kr.co.company.capstone.createGoal.complete.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kr.co.company.capstone.createGoal.complete.contract.GoalCreateCompleteContract
import kr.co.company.capstone.databinding.FragmentGoalCreationCompleteBinding
import kr.co.company.capstone.dto.goal.MakeGoalRequest
import kr.co.company.capstone.util.day.DayUtils

class GoalCreateCompleteFragment : Fragment(), GoalCreateCompleteContract.View {
    private val args by navArgs<GoalCreateCompleteFragmentArgs>()

    private var _binding: FragmentGoalCreationCompleteBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter: GoalCreateCompleteContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalCreationCompleteBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UI 초기화 직접 처리
        initUi(args.makeGoalRequest)

        initListener()
    }

    private fun initListener() {
        binding.btnInviteFriend.setOnClickListener {
            navigateToInvitePage(args.goalId)
        }
        binding.actionToMainPage.setOnClickListener {
            navigateToMainPage()
        }
    }


    private fun initUi(goalInfo: MakeGoalRequest) {
        val sortedDays = DayUtils.sortCheckDays(goalInfo.checkDays)
        val period = "${goalInfo.startDate} ~ ${goalInfo.endDate}"

        with(binding) {
            contentCategory.text = goalInfo.category
            contentTitle.text = goalInfo.title
            contentTime.text = goalInfo.appointmentTime
            contentWeekdays.text = sortedDays
            contentPeriod.text = period
        }
    }

    private fun navigateToInvitePage(goalId: Long) {
        val action = GoalCreateCompleteFragmentDirections
            .actionGoalCreateCompleteFragmentToFragmentInviteUser(goalId)
        findNavController().navigate(action)
    }

    private fun navigateToMainPage() {
        val action = GoalCreateCompleteFragmentDirections
            .actionGoalCreateCompleteFragmentToNavigationHome()
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}