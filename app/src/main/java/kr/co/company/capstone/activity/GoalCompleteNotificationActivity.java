// Android 관련 패키지 import
package kr.co.company.capstone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

// 프로젝트 내부 패키지 import
import kr.co.company.capstone.R;
import kr.co.company.capstone.dto.NotificationDetailResponse;

/*
    앱 진입 후 사용자의 목표 수행 최종 완료한 내용이 있다면 실행되는 액티비티
 */

public class GoalCompleteNotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_clear_my_goal);

        // 성공한 목표 ID
        Intent intent = getIntent();
        NotificationDetailResponse info = (NotificationDetailResponse) intent.getSerializableExtra("info");

        // 뷰 객체 참조
        TextView completeTitle = findViewById(R.id.complete_title);
        TextView completeBody = findViewById(R.id.complete_body);

        // 뷰 객체에 정보 설정
        completeTitle.setText(info.getTitle());
        completeBody.setText(info.getContent());

        Button toMainPage = findViewById(R.id.to_main_page);

        // 메인화면 이동
        toMainPage.setOnClickListener(view -> {
            onBackPressed();
            finish();
        });
    }
}