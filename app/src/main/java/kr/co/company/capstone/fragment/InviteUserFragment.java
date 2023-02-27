package kr.co.company.capstone.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import androidx.navigation.Navigation;
import kr.co.company.capstone.R;
import kr.co.company.capstone.dto.ErrorMessage;
import kr.co.company.capstone.dto.team_mate.TeamMateInviteRequest;
import kr.co.company.capstone.service.TeamMateService;
import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InviteUserFragment extends Fragment {

    TextView text;
    Animation anim;
    Button inviteButton;
    EditText inputUserName;
    long goalId;
    private static final String LOG_TAG = "InviteUserFragment";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            goalId = bundle.getLong("goalId", 0L);
            Log.d(LOG_TAG, "goalId in onCreate : " + goalId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_invite_user, container, false);

        Log.d(LOG_TAG, "goal id in InviteUserFragment : " + goalId);

        text = view.findViewById(R.id.search_member);
        inviteButton = view.findViewById(R.id.do_invite_button);
        inputUserName = view.findViewById(R.id.inputUserName);
        anim = new AlphaAnimation(0.0f,1.0f);
        textEffecting();

        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TeamMateService.getService().invite(new TeamMateInviteRequest(goalId, inputUserName.getText().toString()))
                        .enqueue(new Callback<Void>() {
                            @SneakyThrows
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                Log.d(LOG_TAG, "INVITE CODE : " + response.code());
                                String title = "전송 완료!", message = "초대 요청을 보냈어요! \n응답이 오면 알려드릴게요";
                                int responseCode = response.code();
                                if (responseCode != 200) {
                                    ErrorMessage em = ErrorMessage.getErrorByResponse(response);
                                    title = "띵";
                                    message = em.getMessage();
                                }
                                showAlertDialog(title, message, view);
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                OnErrorFragment onErrorFragment = new OnErrorFragment();
                                onErrorFragment.show(getChildFragmentManager(), "error");
                            }
                        });
            }
        });

        return view;
    }

    private void showAlertDialog(String title, String message, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setMessage(message).setPositiveButton("계속 초대할래요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                inputUserName.setText(null);
            }
        });
        builder.setTitle(title).setMessage(message).setNeutralButton("메인화면으로 갈래요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                inputUserName.setText(null);
                Navigation.findNavController(view).navigate(R.id.action_inviteUserFragment_to_navigation_home);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void textEffecting() {
        anim.setDuration(1000);
        anim.setStartOffset(100);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(3);
        //anim.setRepeatCount(Animation.INFINITE);

        text.startAnimation(anim);
    }


}