package kr.co.company.capstone.timeline.model

import kr.co.company.capstone.dto.goal.GoalDetail
import kr.co.company.capstone.dto.timeline.GoalDateInfo
import kr.co.company.capstone.dto.timeline.PostResponse
import kr.co.company.capstone.service.GoalInquiryService
import kr.co.company.capstone.service.LikeService
import kr.co.company.capstone.service.PostInquiryService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TimeLineRepositoryImpl : TimeLineRepository {

    private val goalInquiryService = GoalInquiryService.getService()
    private val postService = PostInquiryService.getService()
    private val likeService = LikeService.getService()
    override fun getGoalInfo(goalId: Long, callback: TimeLineRepository.GetGoalInfoCallback) {
        val call = goalInquiryService.goalDetail(goalId)
        call.enqueue(object : Callback<GoalDetail> {
            override fun onResponse(call: Call<GoalDetail>, response: Response<GoalDetail>) {
                if (response.isSuccessful) {
                    val goalDetail = response.body()
                    if (goalDetail != null) {
                        val goalDateInfo = GoalDateInfo(
                            startDate = goalDetail.startDate,
                            endDate = goalDetail.endDate,
                            weekDays = goalDetail.weekDays.toCharArray().map { it.toString() }
                        )
                        callback.onSuccess(goalDateInfo)
                    } else {
                        callback.onFailure()
                    }
                } else {
                    callback.onFailure()
                }
            }

            override fun onFailure(call: Call<GoalDetail>, t: Throwable) {
                callback.onFailure()
            }
        })
    }

    override fun getPosts(goalId: Long, date: String, callback: TimeLineRepository.GetPostsCallback) {
        val call = postService.getPosts(goalId, date)
        call.enqueue(object : Callback<PostResponse> {
            override fun onResponse(call: Call<PostResponse>, response: Response<PostResponse>) {
                if (response.isSuccessful) {
                    val postResponse = response.body()
                    if (postResponse != null && postResponse.posts.isNotEmpty()) {
                        callback.onSuccess(postResponse)
                    } else {
                        callback.onFailure()
                    }
                } else {
                    callback.onFailure()
                }
            }

            override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                callback.onFailure()
            }
        })
    }

    override fun likePost(goalId: Long, postId: Long, callback: TimeLineRepository.LikeCallback) {
        likeService.like(goalId, postId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    callback.onSuccess()
                } else {
                    callback.onFailure()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                callback.onFailure()
            }
        })
    }

    override fun unlikePost(goalId: Long, postId: Long, callback: TimeLineRepository.LikeCallback) {
        likeService.unlike(goalId, postId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    callback.onSuccess()
                } else {
                    callback.onFailure()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                callback.onFailure()
            }
        })
    }
}
