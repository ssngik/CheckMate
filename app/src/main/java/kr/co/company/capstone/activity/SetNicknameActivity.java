
package kr.co.company.capstone.activity;

// Android Framework 제공 라이브러리 관련
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

// androidx 라이브러리 관련
import androidx.appcompat.app.AppCompatActivity;
// Java 라이브러리 관련
import java.util.UUID;
import java.util.regex.Pattern;

// Retrofit 라이브러리 관련
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 프로젝트별 클래스 및 파일
import kr.co.company.capstone.R; // R 파일
import kr.co.company.capstone.dto.login.*; // 로그인에 관련된 DTO 클래스들
import kr.co.company.capstone.dto.ErrorMessage; // 에러 메세지 DTO 클래스
import kr.co.company.capstone.service.UserService; // 사용자 관련 API를 사용하기 위한 Service 클래스
import kr.co.company.capstone.service.MyFirebaseMessagingService; // Firebase Messaging Service
import kr.co.company.capstone.util.SharedPreferenceUtil; // SharedPreferences를 사용하기 위한 유틸 클래스


public class SetNicknameActivity extends AppCompatActivity {
    TextView nicknameNotification;
    Button joinButton;
    static final String LOG_TAG = "SetNicknameActivity";
    String nickname;
    boolean nicknameChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_nickname);

        initializeViews();

        // 닉네임 유효성 체크
        checkNickname();

        // 회원가입 버튼 클릭리스너
        setupJoinButtonClickListener();

    }

    // 회원가입 버튼 클릭리스너
    private void setupJoinButtonClickListener() {
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nicknameChecked){
                    // 회원가입
                    signUp();
                }

            }
        });
    }

    // 회원가입
    private void signUp() {
        // 회원가입 시 필요 정보
        String providerId = SharedPreferenceUtil.getString(getApplicationContext(), "providerId");
        String username = SharedPreferenceUtil.getString(getApplicationContext(), "username");
        String emailAddress = SharedPreferenceUtil.getString(getApplicationContext(), "emailAddress");
        String fcmToken = MyFirebaseMessagingService.fcmToken;

        // 사용자 식별 정보
        String identifier = UUID.nameUUIDFromBytes(providerId.getBytes()).toString();

        // 회원가입 Request
        UserSignUpRequest userSignUpRequest = new UserSignUpRequest(identifier, username, emailAddress, nickname);

        UserService.getService().signUp(userSignUpRequest)
                .enqueue(new Callback<Void>(){
            @Override
            public void onResponse(Call<Void> call, Response<Void> response){

                // 회원가입 API 성공
                if(response.isSuccessful()){
                    // 회원가입 후 로그인
                    LoginAfterSignUp(response, identifier, fcmToken);
                }
            }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        // 통신 실패 처리
                        Log.d(LOG_TAG, "fail");
                        Log.d(LOG_TAG, "error message : " + t.getMessage());
                    }
                });
    }

    // 회원가입 후 로그인
    private void LoginAfterSignUp(Response<Void> response, String identifier, String fcmToken) {
        LoginRequest loginRequest = new LoginRequest(identifier, fcmToken);
        UserService.getService().login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful()){
                    // 로그인 response 처리
                    handleLoginResponse(response.body());
                }else{
                    handleLoginFailure(response);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d(LOG_TAG, ErrorMessage.getErrorByResponse(response).toString());
            }
        });
    }

    private static void handleLoginFailure(Response<LoginResponse> response) {
        Log.d(LOG_TAG, "로그인이 실패");
        Log.d(LOG_TAG, ErrorMessage.getErrorByResponse(response).getMessage());
        // TODO: 11/25/23 UI 전달 받으면 에러처리
    }

    // 로그인 Response 처리
    private void handleLoginResponse(LoginResponse loginResponse) {
        SharedPreferenceUtil.setString(getApplicationContext(), "accessToken", loginResponse.getAccessToken());
        SharedPreferenceUtil.setString(getApplicationContext(), "refreshToken", loginResponse.getRefreshToken());
        SharedPreferenceUtil.setString(getApplicationContext(), "nickName", nickname);

        // Main 화면으로 이동
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initializeViews() {
        nicknameNotification = findViewById(R.id.nickname_notification);
        joinButton = findViewById(R.id.joinButton);
    }

    // 닉네임 유효성 체크

    private void checkNickname() {

        // 사용자 input 닉네임
        EditText nicknameEditText = findViewById(R.id.put_nickname);

        // 닉네임 안내 아이콘
        ImageView alertCircle = findViewById(R.id.error_alert);

        // 닉네임 조건 reg
        Pattern charPattern = Pattern.compile("^[a-zA-Z0-9ㄱ-ㅎ가-힣]+$");
        Pattern lengthPattern = Pattern.compile("^.{2,8}$");

        nicknameEditText.addTextChangedListener(new TextWatcher() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nickname = s.toString();

                // 닉네임 중복 체크
                checkDuplication(alertCircle);

                // 닉네임 길이 제한에 벗어난 경우
                if (!lengthPattern.matcher(nickname).matches()){
                    showNicknameErrorUi(R.string.nickname_length_guide, R.drawable.alert_cirlcle_error, alertCircle);
                }

                // 특수문자를 사용한 경우
                else if (!charPattern.matcher(nickname).matches()) {
                    showNicknameErrorUi(R.string.nickname_special_guide, R.drawable.alert_cirlcle_error, alertCircle);
                }

                // 올바른 닉네임 설정
                else {
                    showNicknameSuccessUi(R.string.nickname_ok, R.drawable.alert_circle_good, alertCircle);
                }

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void checkDuplication(ImageView alertCircle) {
        UserService.getService().duplicatedNicknameCheck(nickname)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        // 닉네임이 중복된 경우
                        if (!response.isSuccessful())
                            showNicknameErrorUi(R.string.nickname_duplication, R.drawable.alert_cirlcle_error, alertCircle);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        // TODO: 11/25/23 UI 받으면 에러처리
                    }
                });
    }

    // 적합하지 않은 닉네임
    private void showNicknameErrorUi(int messageResId, int imageResId, ImageView alertCircle) {
        nicknameNotification.setText(messageResId);
        alertCircle.setImageResource(imageResId);
        setJoinButtonDisabled();
    }

    // 적합한 닉네임 화면 표시
    private void showNicknameSuccessUi(int messageResId, int imageResId, ImageView alertCircle) {
        nicknameNotification.setText(messageResId);
        nicknameNotification.setTextAppearance(R.style.alert_nickname_good);
        alertCircle.setImageResource(imageResId);
        setJoinButtonEnabled();
    }

    // 버튼 활성화
    @SuppressLint("UseCompatLoadingForDrawables")
    private void setJoinButtonEnabled(){
        joinButton.setBackground(getDrawable(R.drawable.btn_nickname_ok));
        joinButton.setClickable(true);
        nicknameChecked = true;
    }

    // 버튼 비활성화
    @SuppressLint("UseCompatLoadingForDrawables")
    private void setJoinButtonDisabled(){
        joinButton.setBackground(getDrawable(R.drawable.btn_nickname_reject));
        joinButton.setClickable(false);
        nicknameChecked = false;
    }

}