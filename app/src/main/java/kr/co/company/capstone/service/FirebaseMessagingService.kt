package kr.co.company.capstone.service

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
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

        val title = data["title"] ?: "No Title"
        val message = data["body"] ?: "No Message"
        val notificationId = data["notificationId"]?.toLongOrNull() ?: 0L
        val type = data["type"]

        // 앱 실행 여부 확인
        val isAppRunning = isAppRunning()

        // 알림 클릭 시 실행될 Intent 생성
        val intent = if (isAppRunning) {
            createMainIntent(message, notificationId, type)
        } else {
            createSplashIntent(message, notificationId, type)
        }

        // PendingIntent 생성
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            uniqueId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Notification Builder, Manager 초기화
        val notificationBuilder = createNotificationBuilder(title, message, pendingIntent)
        val notificationManager = createNotificationManager()

        // 알림 커스텀 뷰 설정
        val contentView = createCustomDesign(title, message)
        notificationBuilder.setContent(contentView)

        // Glide로 이미지 로드
        loadImageIntoNotification(contentView, notificationBuilder, uniqueId)

        // 표시/
        notificationManager.notify(uniqueId, notificationBuilder.build())
    }

    private fun createCustomDesign(title: String, message: String): RemoteViews {
        return RemoteViews(packageName, R.layout.notification).apply {
            setTextViewText(R.id.noti_title, URLDecoder.decode(title, "UTF-8"))
            setTextViewText(R.id.noti_message, URLDecoder.decode(message, "UTF-8"))
            setImageViewResource(R.id.logo, R.drawable.attendance_icon)
        }
    }

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

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

    private fun setNavigateTo(type: String?): String {
        return when (type) {
            "INVITE_SEND" -> "INVITE_SEND"
            "POST_UPLOAD" -> "TimeLine"
            else -> "Default"
        }
    }

    private fun createMainIntent(message: String, notificationId: Long, type: String?): Intent {
        return Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("messageBody", message)
            putExtra("notificationId", notificationId)
            putExtra("navigateTo", setNavigateTo(type))
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
    }

    private fun createSplashIntent(message: String, notificationId: Long, type: String?): Intent {
        return Intent(applicationContext, SplashActivity::class.java).apply {
            putExtra("messageBody", message)
            putExtra("notificationId", notificationId)
            putExtra("navigateTo", setNavigateTo(type))
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    private fun isAppRunning(): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        return activityManager.runningAppProcesses.any { it.processName == packageName }
    }

}
