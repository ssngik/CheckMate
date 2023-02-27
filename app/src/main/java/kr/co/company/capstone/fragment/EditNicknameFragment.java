package kr.co.company.capstone.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.regex.Pattern;

import androidx.navigation.Navigation;
import kr.co.company.capstone.dto.ErrorMessage;
import kr.co.company.capstone.R;
import kr.co.company.capstone.util.SharedPreferenceUtil;
import kr.co.company.capstone.dto.UserNicknameUpdateRequest;
import kr.co.company.capstone.service.UserService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditNicknameFragment extends Fragment {
    EditText nicknameEditText;
    TextView nicknameErrorText, nicknameNotificationText, beforeNickName;
    Button dupCheckButton, setCompleteButton;
    static final String LOG_TAG = "SetNicknameActivity";
    String nickname;
    boolean nicknameChecked = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_nickname, container, false);

        nicknameEditText = view.findViewById(R.id.putNickname);
        nicknameErrorText = view.findViewById(R.id.nick_error_text);
        nicknameNotificationText = view.findViewById(R.id.nickNotification);
        dupCheckButton = view.findViewById(R.id.dupCheck);
        setCompleteButton = view.findViewById(R.id.joinButton);
        beforeNickName = view.findViewById(R.id.before_nickname);

        beforeNickName.setText(SharedPreferenceUtil.getString(getContext(), "nickname"));

        Pattern charPattern = Pattern.compile("^[a-zA-Z0-9ㄱ-ㅎ가-힣]+$");
        Pattern lengthPattern = Pattern.compile("^.{2,8}$");

        nicknameValidation(charPattern, lengthPattern);
        dupCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nickname = nicknameEditText.getText().toString();
                Call<Void> service = UserService.getService().duplicatedNicknameCheck(nickname);
                service.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            nicknameChecked = true;
                            showCorrectDialog();
                            setCompleteButton.setEnabled(true);
                            setCompleteButton.setBackgroundColor(Color.parseColor("#43A047"));
                            Log.i(LOG_TAG, "enable nickname");
                        } else {
                            showIncorrectDialog();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.d(LOG_TAG, "API call error");
                    }
                });
            }
        });

        setCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newNickname = nicknameEditText.getText().toString();
                UserService.getService().updateNickname(new UserNicknameUpdateRequest(newNickname))
                        .enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle("닉네임 변경").setMessage("새로운 닉네임을 적용했어요!").setPositiveButton("확인", null);;
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                    SharedPreferenceUtil.setString(getContext(), "nickname", newNickname);
                                    Navigation.findNavController(view).popBackStack();
                                }
                                else{
                                    ErrorMessage errorMessage = ErrorMessage.getErrorByResponse(response);
                                    Log.d(LOG_TAG, "update nickname response : " + response.code());
                                    Log.d(LOG_TAG, errorMessage.toString());
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle("닉네임 변경 실패").setMessage(errorMessage.getMessage()).setPositiveButton("확인", null);;
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                    Navigation.findNavController(view).popBackStack();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                //Log.d(LOG_TAG, t.getMessage());
                                OnErrorFragment onErrorFragment = new OnErrorFragment();
                                onErrorFragment.show(getChildFragmentManager(), "error");
                            }
                        });
            }
        });
        return view;
    }

    private void showIncorrectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("!").setMessage("중복된 닉네임 입니다.").setPositiveButton("확인", null);;
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showCorrectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("OK").setMessage("사용가능한 닉네임입니다.").setPositiveButton("확인", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void nicknameValidation(Pattern charPattern, Pattern lengthPattern) {
        nicknameEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nicknameChecked = false;
                String target = s.toString();
                if (!charPattern.matcher(target).matches()) {
                    nicknameEditText.setBackgroundResource(R.drawable.red_edit_text);
                    nicknameErrorText.setText("특수문자 사용 불가");
                    dupCheckButton.setEnabled(false);
                    setCompleteButton.setEnabled(false);

                } else if (!lengthPattern.matcher(target).matches()) {
                    nicknameErrorText.setText("2글자 이상 8글자 이하");
                    dupCheckButton.setEnabled(false);
                    setCompleteButton.setEnabled(false);

                } else {
                    nicknameEditText.setBackgroundResource(R.drawable.green_edit_text);
                    nicknameErrorText.setText("");
                    dupCheckButton.setEnabled(true);
                    dupCheckButton.setBackgroundColor(Color.parseColor("#43A047"));
                    setCompleteButton.setBackgroundColor(Color.parseColor("#43A047"));
                }
            }



            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}