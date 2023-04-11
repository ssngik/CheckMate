// 안드로이드 OS 관련
package kr.co.company.capstone.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// 안드로이드 위젯 관련
import android.widget.ImageView;
import android.widget.TextView;

// androidx 라이브러리 관련
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

// DTO 클래스 관련
import kr.co.company.capstone.dto.goal.GoalCreateRequest;

// R 클래스 관련
import kr.co.company.capstone.R;

public class GoalCreateCompleteFragment extends Fragment {
    String LOG_TAG = "Complete";
    private static final String ARG_PARAM1 = "goalId";

    GoalCreateRequest goalCreateRequest;
    ImageView myTitleImage;

    Long goalId;

    public GoalCreateCompleteFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            Bundle bundle = getArguments();
            goalId = bundle.getLong("goalId");
            goalCreateRequest = (GoalCreateRequest) bundle.getSerializable("request");
        }
}
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_complete, container, false);


        TextView title = view.findViewById(R.id.my_title_name);
        TextView startDateInfo = view.findViewById(R.id.start_date_info);
        TextView endDateInfo = view.findViewById(R.id.end_date_info);
        TextView checkDays = view.findViewById(R.id.my_day_set);
        TextView appointmentTime = view.findViewById(R.id.my_time_set);
        TextView toMain = view.findViewById(R.id.to_main);
        TextView toInvite = view.findViewById(R.id.to_invite);
        myTitleImage = view.findViewById(R.id.my_title_image);

        setTextWithGoalCreateRequest(title, startDateInfo, endDateInfo, checkDays, appointmentTime);
        clickInviteButton(toInvite);
        clickToMainButton(toMain);

        return view;
    }

    // 목표 생성시 작성한 정보
    private void setTextWithGoalCreateRequest(TextView title, TextView startDateInfo, TextView endDateInfo, TextView checkDays, TextView appointmentTime) {
        title.setText(goalCreateRequest.getTitle());
        startDateInfo.setText(goalCreateRequest.getStartDate());
        endDateInfo.setText(goalCreateRequest.getEndDate());
        checkDays.setText(goalCreateRequest.getCheckDays());
        if(goalCreateRequest.getAppointmentTime()!=null) appointmentTime.setText(goalCreateRequest.getAppointmentTime());
    }

    // Main 화면 이동
    private void clickToMainButton(TextView toMain) {
        toMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_goalCreateCompleteActivity_to_navigation_home);
            }
        });
    }

    // 초대하기
    private void clickInviteButton(TextView toInvite) {
        toInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle inviteId = new Bundle();
                inviteId.putLong("goalId", goalId);
                Navigation.findNavController(view).navigate(R.id.action_goalCreateCompleteActivity_to_fragmentInviteUser, inviteId);
            }
        });
    }

}
