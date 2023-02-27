package kr.co.company.capstone.activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

import kr.co.company.capstone.R;
import kr.co.company.capstone.dto.ErrorMessage;
import kr.co.company.capstone.dto.NotificationDetailListResponse;
import kr.co.company.capstone.dto.NotificationDetailResponse;
import kr.co.company.capstone.service.NotificationService;
import org.jetbrains.annotations.NotNull;
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
        AlertDialog alertDialog = setOnErrorDialog();

        NotificationService.getService().findGoalCompleteNotifications()
                .enqueue(new Callback<NotificationDetailListResponse>() {
                    @Override
                    public void onResponse(Call<NotificationDetailListResponse> call, Response<NotificationDetailListResponse> response) {
                        notificationExistOrNot(response);

                    }
                    @Override
                    public void onFailure(Call<NotificationDetailListResponse> call, Throwable t) {
                        Log.d(LOG_TAG, t.getMessage());
                        alertDialog.show();
                    }
                });
    }

    @NotNull
    private AlertDialog setOnErrorDialog() {
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

    private void notificationExistOrNot(Response<NotificationDetailListResponse> response) {
        if (response.isSuccessful()) {
            List<NotificationDetailResponse> notifications = response.body().getNotifications();
            if (notifications.size() > 0) {
                Intent intent = new Intent(getApplicationContext(), GoalCompleteNotificationActivity.class);
                intent.putExtra("info", notifications.get(0));
                startActivity(intent);
            }
        } else {
            Log.d(LOG_TAG, "error code : " + response.code());
            Log.d(LOG_TAG, ErrorMessage.getErrorByResponse(response).toString());
        }
    }

    private void setUpNavigation() {
        BottomNavigationView navView = findViewById(R.id.bottomNav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_history, R.id.navigation_alarm, R.id.settingPageFragment).build();

        //Initialize NavController.
        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navView, navController);


    }

}