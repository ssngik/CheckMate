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
import kr.co.company.capstone.dto.ErrorMessage;
import kr.co.company.capstone.dto.team_mate.TeamMateInviteReplyRequest;
import kr.co.company.capstone.dto.team_mate.TeamMateInviteReplyResponse;
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
            long teamMateId = bundle.getLong("teamMateId");
            notificationId = bundle.getLong("notificationId");
        }

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.invite_response, container, false);

        acceptButton = view.findViewById(R.id.accept_button);
        rejectButton = view.findViewById(R.id.reject_button);
        TextView inviteComment = view.findViewById(R.id.set_date_text);

        inviteComment.setText(message);

        clickAcceptButton();
        clickRejectButton();

        return view;
    }

    private void clickRejectButton() {
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TeamMateService.getService().inviteReply(new TeamMateInviteReplyRequest(notificationId, false))
                        .enqueue(new Callback<TeamMateInviteReplyResponse>() {
                            @Override
                            public void onResponse(Call<TeamMateInviteReplyResponse> call, Response<TeamMateInviteReplyResponse> response) {
                                goToAlarm(response, view);
                            }
                            @Override
                            public void onFailure(Call<TeamMateInviteReplyResponse> call, Throwable t) {
                                OnErrorFragment onErrorFragment = new OnErrorFragment();
                                onErrorFragment.show(getChildFragmentManager(), "error");
                                //Log.d(LOG_TAG, "accept error");
                            }
                        });
            }
        });
    }

    private void goToAlarm(Response<TeamMateInviteReplyResponse> response, View view) {
        TeamMateInviteReplyResponse inviteReplyResponse = response.body();
        if (inviteReplyResponse != null) {
            Navigation.findNavController(view).navigate(R.id.action_inviteResponseFragment_to_navigation_alarm);
        }
    }

    private void clickAcceptButton() {
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TeamMateService.getService()
                        .inviteReply(new TeamMateInviteReplyRequest(notificationId, true))
                        .enqueue(new Callback<TeamMateInviteReplyResponse>() {
                            @Override
                            public void onResponse(Call<TeamMateInviteReplyResponse> call, Response<TeamMateInviteReplyResponse> response) {
                                if(response.isSuccessful()){
                                    goToGoalDetail(response, view);
                                }
                                else{
                                    Log.d(LOG_TAG, ErrorMessage.getErrorByResponse(response).toString());
                                    showAlertDialog("합류 실패", "초대를 수락할 수 있는 기간이 지났어요ㅜㅜ", view);
                                }
                            }
                            @Override
                            public void onFailure(Call<TeamMateInviteReplyResponse> call, Throwable t) {
                                OnErrorFragment onErrorFragment = new OnErrorFragment();
                                onErrorFragment.show(getChildFragmentManager(), "error");
                                //Log.d(LOG_TAG, "accept error");
                            }
                        });
            }
        });
    }

    private void goToGoalDetail(Response<TeamMateInviteReplyResponse> response, View view) {
        TeamMateInviteReplyResponse inviteReplyResponse = response.body();
        InviteResponseFragment inviteResponseFragment = new InviteResponseFragment();
        Bundle next = new Bundle();
        next.putLong("goalId", inviteReplyResponse.getGoalId());
        inviteResponseFragment.setArguments(next);
        Navigation.findNavController(view).navigate(R.id.action_inviteResponseFragment_to_goalDetailFragment, next);
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
