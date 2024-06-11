package kr.co.company.capstone.service;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import kr.co.company.capstone.fragment.TimeLineFragment;
import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Objects;

import kr.co.company.capstone.R;
import kr.co.company.capstone.invitationReplyDialog.InvitationReplyDialogFragment;
import lombok.SneakyThrows;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String LOG_TAG = "MsgService";
    private int notificationId;
    public static String fcmToken;
    PendingIntent pendingIntent = null;
    Intent intent = null;

    @SneakyThrows
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(LOG_TAG, "From: " + remoteMessage.getFrom());
        Log.d(LOG_TAG, "remoteMessage.getData().size() : " + remoteMessage.getData());

        if (remoteMessage.getData().size() > 0) {
            showNotification(remoteMessage.getData());
        }
    }

    private void showNotification(Map<String, String> data) throws UnsupportedEncodingException {
        int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
        Bundle bundle = new Bundle();

        String title = data.get("title");
        String message = data.get("body");
        String notificationId = data.get("notificationId");

        ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

        intent = new Intent(getApplicationContext(), cn.getClass());
        boolean flag = false;
        if(Objects.equals(data.get("type"), "INVITE_GOAL")){
            flag = true;
            intent = new Intent(getApplicationContext(), InvitationReplyDialogFragment.class);
            bundle.putString("messageBody", message);
        }
        else if(Objects.equals(data.get("type"), "POST_UPLOAD")){
            flag = true;
            intent = new Intent(this, TimeLineFragment.class);
        }

        bundle.putLong("notificationId", Long.parseLong(notificationId));
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if(flag) pendingIntent = PendingIntent.getActivity(getApplicationContext(), iUniqueId, intent, PendingIntent.FLAG_IMMUTABLE);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = getNotificationBuilder(uri, title, message);
        RemoteViews content = getCustomDesign(title, message);
        notificationBuilder = notificationBuilder.setContent(content);

        NotificationManager notificationManager = getNotificationManager(uri);
        MyFirebaseMessagingService.this.notificationId = (int) System.currentTimeMillis();

        NotificationTarget notificationTarget = new NotificationTarget(getApplicationContext(),
                R.id.logo, content, notificationBuilder.build(), MyFirebaseMessagingService.this.notificationId);
        Glide.with(getApplicationContext()).asBitmap().circleCrop().load(R.drawable.attendance_icon).into(notificationTarget);
        notificationManager.notify(MyFirebaseMessagingService.this.notificationId, notificationBuilder.build());
    }

    private NotificationManager getNotificationManager(Uri uri) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(uri, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        return notificationManager;
    }

    @NotNull
    private NotificationCompat.Builder getNotificationBuilder(Uri uri, String title, String message) throws UnsupportedEncodingException {
        return new NotificationCompat.Builder(getApplicationContext(), "channel_id")
                .setSmallIcon(R.drawable.ic_icon_foreground)
                .setContentTitle(URLDecoder.decode(title, "UTF-8"))
                .setContentText(URLDecoder.decode(message, "UTF-8"))
                .setAutoCancel(true)
                .setSound(uri)
                .setContentIntent(pendingIntent);
    }

    public MyFirebaseMessagingService() {
        super();
        boolean gotFBCrash = false;
        try {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (task.isSuccessful()) {
                                fcmToken = task.getResult();
                                Log.d(LOG_TAG, "FCM token: " + fcmToken);
                            }
                        }
                    });
        } catch (Exception e) {
            Log.w(LOG_TAG, "Fetching FCM registration token failed");
            gotFBCrash = true;
        }
        if(gotFBCrash){
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (task.isSuccessful()) {
                                fcmToken = task.getResult();
                                Log.d(LOG_TAG, "FCM token: " + fcmToken);
                            }
                        }
                    });
        }
    }

    @Override
    public void onNewToken(String token){
        Log.d(LOG_TAG, "Refreshed FCM token: "+token);
        fcmToken = token;
    }

    private RemoteViews getCustomDesign(String title, String message) throws UnsupportedEncodingException {
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.noti_title, URLDecoder.decode(title,"UTF-8"));
        remoteViews.setTextViewText(R.id.noti_message, URLDecoder.decode(message,"UTF-8"));
//        remoteViews.setImageViewResource(R.id.logo, R.drawable.bell);
        remoteViews.setImageViewResource(R.id.logo, R.drawable.attendance_icon);
        return remoteViews;
    }

}
