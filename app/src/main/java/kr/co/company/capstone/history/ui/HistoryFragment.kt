package kr.co.company.capstone.history.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kr.co.company.capstone.databinding.FragmentHistoryBinding
import kr.co.company.capstone.dto.history.HistoryGoalInfo
import kr.co.company.capstone.fragment.ErrorDialogFragment
import kr.co.company.capstone.history.contract.HistoryContract
import kr.co.company.capstone.history.presenter.HistoryPresenter
import kr.co.company.capstone.util.SharedPreferenceUtil

class HistoryFragment : Fragment(), HistoryContract.View {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: HistoryPresenter
    private val adapter: HistoryAdapter by lazy {
        HistoryAdapter(mutableListOf()) { goalId ->
            onDetailClick(goalId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = HistoryPresenter(this)


        setupRecyclerView()
        presenter.loadHistoryGoals()
    }

    override fun showHistoryGoals(goals: List<HistoryGoalInfo>) {
        updateHistoryUI(goals.isEmpty()) // 히스토리 존재 여부에 따른 UI 처리
        if (goals.isNotEmpty()) {
            adapter.updateGoals(goals)
        }
    }

    private fun updateHistoryUI(isEmpty: Boolean) {
        binding.historyEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.historyRecycler.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun setupRecyclerView() {
        binding.historyRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@HistoryFragment.adapter
        }
    }

    // 자세히 보기 클릭시
    private fun onDetailClick(goalId: Long) {
        presenter.getUserIdForGoal(goalId, { SharedPreferenceUtil.getString(requireContext(), "nickname") }) { userId ->
            if (userId != null) {
                val action = HistoryFragmentDirections.actionNavigationHistoryToTimeLineFragment(
                    goalId = goalId,
                    userId = userId
                )
                findNavController().navigate(action)
            } else {
                showError("유저 정보를 가져오지 못했습니다.")
            }
        }
    }

    override fun showError(message: String) {
        val errorDialog = ErrorDialogFragment.getErrorMessage(message)
        errorDialog.show(childFragmentManager, "error_dialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

