package kr.co.company.capstone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import kr.co.company.capstone.R;
import kr.co.company.capstone.util.SharedPreferenceUtil;
import kr.co.company.capstone.Theme;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // accessToken 가져오기
        String accessToken = SharedPreferenceUtil.getString(this, "accessToken");

        //로딩화면 시작.
        LoadingStart(accessToken);
    }
    private void LoadingStart(String accessToken){
        Handler handler=new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                if(accessToken.isEmpty()) { // accessToken 만료
                    SharedPreferenceUtil.removeKey(getApplicationContext(), "accessToken");

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                    Log.d("splash", "loading done !");
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    // 화면 테마 적용
                    Theme.applyTheme(SharedPreferenceUtil.getString(getApplicationContext(), "scnMode"));
                }
            }
        },2000);
    }
}