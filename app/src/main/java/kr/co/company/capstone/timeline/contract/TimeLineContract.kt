package kr.co.company.capstone.timeline.contract

import kr.co.company.capstone.dto.timeline.PostResponse

interface TimeLineContract {
    interface View {
        fun initUi()
        fun showPosts(postResponse: PostResponse)
        fun showNoPosts()
        fun showErrorDialog(errorMessage: String)
        fun stopRefreshing()
        fun updateLikeStatus(postId: Long, isLiked: Boolean)
    }

    interface Presenter {
        fun loadGoalInfo(goalId: Long)
        fun loadPosts()
        fun refresh()
        fun onLikeButtonClicked(postId: Long, isLiked : Boolean)
    }
}