package kr.co.company.capstone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import kr.co.company.capstone.R;
import kr.co.company.capstone.util.SharedPreferenceUtil;
import kr.co.company.capstone.Theme;

public class SplashActivity extends AppCompatActivity {
    private String jwtToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        SharedPreferenceUtil.removeKey(this, "jwtToken");
        jwtToken = SharedPreferenceUtil.getString(this, "accessToken");

        //로딩화면 시작.
        LoadingStart();
    }
    private void LoadingStart(){
        Handler handler=new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                if(jwtToken.isEmpty()) {
                    SharedPreferenceUtil.removeKey(getApplicationContext(), "accessToken");
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                // context 는 생명주기 타고 getapplicationcontext는 싱글턴
                //result ok 인지 체크해서 맞으면 보여주고
                else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    Theme.applyTheme(SharedPreferenceUtil.getString(getApplicationContext(), "scnMode"));
                }
            }
        },2000);
    }
}