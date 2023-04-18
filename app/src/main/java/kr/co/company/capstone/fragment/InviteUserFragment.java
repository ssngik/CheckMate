
package kr.co.company.capstone.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

// layout
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

// Animation
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

// Dialog
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;

// Toast
import android.widget.Toast;
// LOG
import android.util.Log;

// Navigation
import androidx.navigation.Navigation;

// DTO
import kr.co.company.capstone.R;
import kr.co.company.capstone.dto.ErrorMessage;
import kr.co.company.capstone.dto.team_mate.TeamMateInviteRequest;
// 서비스
import kr.co.company.capstone.service.TeamMateService;

// lombok
import lombok.SneakyThrows;

// Retrofit
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InviteUserFragment extends Fragment {

    private static final String LOG_TAG = InviteUserFragment.class.getSimpleName();

    private TextView mText;
    private Animation mAnimation;
    private EditText mInputUserName;
    private long goalId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            goalId = bundle.getLong("goalId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite_user, container, false);
        Button mInviteButton = view.findViewById(R.id.do_invite_button);

        mText = view.findViewById(R.id.search_member);
        mInputUserName = view.findViewById(R.id.inputUserName);
        mAnimation = new AlphaAnimation(0.0f, 1.0f);

        textEffecting();

        mInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Input 사용자 이름
                String userName = mInputUserName.getText().toString().trim();

                // 사용자 이름을 입력하지 않았을 때
                if (userName.isEmpty()) {
                    Toast.makeText(getActivity(), "이름을 알려주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 팀원 초대
                inviteUser(userName);
            }
        });

        return view;
    }

    // 팀원 초대
    private void inviteUser(String userName) {
        TeamMateInviteRequest teamMateInviteRequest = new TeamMateInviteRequest(userName);
        TeamMateService.getService().invite(goalId, teamMateInviteRequest)
                .enqueue(new Callback<Void>() {
                    @SneakyThrows
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        String title, message;
                        // API 요청 성공
                        if (response.isSuccessful()) {
                            // 완료 문구 설정
                            title = String.valueOf(R.string.sent);
                            message = String.valueOf(R.string.invite_complete);
                        } else {
                            ErrorMessage em = ErrorMessage.getErrorByResponse(response);
                            title = "띵";
                            message = em.getMessage();
                        }
                        // 상태 안내
                        showAlertDialog(title, message);
                    }

                    // API 요청 실패
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        OnErrorFragment onErrorFragment = new OnErrorFragment();
                        onErrorFragment.show(getChildFragmentManager(), "error");
                    }
                });
    }


    private void showAlertDialog(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(title)
                .setMessage(message)
                // positive
                .setPositiveButton("계속 초대할래요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mInputUserName.setText(null);
                    }
                })
                // neutral
                .setNeutralButton("메인화면으로 갈래요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mInputUserName.setText(null);
                        Navigation.findNavController(requireView()).navigate(R.id.action_inviteUserFragment_to_navigation_home);
                    }
                })
                .create()
                .show();
    }

    private void textEffecting() {
        mAnimation.setDuration(1000);
        mAnimation.setStartOffset(100);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setRepeatCount(3);
        //anim.setRepeatCount(Animation.INFINITE);

        mText.startAnimation(mAnimation);
    }


}