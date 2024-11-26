package kr.co.company.capstone.service

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Bundle
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.NotificationTarget
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kr.co.company.capstone.R
import kr.co.company.capstone.main.MainActivity
import kr.co.company.capstone.splash.SplashActivity
import java.net.URLDecoder

class FirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID = "general_channel"
        private const val CHANNEL_NAME = "general Notifications"
        var fcmToken: String = ""
    }
    init {
        // fcm token 초기화
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                fcmToken = task.result
            }
        }
    }

    // 새로운 FCM Token이 생성되었을 때 호출
    override fun onNewToken(token: String) {
        fcmToken = token
    }

    // FCM 메시지를 수신했을 때 호출
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            showNotification(remoteMessage.data)
        }
    }


    private fun showNotification(data: Map<String, String>) {
        val uniqueId = (System.currentTimeMillis() and 0xfffffffL).toInt()

        val title = data["title"] ?: "No Title" // 알림 제목
        val message = data["body"] ?: "No Message" // 알림 내용
        val notificationId = data["notificationId"]?.toLongOrNull() ?: 0L // 알림 ID
        val userId = data["userId"]?.toLongOrNull() ?: 0L // 사용자 ID
        val goalId = data["goalId"]?.toLongOrNull() ?: 0L // 목표 ID
        val type = data["type"] // 알림 타입

        // 앱 실행 여부 확인
        val isAppRunning = isAppRunning()

        // 알림 클릭 시 실행될 인텐트 생성
        val intent = createIntent(message, notificationId, goalId, userId, type, isAppRunning)

        // 알림 클릭 시 PendingIntent 생성
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            uniqueId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 알림 생성 및 커스텀 뷰 적용
        val notificationBuilder = createNotificationBuilder(title, message, pendingIntent)
        val notificationManager = createNotificationManager()
        val contentView = createCustomDesign(title, message)
        notificationBuilder.setContent(contentView)

        // Glide로 이미지 로드
        loadImageIntoNotification(contentView, notificationBuilder, uniqueId)

        // 표시/
        notificationManager.notify(uniqueId, notificationBuilder.build())
    }

    private fun createIntent(
        message: String,
        notificationId: Long,
        goalId: Long,
        userId: Long,
        type: String?,
        isAppRunning: Boolean
    ): Intent {
        // 실행 상태에 따른 Activity 이동
        return Intent(applicationContext, if (isAppRunning) MainActivity::class.java else SplashActivity::class.java).apply {
            putExtras(createCommonBundle(message, notificationId, goalId, userId, type))
            addFlags(if (isAppRunning) Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            else Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    private fun createCommonBundle(
        message: String,
        notificationId: Long,
        goalId: Long,
        userId: Long,
        type: String?
    ): Bundle {
        return Bundle().apply {
            putString("messageBody", message)
            putLong("notificationId", notificationId)
            putLong("goalId", goalId)
            putLong("userId", userId)
            putString("navigateTo", type)
        }
    }


    private fun createCustomDesign(title: String, message: String): RemoteViews {
        return RemoteViews(packageName, R.layout.notification).apply {
            setTextViewText(R.id.noti_title, URLDecoder.decode(title, "UTF-8"))
            setTextViewText(R.id.noti_message, URLDecoder.decode(message, "UTF-8"))
            setImageViewResource(R.id.logo, R.drawable.attendance_icon)
        }
    }

    // 이미지 로드 및 알림 적용
    private fun loadImageIntoNotification(
        contentView: RemoteViews,
        notificationBuilder: NotificationCompat.Builder,
        notificationId: Int
    ) {
        Glide.with(applicationContext)
            .asBitmap()
            .circleCrop()
            .load(R.drawable.attendance_icon)
            .into(
                NotificationTarget(
                    applicationContext,
                    R.id.logo,
                    contentView,
                    notificationBuilder.build(),
                    notificationId
                )
            )
    }



    private fun createNotificationManager(): NotificationManager {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        return notificationManager
    }

    private fun createNotificationBuilder(
        title: String,
        message: String,
        pendingIntent: PendingIntent
    ): NotificationCompat.Builder {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_icon_foreground)
            .setContentTitle(URLDecoder.decode(title, "UTF-8"))
            .setContentText(URLDecoder.decode(message, "UTF-8"))
            .setAutoCancel(true)
            .setSound(uri)
            .setContentIntent(pendingIntent)
    }

    // 앱 실행 여부 확인
    private fun isAppRunning(): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager

        // 현재 포어그라운드 확인
        return activityManager.runningAppProcesses.any { processInfo ->
            processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    processInfo.processName == packageName
        }
    }
}
