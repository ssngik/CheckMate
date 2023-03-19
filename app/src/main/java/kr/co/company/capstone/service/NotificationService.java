package kr.co.company.capstone.service;

import kr.co.company.capstone.dto.NotificationInfoListResponse;
import kr.co.company.capstone.dto.NotificationDetailListResponse;
import kr.co.company.capstone.dto.NotificationDetailResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NotificationService {

    // 단건 알림 조회
    @GET("/notifications/{notificationId}")
    Call<NotificationDetailResponse> findNotification(@Path("notificationId") long notificationId);

    // 유저별 알림 조회
    @GET("/notifications")
    Call<NotificationInfoListResponse> notifications(@Query("cursorId") Long cursorId);

    // 목표 수행 완료 알림 조회
    @GET("/notifications/goal-complete")
    Call<NotificationDetailListResponse> findGoalCompleteNotifications();

    static NotificationService getService(){
        return RetrofitBuilder.getRetrofit().create(NotificationService.class);
    }
}
