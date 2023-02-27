package kr.co.company.capstone;

import android.app.Application;

import android.content.Context;
import com.kakao.sdk.common.KakaoSdk;

public class GlobalApplication extends Application {
    private static GlobalApplication instance;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        GlobalApplication.context = getApplicationContext();
        String string = getResources().getString(R.string.kakao_native_key);
        KakaoSdk.init(this, string);
    }

    public static Context getAppContext() {
        return GlobalApplication.context;
    }
}