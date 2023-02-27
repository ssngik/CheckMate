package kr.co.company.capstone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.company.capstone.R;
import kr.co.company.capstone.dto.NotificationDetailResponse;
import kr.co.company.capstone.fragment.GoalDetailFragment;
import lombok.SneakyThrows;

import java.util.HashMap;


public class GoalCompleteNotificationActivity extends AppCompatActivity {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_clear_my_goal);

        Intent intent = getIntent();
        NotificationDetailResponse info = (NotificationDetailResponse) intent.getSerializableExtra("info");

        TextView completeTitle = findViewById(R.id.complete_title);
        TextView completeBody = findViewById(R.id.complete_body);

        completeTitle.setText(info.getTitle());
        completeBody.setText(info.getBody());

        Button toMainPage = findViewById(R.id.to_main_page);

        toMainPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
    }
}
