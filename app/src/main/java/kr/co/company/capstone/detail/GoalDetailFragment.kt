package kr.co.company.capstone.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kr.co.company.capstone.R
import kr.co.company.capstone.databinding.FragmentGoalDetailBinding
import kr.co.company.capstone.dto.goal.GoalCalendar
import kr.co.company.capstone.dto.goal.GoalDetail
import kr.co.company.capstone.dto.goal.Mate
import kr.co.company.capstone.fragment.ErrorDialogFragment
import kr.co.company.capstone.util.FragmentUtil
import kr.co.company.capstone.util.SharedPreferenceUtil
import kr.co.company.capstone.util.adapter.CalendarRecyclerViewAdapter
import kr.co.company.capstone.util.adapter.TeamMateRecyclerViewAdapter

class GoalDetailFragment : Fragment(), GoalDetailContract.DetailView {
    private var _binding : FragmentGoalDetailBinding? = null
    private lateinit var presenter : GoalDetailContract.Presenter
    private val fragmentUtil = FragmentUtil()
    private val binding get() = _binding!!
    private var goalId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) { goalId = arguments?.getLong("goalId")!! }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalDetailBinding.inflate(inflater, container, false)
        presenter = GoalDetailPresenter(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.loadGoalDetailViewInformation(goalId)
        initListener()
    }

    // 함께 하는 팀원 Recyclerview 초기화
    private fun fetchTeamMateInformation(mates : List<Mate>){
        val teamMatesAdapter = TeamMateRecyclerViewAdapter(mates)
        binding.teamMateList.adapter = teamMatesAdapter
        binding.teamMateList.layoutManager = LinearLayoutManager(context)
    }

    // mateId
    private fun getMateId(): Long? {
        return getUserMateInfo()?.mateId
    }

    // userId
    private fun getUserId(): Long? {
        return getUserMateInfo()?.userId
    }

    private fun getUserMateInfo(): Mate? {
        val mates = presenter.getMates()
        val userNickname = SharedPreferenceUtil.getString(context, "nickname")
        return mates.find { it.nickname == userNickname }
    }

    private fun initListener() {
        binding.btnDoMyGoal.setOnClickListener {
            val myMateId = getMateId()
            val userId = getUserId()

            if (myMateId != null && userId != null) {
                val action = GoalDetailFragmentDirections.actionGoalDetailFragmentToAlbumFragment(
                    goalId = goalId,
                    userId = userId,
                    mateId = myMateId,
                    title = binding.goalTitle.text.toString()
                )
                findNavController().navigate(action)
            }else {
                showError("문제가 발생했습니다.")
            }

        }
        binding.btnInvite.setOnClickListener { fragmentUtil.actionDetailToInvite(binding.root, goalId)}
        binding.btnTimeline.setOnClickListener {
            val userId = getUserId()
            if (userId != null) {
                val action = GoalDetailFragmentDirections.actionGoalDetailFragmentToTimeLineFragment(
                    goalId = goalId,
                    userId = userId
                )
                findNavController().navigate(action)
            } else {
                showError("문제가 발생했습니다.")
            }
        }
    }

    override fun initView(result : GoalDetail) {

        // View 기본 정보 초기화
        with(binding){
            goalTitle.text = result.title // 목표 타이틀
            dayWeek.text = presenter.getWeekOfMonth() // 오늘 기준 주차
            textPercent.text = getString(R.string.progress_percent, result.achievementPercent)
        }

        // 함께 하는 팀원 목록
        fetchTeamMateInformation(result.mates)

        // 목표 진행 여부에 따른 버튼 상태 설정
        presenter.fetchDoButtonStatus(result.uploadable)

        // 목표 진행 캘린더 초기화
        presenter.fetchCalendarViewInformation(result)
    }

    // 목표 수행 여부에 따른 버튼 동작 상태 정의
    override fun setDoButtonStatus(statusText: String, drawableId: Int) {
        with(binding.btnDoMyGoal) {
            isEnabled = statusText.isEmpty()
            this.text= statusText

            // Drawable 설정
            if (isEnabled) {
                setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableId, 0)
            } else {
                setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }
    }


    // 목표 수행 일정 RecyclerView
    override fun fetchCalendar(calendarList: List<GoalCalendar>) {
        val adapter = CalendarRecyclerViewAdapter(calendarList)
        binding.calendarDateRecyclerview.adapter = adapter
        binding.calendarDateRecyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun showProgress() {
        binding.loading.show()
        binding.goalDetailGroup.visibility = View.INVISIBLE
    }

    override fun hideProgress() {
        binding.loading.hide()
        binding.goalDetailGroup.visibility = View.VISIBLE
    }

    override fun showError(errorMessage: String) {
        val errorDialog = ErrorDialogFragment.getErrorMessage(errorMessage)
        errorDialog.show(childFragmentManager, "error_dialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        presenter.detachView()
    }

}