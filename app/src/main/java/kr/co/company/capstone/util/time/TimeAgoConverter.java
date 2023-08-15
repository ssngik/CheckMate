package kr.co.company.capstone.util.time;


import android.os.Build;
import androidx.annotation.RequiresApi;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class TimeAgoConverter {


    public static String formatTimeAgo(String datetime) {

        LocalDateTime notificationTime = LocalDateTime.parse(datetime, DateTimeFormatter.ISO_DATE_TIME); // ISO_DATE_TIME 형식으로 변환
        LocalDateTime currentTime = LocalDateTime.now(); // 현재 시간
        Duration duration = Duration.between(notificationTime, currentTime); // 알림이 온 시간과 현재 시간과의 차이

        long minutes = duration.toMinutes();

        if (minutes < 1) {
            return "방금 전";
        } else if (minutes < 60) {
            return minutes + "분 전";
        } else if (minutes < 1440) {
            return minutes / 60 + "시간 전";
        } else {
            return minutes / 1440 + "일 전";
        }
    }

}


