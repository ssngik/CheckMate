package kr.co.company.capstone.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import kr.co.company.capstone.R;
import kr.co.company.capstone.dto.team_mate.TeamMateInviteReplyRequest;
import kr.co.company.capstone.dto.team_mate.TeamMateAcceptInviteResponse;
import kr.co.company.capstone.service.TeamMateService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InviteResponseFragment extends Fragment {
    private Button acceptButton, rejectButton;
    private String message;

    private static final String LOG_TAG = "InviteResponseFragment";
    private long notificationId;

    public InviteResponseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            Bundle bundle = getArguments();
            message = bundle.getString("messageBody");
            notificationId = bundle.getLong("notificationId");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.invite_response, container, false);

        acceptButton = view.findViewById(R.id.accept_button);
        rejectButton = view.findViewById(R.id.reject_button);

        // 초대 문구
        TextView inviteComment = view.findViewById(R.id.set_date_text);
        inviteComment.setText(message);

        // 목표 수락 버튼
        clickAcceptButton();
        // 목표 거절 버튼
        clickRejectButton();

        return view;
    }

    private void clickRejectButton() {
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TeamMateService.getService().inviteReject(new TeamMateInviteReplyRequest(notificationId))
                        .enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.isSuccessful()){
                                    // 알림 화면으로 이동
                                    goToAlarm(view);
                                }else{
                                    showAlertDialog("띵", "예상치 못한 에러 발생", view);
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

    // 알림 화면으로 이동
    private void goToAlarm(View view) {
        Navigation.findNavController(view).navigate(R.id.action_inviteResponseFragment_to_navigation_alarm);
    }

    // 목표 상세 화면으로 이동
    private void goToGoalDetail(Response<TeamMateAcceptInviteResponse> response, View view) {

        TeamMateAcceptInviteResponse inviteReplyResponse = response.body();
        InviteResponseFragment inviteResponseFragment = new InviteResponseFragment();
        Bundle next = new Bundle();

        next.putLong("goalId", inviteReplyResponse.getGoalId());
        inviteResponseFragment.setArguments(next);

        Navigation.findNavController(view).navigate(R.id.action_inviteResponseFragment_to_goalDetailFragment, next);
    }

    private void clickAcceptButton() {
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TeamMateService.getService()
                        .inviteReply(new TeamMateInviteReplyRequest(notificationId))
                        .enqueue(new Callback<TeamMateAcceptInviteResponse>() {
                            @Override
                            public void onResponse(Call<TeamMateAcceptInviteResponse> call, Response<TeamMateAcceptInviteResponse> response) {
                                if(response.isSuccessful()){
                                    // 목표 상세 화면으로 이동
                                    goToGoalDetail(response, view);
                                }
                                else{
                                    showAlertDialog("합류 실패", "초대를 수락할 수 있는 기간이 지났어요ㅜㅜ", view);
                                }
                            }
                            @Override
                            public void onFailure(Call<TeamMateAcceptInviteResponse> call, Throwable t) {
                                OnErrorFragment onErrorFragment = new OnErrorFragment();
                                onErrorFragment.show(getChildFragmentManager(), "error");
                            }
                        });
            }
        });
    }


    private void showAlertDialog(String title, String message, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setMessage(message).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Navigation.findNavController(view).navigate(R.id.action_inviteResponseFragment_to_navigation_alarm);
            }
        });;
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
