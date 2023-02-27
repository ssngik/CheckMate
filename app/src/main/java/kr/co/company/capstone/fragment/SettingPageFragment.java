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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.Objects;

import kr.co.company.capstone.R;
import kr.co.company.capstone.Theme;
import kr.co.company.capstone.activity.LoginActivity;
import kr.co.company.capstone.dto.ErrorMessage;
import kr.co.company.capstone.service.LoginService;
import kr.co.company.capstone.util.SharedPreferenceUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingPageFragment extends Fragment {

    final String[] words = new String[]{"라이트 모드", "다크 모드"};

    private String LOG_TAG = "SettingPageFragment";
    RelativeLayout settingSupportArea;
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
        View rootView = inflater.inflate(R.layout.fragment_setting_icon, container, false);

        TextView logout = rootView.findViewById(R.id.Logout);
        TextView edit_name = rootView.findViewById(R.id.edit_nickname);
        TextView email = rootView.findViewById(R.id.Support_email);
        TextView mode = rootView.findViewById(R.id.Screen_mode);
        TextView nickname = rootView.findViewById(R.id.Nickname);
        TextView privacy = rootView.findViewById(R.id.Privacy_policy);
        settingSupportArea = rootView.findViewById(R.id.setting_support_area);
        settingSupportArea.setClickable(true);
        nickname.setText(SharedPreferenceUtil.getString(getContext(), "nickname"));
        logout.setText(SharedPreferenceUtil.getString(getContext(), "nickname") + " 로그아웃");

        logOut(logout);
        editNickName(edit_name);
        setWhiteOrDarkMode(mode);
        clickEmail(email);
        privacyInfo(privacy);

        return rootView;
    }

    private void editNickName(TextView edit_name) {
        edit_name.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.editNicknameFragment));
    }

    private void logOut(TextView logout) {
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginService.getService().logout().enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            SharedPreferenceUtil.removeKey(getActivity(), "jwtToken");
                            SharedPreferenceUtil.removeKey(getActivity(), "refreshToken");
                            Objects.requireNonNull(getActivity()).finish();
                        } else {
                            OnErrorFragment onErrorFragment = new OnErrorFragment();
                            onErrorFragment.show(getChildFragmentManager(), "error");
//                            Log.d(LOG_TAG, ErrorMessage.getErrorByResponse(response).toString());
                        }

                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
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

    private void setWhiteOrDarkMode(TextView mode) {
        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity()).setTitle("화면 스타일 선택").setSingleChoiceItems(words,  -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        settingSupportArea.setClickable(false);
                        Toast.makeText(getActivity(), words[which] + "로 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        switch (which) {
                            case 0:
                                Theme.applyTheme(Theme.LIGHT_MODE);
                                SharedPreferenceUtil.setString(getActivity(), "scnMode", Theme.LIGHT_MODE);
                                break;
                            case 1:
                                Theme.applyTheme(Theme.DARK_MODE);
                                SharedPreferenceUtil.setString(getActivity(), "scnMode", Theme.DARK_MODE);
                                break;
                        }
                        settingSupportArea.setClickable(true);
                    }
                }).show();
            }
        });
    }

    private void clickEmail(TextView email) {
        email.setOnClickListener(new TextView.OnClickListener() {
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("plain/text");
                String[] address = {"teamcheckus@gmail.com"};
                email.putExtra(Intent.EXTRA_EMAIL, address);
                email.putExtra(Intent.EXTRA_SUBJECT, "help");
                startActivity(email);
            }
        });
    }

    private void privacyInfo(TextView privacy) {
        privacy.setOnClickListener(new TextView.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://checkus.notion.site/0b59fd1da54e44aa9cc5fa10b36868de"));
                startActivity(intent);
            }
        });
    }
}
