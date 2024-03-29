package kr.co.company.capstone.service;

import kr.co.company.capstone.dto.notification.NotificationInfoListResponse;
import kr.co.company.capstone.dto.notification.NotificationDetailListResponse;
import kr.co.company.capstone.dto.notification.NotificationDetailResponse;
import kr.co.company.capstone.dto.notification.NotificationInfoResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NotificationService {

    // 단건 알림 조회 (알림 하나 클릭 했을 때)
    @GET("/notifications/{notificationId}")
    Call<NotificationDetailResponse> findNotification(@Path("notificationId") long notificationId);

    // 유저별 알림 조회
    @GET("/notifications")
    Call<NotificationInfoListResponse<NotificationInfoResponse>> notifications(@Query("cursorId") Long cursorId, @Query("size") int size);

    // 목표 수행 완료 알림 조회
    @GET("/notifications/goal-complete")
    Call<NotificationDetailListResponse> findGoalCompleteNotifications();

    static NotificationService getService(){
        return RetrofitBuilder.getRetrofit().create(NotificationService.class);
    }
}
