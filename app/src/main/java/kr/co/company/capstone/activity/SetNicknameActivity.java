
package kr.co.company.capstone.activity;

// Android Framework 제공 라이브러리 관련
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

// androidx 라이브러리 관련
import androidx.appcompat.app.AlertDialog;
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

    public static Context context;
    EditText nicknameEditText;
    TextView nicknameErrorText;
    TextView nicknameNotiText;
    Button dupCheckButton;
    Button joinButton;
    static final String LOG_TAG = "SetNicknameActivity";
    String nickname;
    boolean nicknameChecked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_nickname);

        nicknameEditText = findViewById(R.id.putNickname);
        nicknameErrorText = findViewById(R.id.nick_error_text);
        nicknameNotiText = findViewById(R.id.nickNotification);
        dupCheckButton = findViewById(R.id.dupCheck);
        joinButton = findViewById(R.id.joinButton);

        Pattern charPattern = Pattern.compile("^[a-zA-Z0-9ㄱ-ㅎ가-힣]+$");
        Pattern lengthPattern = Pattern.compile("^.{2,8}$");

        nicknameValidation(charPattern, lengthPattern);
        dupCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nickname = nicknameEditText.getText().toString();
                UserService.getService().duplicatedNicknameCheck(nickname)
                        .enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    nicknameChecked = true;
                                    showCorrectDialog();
                                    joinButton.setEnabled(true);
                                    joinButton.setBackgroundColor(Color.parseColor("#43A047"));
                                    Log.i(LOG_TAG, "enable nickname");
                                } else {
                                    showIncorrectDialog();
                                    Log.d(LOG_TAG, "dupCheckError");
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                showIncorrectDialogAndRestart();
                            }
                        });
            }
        });

        // 회원가입
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nicknameChecked){
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

                            if(response.isSuccessful()){ // 회원가입 API 성공
                                Log.d(LOG_TAG, response.toString());

                                LoginRequest loginRequest = new LoginRequest(identifier, fcmToken);
                                UserService.getService().login(loginRequest).enqueue(new Callback<LoginResponse>() {
                                    @Override
                                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                                        if(response.isSuccessful()){
                                            LoginResponse loginResponse = response.body();
                                            Log.d(LOG_TAG, loginResponse + "login Success!!");

                                            SharedPreferenceUtil.setString(getApplicationContext(), "accessToken", loginResponse.getAccessToken());
                                            SharedPreferenceUtil.setString(getApplicationContext(), "refreshToken", loginResponse.getRefreshToken());
                                            SharedPreferenceUtil.setString(getApplicationContext(), "nickName", nickname);

                                            // Main 화면으로 이동
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            finish();

                                        }else{
                                            Log.d(LOG_TAG, "로그인이 실패");
                                            Log.d(LOG_TAG, ErrorMessage.getErrorByResponse(response).getMessage());
                                            Log.d(LOG_TAG, loginRequest.toString());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                                        Log.d(LOG_TAG, ErrorMessage.getErrorByResponse(response).toString());
                                    }
                                });



                            }else{
                                Log.d(LOG_TAG, "fail");
                                Log.d(LOG_TAG, ErrorMessage.getErrorByResponse(response).toString());
                                Log.d(LOG_TAG, "error response : " + response);
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
            }
        });

    }

    private void showIncorrectDialogAndRestart(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SetNicknameActivity.this);
        builder.setTitle("띵").setMessage("중복된 닉네임 입니다.").setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showIncorrectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SetNicknameActivity.this);
        builder.setTitle("띵").setMessage("중복된 닉네임 입니다.").setPositiveButton("확인",null);;
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showCorrectDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SetNicknameActivity.this);
        builder.setTitle("OK").setMessage("사용가능한 닉네임입니다.").setPositiveButton("확인",null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void nicknameValidation(Pattern charPattern, Pattern lengthPattern) {
        nicknameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nicknameChecked = false;
                String target = s.toString();
                if (!charPattern.matcher(target).matches()) {
                    nicknameEditText.setBackgroundResource(R.drawable.red_edit_text);
                    nicknameErrorText.setText("특수문자 사용 불가");
                    dupCheckButton.setEnabled(false);
                    joinButton.setEnabled(false);

                } else if (!lengthPattern.matcher(target).matches()){
                    nicknameErrorText.setText("2글자 이상 8글자 이하");
                    dupCheckButton.setEnabled(false);
                    joinButton.setEnabled(false);

                }
                else {
                    nicknameEditText.setBackgroundResource(R.drawable.green_edit_text);
                    nicknameErrorText.setText("");
                    dupCheckButton.setEnabled(true);
                    dupCheckButton.setBackgroundColor(Color.parseColor("#43A047"));
                    joinButton.setBackgroundColor(Color.parseColor("#43A047"));
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

}