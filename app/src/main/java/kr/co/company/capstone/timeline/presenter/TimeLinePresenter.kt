package kr.co.company.capstone.timeline.presenter

import android.util.Log
import kr.co.company.capstone.dto.timeline.GoalDateInfo
import kr.co.company.capstone.dto.timeline.PostResponse
import kr.co.company.capstone.timeline.contract.TimeLineContract
import kr.co.company.capstone.timeline.model.TimeLineRepository
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TimeLinePresenter(
    private val view: TimeLineContract.View,
    private val repository: TimeLineRepository
) : TimeLineContract.Presenter {
    private var goalId: Long = 0L
    private lateinit var goalInfo: GoalDateInfo
    private lateinit var startDate: LocalDate
    private lateinit var endDate: LocalDate
    private var currentDate: LocalDate = LocalDate.now()
    private var isLoading = false

    override fun loadGoalInfo(goalId: Long) {
        this.goalId = goalId
        repository.getGoalInfo(goalId, object : TimeLineRepository.GetGoalInfoCallback {
            override fun onSuccess(goalDateInfo: GoalDateInfo) {
                goalInfo = goalDateInfo

                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                startDate = LocalDate.parse(goalInfo.startDate, formatter)
                endDate = LocalDate.parse(goalInfo.endDate, formatter)
                currentDate = if (LocalDate.now().isAfter(endDate)) endDate else LocalDate.now()

                view.initUi()
            }

            override fun onFailure() {
                view.showErrorDialog("목표 정보를 가져오는데 실패했어요.")
            }
        })
    }

    override fun loadPosts() {
        if (isLoading) return
        isLoading = true

        // 날짜 범위 제한
        if (currentDate.isBefore(startDate)) {
            isLoading = false
            return
        }

        val dayOfWeek = getDayOfWeek(currentDate)
        if (goalInfo.weekDays.contains(dayOfWeek)) {
            val dateStr = currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))

            repository.getPosts(goalId, dateStr, object : TimeLineRepository.GetPostsCallback {
                override fun onSuccess(response: PostResponse) {
                    if (response.posts.isEmpty()) {
                        // 해당 날짜에 포스트 없으면 이전 날짜 확인
                        currentDate = currentDate.minusDays(1)
                        isLoading = false
                        loadPosts()
                    } else {
                        // 포스트 표시
                        view.showPosts(response)
                        currentDate = currentDate.minusDays(1)
                        isLoading = false
                    }
                }

                override fun onFailure() {
                    // 목표 인증 요일이 아닐 경우 날짜만 변경 후 재요청
                    currentDate = currentDate.minusDays(1)
                    isLoading = false
                    loadPosts()
                }
            })
        } else { // 목표 인증 요일이 아닌 경우
            currentDate = currentDate.minusDays(1)
            isLoading = false
            loadPosts()
        }
    }

    private fun getDayOfWeek(date: LocalDate): String {
        return when (date.dayOfWeek) {
            DayOfWeek.MONDAY -> "월"
            DayOfWeek.TUESDAY -> "화"
            DayOfWeek.WEDNESDAY -> "수"
            DayOfWeek.THURSDAY -> "목"
            DayOfWeek.FRIDAY -> "금"
            DayOfWeek.SATURDAY -> "토"
            DayOfWeek.SUNDAY -> "일"
            else -> ""
        }
    }

    override fun refresh() {
        // currentDate 초기화
        currentDate = if (LocalDate.now().isAfter(endDate)) endDate else LocalDate.now()
        // 게시물 로드
        loadPosts()
    }

    override fun onLikeButtonClicked(postId: Long, isLiked: Boolean) {
        // 좋아요 버튼 클릭 처리
        if (isLiked) {
            repository.unlikePost(goalId, postId, object: TimeLineRepository.LikeCallback {
                override fun onSuccess() {
                    view.updateLikeStatus(postId, false)
                }

                override fun onFailure() {
                    view.updateLikeStatus(postId, true)
                }
            })
        }else {
            repository.likePost(goalId, postId, object: TimeLineRepository.LikeCallback {
                override fun onSuccess() {
                    view.updateLikeStatus(postId, true)
                }

                override fun onFailure() {
                    view.updateLikeStatus(postId, false)
                }
            })
        }
    }
}
