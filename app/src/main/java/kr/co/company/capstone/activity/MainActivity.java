package kr.co.company.capstone.activity;

// Android 라이브러리 import
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

// androidx 라이브러리 import
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

// Google 라이브러리 import
import com.google.android.material.bottomnavigation.BottomNavigationView;

// Java 라이브러리 import
import java.util.List;

// kr.co.company.capstone 패키지 내 클래스 import
import kr.co.company.capstone.R;
import kr.co.company.capstone.dto.ErrorMessage;
import kr.co.company.capstone.dto.NotificationDetailListResponse;
import kr.co.company.capstone.dto.NotificationDetailResponse;
import kr.co.company.capstone.service.NotificationService;

// org.jetbrains.annotations 라이브러리 import
import org.jetbrains.annotations.NotNull;

// retrofit2 라이브러리 import
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom);

        setUpNavigation();

        // 애러 다이얼로그 생성
        AlertDialog alertDialog = createOnErrorDialog();

        // 사용자 목표 수행 완료 알림이 있다면 받아옴
        NotificationService.getService().findGoalCompleteNotifications()
                .enqueue(new Callback<NotificationDetailListResponse>() {
                    @Override
                    public void onResponse(Call<NotificationDetailListResponse> call, Response<NotificationDetailListResponse> response) {
                        notificationExistOrNot(response);
                    }
                    @Override
                    public void onFailure(Call<NotificationDetailListResponse> call, Throwable t) {
                        showOnErrorDialog(alertDialog);
                    }
                });
    }

    // AlertDialog 생성, 설정
    @NotNull
    private AlertDialog createOnErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("띵").setMessage("오류가 발생했습니다.").setPositiveButton("넵", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
        return builder.create();
    }

    // 생성한 AlertDialog를 보여줌
    private void showOnErrorDialog(AlertDialog alertDialog){
        alertDialog.show();
    }

    private void notificationExistOrNot(Response<NotificationDetailListResponse> response) {
        if (response.isSuccessful()) {
            List<NotificationDetailResponse> notifications = response.body().getNotifications();

            // 받아야 할 목표 수행 완료 알림이 있다면
            if (notifications.size() > 0) {
                Intent intent = new Intent(getApplicationContext(), GoalCompleteNotificationActivity.class);
                intent.putExtra("info", notifications.get(0));
                startActivity(intent);
            }
        } else {
            Log.d(LOG_TAG, "in notification , error code : " + response.code());
            Log.d(LOG_TAG, ErrorMessage.getErrorByResponse(response).toString());
        }
    }

    private void setUpNavigation() {
        BottomNavigationView navView = findViewById(R.id.bottomNav_view);

        // AppBarConfiguration 설정
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_history, R.id.navigation_alarm, R.id.settingPageFragment).build();

        // NavController 초기화
        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);

        // NavController와 BottomNavigationView 연결
        NavigationUI.setupWithNavController(navView, navController);


    }

}