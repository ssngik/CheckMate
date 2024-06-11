package kr.co.company.capstone.home

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
import kr.co.company.capstone.dto.goal.OngoingGoalInfoResponse
import kr.co.company.capstone.dto.goal.TodayGoalInfoResponse
import kr.co.company.capstone.fragment.ErrorDialogFragment
import kr.co.company.capstone.util.NavigationUtil

class MainPageFragment : Fragment(), MainPageContract.MainView {

    private var _binding: FragmentMainPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter : MainPageContract.Presenter
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

        presenter = MainPagePresenter(MainPageModel())
        presenter.attachView(this)
        presenter.onViewCreated()
    }

    // 뷰 초기화
    override fun initView() {
        // 사용자 이름, 서버 복구 후 SharedPreference 저장 정보 사용
        val userName = "상익"
//        val userName = SharedPreferenceUtil.getString(context, "nickName")
        // 인사 문구 설정
        val greetingText = "$userName 님\n오늘도 파이팅하세요!"
        val spannable = SpannableString(greetingText)
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.checkmate_color)),
            0, userName.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE // 왼쪽, 오른쪽 제거
        )
        binding.mainGreeting.text = spannable
    }

    override fun showProgress() {
        binding.loading.show()
        binding.mainPageGroup.visibility = View.INVISIBLE
    }

    override fun hideProgress() {
        binding.loading.hide()
        binding.mainPageGroup.visibility = View.VISIBLE
    }

    // 새 목표 만들기
    override fun navigateToNewGoalPage() {
        NavigationUtil.navigateTo(binding.root, R.id.action_navigation_home_to_createNewGoalFirstPage)
    }

    override fun setUpBtnClickListener() {
        binding.setNewGoalBtn.setOnClickListener {
            presenter.onSetNewGoalButtonClick()
        }
    }

    // 오늘 진행할 목표
    override fun fetchTodayGoals(result: List<TodayGoalInfoResponse>) {
        setupTodayGoalsAdapter(result)
    }

    // 진행 중인 목표
    override fun fetchOngoingGoals(result : List<OngoingGoalInfoResponse>) {
        setupOngoingGoalsAdapter(result)
    }

    override fun showError(errorMessage: String) {
        val errorDialog = ErrorDialogFragment.getErrorMessage(errorMessage)
        errorDialog.show(childFragmentManager, "error_dialog")
    }

    private fun setupTodayGoalsAdapter(todayGoals: List<TodayGoalInfoResponse>) {
        val todayGoalsAdapter = TodayGoalRecyclerViewAdapter(todayGoals)
        binding.recyclerToday.adapter = todayGoalsAdapter
        binding.recyclerToday.layoutManager = LinearLayoutManager(context)

        todayGoalsAdapter.itemClick = object : TodayGoalRecyclerViewAdapter.ItemClick {
            override fun onClick(view: View, position: Int, goalId: Long) {
                val goalIdBundle = Bundle().apply { putLong("goalId", goalId) }
                NavigationUtil.navigateTo(binding.root, R.id.action_navigation_home_to_goalDetailFragment, goalIdBundle)
            }
        }
    }

    private fun setupOngoingGoalsAdapter(ongoingGoals: List<OngoingGoalInfoResponse>){
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
    companion object {
        private const val LOG_TAG = "MainPageFragment"
    }
}