package kr.co.company.capstone.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.Objects;

import kr.co.company.capstone.R;
import kr.co.company.capstone.Theme;
import kr.co.company.capstone.activity.LoginActivity;
import kr.co.company.capstone.service.LoginService;
import kr.co.company.capstone.util.SharedPreferenceUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingPageFragment extends Fragment {


    private String LOG_TAG = "SettingPageFragment";
    public SettingPageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_icon, container, false);

        /*
         * UI 설정 부분
         */

        // 사용자 닉네임 UI 설정
        TextView myNickName = view.findViewById (R.id.Nickname);
        myNickName.setText(SharedPreferenceUtil.getString(getContext(), "nickName"));

        /*
        * 기능 부분
        * */

        // 로그아웃 버튼 리스너 설정
        setClickLogOutButton(view);

        // 닉네임 변경으로 이동
        editNickName(view);

        // 화면 모드 설정
        setWhiteOrDarkMode(view);

        // 이메일 보내기
        clickEmail(view);

        // 개인정보 처리방침
        privacyInfo(view);

        return view;
    }

    // 닉네임 변경
    private void editNickName(View view) {
        TextView editNickNameTextButton = view.findViewById (R.id.edit_nickname);
        editNickNameTextButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.editNicknameFragment));
    }

    private void setClickLogOutButton(View view) {
        TextView logout = view.findViewById(R.id.Logout);

        logout.setText(SharedPreferenceUtil.getString(getContext(), "nickName") + " 로그아웃");
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginService.getService().logout().enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            SharedPreferenceUtil.removeKey(getActivity(), "accessToken");
                            SharedPreferenceUtil.removeKey(getActivity(), "refreshToken");
                            Objects.requireNonNull(getActivity()).finish();

                            Log.d(LOG_TAG, "succes!");

                            // 초기 로그인 화면으로 이동
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            startActivity(intent);

                        } else {
                            OnErrorFragment onErrorFragment = new OnErrorFragment();
                            onErrorFragment.show(getChildFragmentManager(), "error");
                            Log.d(LOG_TAG, response.toString());
                        }


                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        OnErrorFragment onErrorFragment = new OnErrorFragment();
                        onErrorFragment.show(getChildFragmentManager(), "error");
                    }
                });
            }

        });
    }

    // 화면 모드 변경
    private void setWhiteOrDarkMode(View view) {

        // 사용자 지원 안내 정보 구역
        RelativeLayout settingSupportArea = view.findViewById (R.id.setting_support_area);
        settingSupportArea.setClickable(true);

        // 화면 테마 Text Button
        TextView displayThemeTextButton = view.findViewById (R.id.Screen_mode);

        // 다크 | 라이트 모드 설정 버튼 클릭 리스너
        displayThemeTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String[] words = new String[]{"라이트 모드", "다크 모드"};

                new AlertDialog.Builder(getActivity()).setTitle("화면 스타일 선택").setSingleChoiceItems(words,  -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // 사용자 지원 안내 부분 터치 불가 상태
                        settingSupportArea.setClickable(false);

                        switch (which) {
                            case 0: // Light Mode
                                Theme.applyTheme(Theme.LIGHT_MODE);
                                SharedPreferenceUtil.setString(getActivity(), "scnMode", Theme.LIGHT_MODE);
                                break;
                            case 1: // Dark Mode
                                Theme.applyTheme(Theme.DARK_MODE);
                                SharedPreferenceUtil.setString(getActivity(), "scnMode", Theme.DARK_MODE);
                                break;
                        }

                        // 변경 완료 알림
                        Toast.makeText(getActivity(), words[which] + "로 변경되었습니다.", Toast.LENGTH_SHORT).show();

                        // 사용자 지원 안내 부분 터치 가능 상태
                        settingSupportArea.setClickable(true);
                    }
                }).show();
            }
        });
    }

    // 이메일 눌렀을 때
    private void clickEmail(View view) {
        TextView emailTextButton = view.findViewById (R.id.Support_email);

        emailTextButton.setOnClickListener(view1 -> {
            Intent email = new Intent(Intent.ACTION_SEND);
            email.setType("plain/text");
            String[] address = {"teamcheckus@gmail.com"};

            email.putExtra(Intent.EXTRA_EMAIL, address);
            email.putExtra(Intent.EXTRA_SUBJECT, "help");

            startActivity(email);
        });

    }

    // 정책 관련 안내
    private void privacyInfo(View view) {
        TextView privacy = view.findViewById(R.id.Privacy_policy);

        privacy.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://checkus.notion.site/0b59fd1da54e44aa9cc5fa10b36868de"));
            startActivity(intent);
        });
    }
}
