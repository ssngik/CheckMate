package kr.co.company.capstone.service;

import kr.co.company.capstone.dto.NotificationInfoListResponse;
import kr.co.company.capstone.dto.NotificationDetailListResponse;
import kr.co.company.capstone.dto.NotificationDetailResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NotificationService {
    @GET("/notification/{notificationId}")
    Call<NotificationDetailResponse> findNotification(@Path("notificationId") long notificationId);

    @GET("/notification")
    Call<NotificationInfoListResponse> notifications(@Query("cursorId") Long cursorId);

    @GET("/notification/goal-complete")
    Call<NotificationDetailListResponse> findGoalCompleteNotifications();

    static NotificationService getService(){
        return RetrofitBuilder.getRetrofit().create(NotificationService.class);
    }
}
