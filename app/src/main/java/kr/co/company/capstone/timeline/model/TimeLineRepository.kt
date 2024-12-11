package kr.co.company.capstone.timeline.model

import kr.co.company.capstone.dto.timeline.GoalDateInfo
import kr.co.company.capstone.dto.timeline.PostResponse

interface TimeLineRepository {
    fun getGoalInfo(goalId: Long, callback: GetGoalInfoCallback)
    fun getPosts(goalId: Long, date: String, callback: GetPostsCallback)
    fun likePost(goalId: Long, postId: Long, callback: LikeCallback)
    fun unlikePost(goalId: Long, postId: Long, callback: LikeCallback)


    interface GetGoalInfoCallback {
        fun onSuccess(goalDateInfo: GoalDateInfo)
        fun onFailure()
    }

    interface GetPostsCallback {
        fun onSuccess(response: PostResponse)
        fun onFailure()
    }

    interface LikeCallback {
        fun onSuccess()
        fun onFailure()
    }
}

