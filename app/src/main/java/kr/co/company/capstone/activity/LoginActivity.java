package kr.co.company.capstone.activity;

// Android Framework 제공 라이브러리 관련
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

// Android library
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

// Google API client imports
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

// Kakao API client imports
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;

// Naver API client imports
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

// Kotlin import
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

// 프로젝트별
import kr.co.company.capstone.R;
import kr.co.company.capstone.dto.ErrorMessage;
import kr.co.company.capstone.dto.login.*;
import kr.co.company.capstone.service.UserService;
import kr.co.company.capstone.util.SharedPreferenceUtil;
import kr.co.company.capstone.service.MyFirebaseMessagingService;

// lombok
import lombok.SneakyThrows;

// 레트로핏 관련
import retrofit2.Call;
import retrofit2.Response;

// JSON 관련
import org.json.JSONException;
import org.json.JSONObject;

// Java library imports
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Objects;
import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    // 로그 태그
    static final String LOG_TAG = "LoginActivity";

    // 네이버 로그인 정보
    private static String OAUTH_CLIENT_ID;
    private static String OAUTH_CLIENT_SECRET;
    private static String OAUTH_CLIENT_NAME;

    // Redirect code
    private static final int REDIRECT_MAIN = 200;
    private static final int REDIRECT_SET_NICKNAME = 201;

    int redirectCode;
    private static OAuthLogin mOAuthLoginInstance;
    private static Context mContext;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton googleLoginButton;
    private final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        // FCM Token 서비스 실행
        Intent fcmToken = new Intent(getApplicationContext(), MyFirebaseMessagingService.class);
        startService(fcmToken);

        // 네이버 로그인 정보 초기화
        OAUTH_CLIENT_ID = String.valueOf(getResources().getString(R.string.OAUTH_CLIENT_ID));
        OAUTH_CLIENT_SECRET = String.valueOf(getResources().getString(R.string.OAUTH_CLIENT_SECRET));
        OAUTH_CLIENT_NAME = String.valueOf(getResources().getString(R.string.OAUTH_CLIENT_NAME));


        mContext = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_login);

        // 네이버 로그인 초기화
        naverInitData();

        googleSignInButtonSetText();

        // 구글 로그인
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        // 카카오 로그인
        ImageButton kakao_login_btn = findViewById(R.id.kakao_login_btn);
        kakao_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 디바이스에 카카오톡 설치 여부 확인
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginActivity.this))
                    UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this, callback);

                    // 카카오 계정으로 로그인 (카카오톡 설치 x )
                else UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this, callback);
            }
        });
    }

    private void googleSignInButtonSetText() {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleLoginButton = findViewById(R.id.google_login_button);
        setGooglePlusButtonText(googleLoginButton, "Google 계정으로 로그인");
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {

            // 사용자 정보
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);
            String username = acct.getDisplayName();
            String email = acct.getEmail();
            String providerId = acct.getId();

            // 회원가입 시 필요 정보
            SharedPreferenceUtil.setString(this, "username", username);
            SharedPreferenceUtil.setString(this, "providerId", providerId);
            SharedPreferenceUtil.setString(this, "emailAddress", email);

            // 로그인 Request
            LoginRequest loginRequest = new LoginRequest(UUID.nameUUIDFromBytes(providerId.getBytes()).toString(), MyFirebaseMessagingService.fcmToken);

            // 로그인 호출
            callLoginApi(UserService.getService().login(loginRequest));

        } catch (ApiException e) {
            Log.e(LOG_TAG, "구글 로그인 에러" + e.getStatusCode());
        }
    }

    private void updateKakaoLogin() {
        UserApiClient.getInstance().me((user, throwable) -> {

            if (user != null) {
                // 카카오에서 사용자 정보
                KakaoAuthModel kakaoAuthModel = new KakaoAuthModel(user);
                kakaoAuthModel.setFcmToken(MyFirebaseMessagingService.fcmToken);

                // 카카오에서 얻은 providerId
                String providerId = String.valueOf(user.getId());

                // 회원가입에 필요한 사용자 정보
                SharedPreferenceUtil.setString(this, "providerId", providerId);
                SharedPreferenceUtil.setString(this, "emailAddress", user.getKakaoAccount().getEmail());
                SharedPreferenceUtil.setString(this, "username", user.getKakaoAccount().getProfile().getNickname());

                // Login Request
                LoginRequest loginRequest = LoginRequest.builder()
                        .fcmToken(MyFirebaseMessagingService.fcmToken)
                        .identifier(UUID.nameUUIDFromBytes(providerId.getBytes()).toString())
                        .build();

                // 로그인 호출
                callLoginApi(UserService.getService().login(loginRequest));
            } else {
                Log.d(LOG_TAG, "User ________________________ null!!! kakao !!");
            }
            return null;
        });
    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }


    static Response<LoginResponse> response;

    public void callLoginApi(Call<LoginResponse> snsLogin) {
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                response = snsLogin.execute();
                Log.d(LOG_TAG, "sns Login : " + snsLogin);
                Log.d(LOG_TAG, "responseCode : " + response.code());
                System.out.println(response.message());
                // http status : 200
                if (response.isSuccessful()) {
                    // 메인 화면으로 가는 로직
                    LoginResponse loginResponse = response.body();
                    Log.d(LOG_TAG, "loginResponse !!!!!!!!!!" + loginResponse);

                    if (loginResponse.getAccessToken() != null) {
                        redirectCode = REDIRECT_MAIN;
                        SharedPreferenceUtil.setString(LoginActivity.this, "accessToken", loginResponse.getAccessToken());
                        SharedPreferenceUtil.setString(LoginActivity.this, "refreshToken", loginResponse.getRefreshToken());
                        //JwtTokenDecoder decoder = new JwtTokenDecoder(loginResponse.getAccessToken());
                    }
                } else { // 200이 아닌 경우
                    ErrorMessage em = ErrorMessage.getErrorByResponse(response);

                    // 신규 회원 회원가입.
                    if (Objects.equals(em.getCode(), "USER-001")) {
                        redirectCode = REDIRECT_SET_NICKNAME;
                    } else {
                        // TODO: 2023/04/03 모두 문제 -> 예외 처리
                        System.out.println("else--------");
                    }
                }
                redirectActivityByRedirectCode();

            }
        }).start();
    }

    private void redirectActivityByRedirectCode() {
        if (redirectCode == REDIRECT_SET_NICKNAME) {
            redirectNickname();
        } else if (redirectCode == REDIRECT_MAIN) redirectSignupActivity();
    }

    private void redirectSignupActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void redirectNickname() {
        Log.d(LOG_TAG, "redirectNickname=============");
        Intent intent = new Intent(getApplicationContext(), SetNicknameActivity.class);
        //intent.putExtra("newUser", userSignUpRequest);
        startActivity(intent);
        finish();
    }

    private void naverInitData() {
        // OAuthLogin 인스턴스 초기화
        mOAuthLoginInstance = OAuthLogin.getInstance();
        mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME);

        // naver Login 버튼 설정
        // naverLoginButton 을 누르면 OAuthLoginHandler 실행
        OAuthLoginButton naverLoginButton = findViewById(R.id.naver_login_btn);
        naverLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);
    }


    @SuppressWarnings("HandlerLeak")
    private final OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                Log.d(LOG_TAG, "Naver Login succes ! ");
                String accessToken = mOAuthLoginInstance.getAccessToken(mContext);

                // 사용자 정보 호출 API 요청
                new Thread(() -> {
                    String result = mOAuthLoginInstance.requestApi(mContext, accessToken, "https://openapi.naver.com/v1/nid/me");
                    try{
                        JSONObject jsonResult = new JSONObject(result);

                        // 네이버 프로필에서 얻은 고유 식별자.
                        String id = jsonResult.getJSONObject("response").getString("id");
                        // 사용자 이름 ( 디코딩한 결과 )
                        String name = URLDecoder.decode(jsonResult.getJSONObject("response").getString("name"), "UTF-8");

                        // 회원가입에 필요한 사용자 정보 저장
                        SharedPreferenceUtil.setString(mContext, "providerId", id);
                        SharedPreferenceUtil.setString(mContext, "emailAddress", jsonResult.getJSONObject("response").getString("email"));
                        SharedPreferenceUtil.setString(mContext, "username", name);

                        // Login Request
                        LoginRequest loginRequest = LoginRequest.builder()
                                .fcmToken(MyFirebaseMessagingService.fcmToken)
                                .identifier(UUID.nameUUIDFromBytes(id.getBytes()).toString())
                                .build();

                        // 로그인 호출
                        callLoginApi(UserService.getService().login(loginRequest));

                    }catch (JSONException e ){
                        e.printStackTrace();
                        // TODO: 2023/04/08 JSON 파싱 오류 처리

                    } catch (UnsupportedEncodingException e) {
                        // URL 디코딩 오류 처리
                        throw new RuntimeException("Failed to decode the user name.", e);
                    }

                }).start();

                }
                else{
                    // TODO: 2023/04/08 인증 실패 처리
                }
            }

    };


    Function2<OAuthToken, Throwable, Unit> callback = (oAuthToken, throwable) -> {
        if (oAuthToken != null) {
            updateKakaoLogin();
        }
        if (throwable != null) {
            Log.d(LOG_TAG, "[카카오] 로그인 실패");
            Log.d(LOG_TAG, "signInKakao()" + throwable.getLocalizedMessage());
        }
        return null;
    };
}
