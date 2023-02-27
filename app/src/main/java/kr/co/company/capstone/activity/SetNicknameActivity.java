package kr.co.company.capstone.activity;

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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

import kr.co.company.capstone.JwtTokenDecoder;
import kr.co.company.capstone.R;
import kr.co.company.capstone.util.SharedPreferenceUtil;
import kr.co.company.capstone.dto.ErrorMessage;
import kr.co.company.capstone.dto.login.UserSignUpRequest;
import kr.co.company.capstone.dto.login.UserSignUpResponse;
import kr.co.company.capstone.service.UserService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SetNicknameActivity extends AppCompatActivity {

    EditText nicknameEditText;
    TextView nicknameErrorText;
    TextView nicknameNotiText;
    Button dupCheckButton;
    Button joinButton;
    static final String LOG_TAG = "SetNicknameActivity";
    String nickname;
    boolean nicknameChecked = false;
    UserSignUpRequest userSignUpRequest = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_nickname);

        Intent intent = getIntent();
        userSignUpRequest = (UserSignUpRequest) intent.getSerializableExtra("newUser");
        Log.d(LOG_TAG, "userSignUpRequest : " + userSignUpRequest.toString());

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
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                showIncorrectDialogAndRestart();
                            }
                        });
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(nicknameChecked) {
                    userSignUpRequest.setNickname(nickname);
                    UserService.getService().SignUp(userSignUpRequest)
                            .enqueue(new Callback<UserSignUpResponse>() {
                                @Override
                                public void onResponse(Call<UserSignUpResponse> call, Response<UserSignUpResponse> response) {
                                    if (response.isSuccessful()) {
                                        UserSignUpResponse userSignUpResponse = response.body();
                                        Log.d(LOG_TAG, "Sign Up Success - UserSignUpResponse : " + userSignUpResponse);
                                        SharedPreferenceUtil.setString(getApplicationContext(), "jwtToken", userSignUpResponse.getAccessToken());
                                        SharedPreferenceUtil.setString(getApplicationContext(), "refreshToken", userSignUpResponse.getRefreshToken());

                                        JwtTokenDecoder jwtTokenDecoder = new JwtTokenDecoder(userSignUpResponse.getAccessToken());
                                        SharedPreferenceUtil.setString(getApplicationContext(), "nickname", jwtTokenDecoder.getNicknameByToken());
                                        SharedPreferenceUtil.setLong(getApplicationContext(), "userId", jwtTokenDecoder.getUserIdByToken());

                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        showIncorrectDialogAndRestart();
                                        Log.d(LOG_TAG, ErrorMessage.getErrorByResponse(response).toString());
                                        Log.d(LOG_TAG, "SignUp Error");
                                    }
                                }

                                @Override
                                public void onFailure(Call<UserSignUpResponse> call, Throwable t) {
                                    Log.d(LOG_TAG, "API call error");
                                    showIncorrectDialogAndRestart();
                                }
                            });
                }
                else{
                    Log.d(LOG_TAG, "required nickname check");
                    showIncorrectDialogAndRestart();
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
        });;
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