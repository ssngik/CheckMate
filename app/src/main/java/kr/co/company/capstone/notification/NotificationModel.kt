package kr.co.company.capstone.notification

import kr.co.company.capstone.dto.notification.NotificationResponse
import kr.co.company.capstone.service.NotificationService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationModel : NotificationContract.NotificationModel{
    override fun callLoadAllUserNotificationsApi(
        onSuccess: (NotificationResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        NotificationService.service().loadAllUserNotifications().enqueue(object : Callback<NotificationResponse>{
            override fun onResponse(
                call: Call<NotificationResponse>,
                response: Response<NotificationResponse>
            ) {
                if (response.isSuccessful){
                    val notificationsResponse = response.body()!!
                    onSuccess(notificationsResponse)
                }else{
                    onFailure("알림 정보를 불러올 수 없습니다.")
                }
            }

            override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                onFailure("문제가 발생했습니다. 다시 시도해 주세요.")
            }

        })
    }

}