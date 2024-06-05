package kr.co.company.capstone.service

import kr.co.company.capstone.dto.notification.NotificationDetailResponse
import kr.co.company.capstone.dto.notification.NotificationResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationService {
    // 단건 알림 조회 (알림 하나 클릭 했을 때)
    @GET("/notifications/{notificationId}")
    fun loadSpecificNotificationInformation(@Path("notificationId") notificationId: Long): Call<NotificationDetailResponse>

    // 알림 조회
    @GET("/notifications")
    fun loadNotifications(): Call<NotificationResponse>

    // 추가 알림 조회
    @GET("/notifications")
    fun loadAdditionalNotifications(
        @Query("cursorId") cursorId : Long,
        @Query("size") size : Int
    ): Call<NotificationResponse>

    companion object {
        fun service(): NotificationService{
            return RetrofitBuilder.getRetrofit().create(NotificationService::class.java)
        }
    }
}
