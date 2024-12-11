package kr.co.company.capstone.timeline.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kr.co.company.capstone.databinding.FragmentTimelineBinding
import kr.co.company.capstone.dto.timeline.PostResponse
import kr.co.company.capstone.fragment.ErrorDialogFragment.Companion.getErrorMessage
import kr.co.company.capstone.timeline.contract.TimeLineContract
import kr.co.company.capstone.timeline.model.TimeLineRepositoryImpl
import kr.co.company.capstone.timeline.presenter.TimeLinePresenter

class TimeLineFragment : Fragment(), TimeLineContract.View, SwipeRefreshLayout.OnRefreshListener  {
    private var _binding : FragmentTimelineBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter : TimeLineContract.Presenter
    private lateinit var timeLineAdapter : TimeLineAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val args: TimeLineFragmentArgs by navArgs() // Safe Args로 전달된 인자


    private val userId: Long by lazy { args.userId }
    private val goalId: Long by lazy { args.goalId }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = TimeLinePresenter(this, TimeLineRepositoryImpl())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimelineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 해당 목표 정보
        presenter.loadGoalInfo(goalId)
    }

    override fun showPosts(postResponse: PostResponse) {
        binding.timeLineGoalTitle.text = postResponse.goalTitle
        timeLineAdapter.addPosts(postResponse.posts)
        stopRefreshing()
    }

    override fun showNoPosts() {
        stopRefreshing()
    }

    override fun initUi() {
        timeLineAdapter = TimeLineAdapter(mutableListOf(), presenter::onLikeButtonClicked, userId)

        swipeRefreshLayout = binding.swipeLayout
        swipeRefreshLayout.setOnRefreshListener(this)

        // RecyclerView 설정
        binding.timeLineRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = timeLineAdapter
            // 스크롤 리스너 : 리스트 최하단 도달 시 추가 로딩
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (!recyclerView.canScrollVertically(1)) {
                        presenter.loadPosts()
                    }
                }
            })
        }
        binding.timeLineRecyclerView.post {
            if (!binding.timeLineRecyclerView.canScrollVertically(1)) {
                presenter.loadPosts()
            }
        }

    }

    override fun showErrorDialog(errorMessage: String) {
        val errorDialogFragment = getErrorMessage(errorMessage)
        errorDialogFragment.show(parentFragmentManager, "ErrorDialogFragment")
    }

    override fun stopRefreshing() {
        swipeRefreshLayout.isRefreshing = false
    }

    override fun updateLikeStatus(postId: Long, isLiked: Boolean) {
        val post = timeLineAdapter.getPosts().find { it.postId == postId }
        post?.let {
            if (isLiked) {
                it.likedUserIds.add(userId)
            } else {
                it.likedUserIds.remove(userId)
            }
            timeLineAdapter.notifyItemChanged(timeLineAdapter.getPosts().indexOf(it))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRefresh() {
        timeLineAdapter.clearPosts()
        presenter.refresh()
    }
}
