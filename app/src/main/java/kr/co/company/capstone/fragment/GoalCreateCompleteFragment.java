package kr.co.company.capstone.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import kr.co.company.capstone.R;
import kr.co.company.capstone.dto.goal.GoalCreateResponse;

public class GoalCreateCompleteFragment extends Fragment {
    String LOG_TAG = "Complete";
    private static final String ARG_PARAM1 = "goalId";

    GoalCreateResponse goalCreateResponse;
    ImageView myTitleImage;
    public GoalCreateCompleteFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            Bundle bundle = getArguments();
            goalCreateResponse = (GoalCreateResponse) bundle.getSerializable("goalCreateResponse");
        }
}
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_complete, container, false);


        TextView myTitleName = view.findViewById(R.id.my_title_name);
        TextView startDateInfo = view.findViewById(R.id.start_date_info);
        TextView endDateInfo = view.findViewById(R.id.end_date_info);
        TextView myDaySet = view.findViewById(R.id.my_day_set);
        TextView myMethodSet = view.findViewById(R.id.my_method_set);
        TextView myTimeSet = view.findViewById(R.id.my_time_set);
        TextView toMain = view.findViewById(R.id.to_main);
        TextView toInvite = view.findViewById(R.id.to_invite);
        myTitleImage = view.findViewById(R.id.my_title_image);
        TextView myLikeSet = view.findViewById(R.id.my_like_set);

        setTextWithGoalCreateResponse(myTitleName, startDateInfo, endDateInfo, myDaySet, myTimeSet, myLikeSet);
        checkInstantOrConfirm(myMethodSet);
        clickInviteButton(toInvite);
        clickToMainButton(toMain);

        return view;
    }

    private void setTextWithGoalCreateResponse(TextView myTitleName, TextView startDateInfo, TextView endDateInfo, TextView myDaySet, TextView myTimeSet, TextView myLikeSet) {
        myTitleName.setText(goalCreateResponse.getTitle());
        startDateInfo.setText(goalCreateResponse.getStartDate());
        endDateInfo.setText(goalCreateResponse.getEndDate());
        myDaySet.setText(goalCreateResponse.getWeekDays());
        myLikeSet.setText(goalCreateResponse.getMinimumLike() != null ? goalCreateResponse.getMinimumLike().toString() : "");
        if(goalCreateResponse.getAppointmentTime()!=null){
            myTimeSet.setText(goalCreateResponse.getAppointmentTime());
        }
    }

    private void clickToMainButton(TextView toMain) {
        toMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_goalCreateCompleteActivity_to_navigation_home);
            }
        });
    }

    private void clickInviteButton(TextView toInvite) {
        toInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle inviteId = new Bundle();
                inviteId.putLong("goalId", goalCreateResponse.getId());
                Navigation.findNavController(view).navigate(R.id.action_goalCreateCompleteActivity_to_fragmentInviteUser, inviteId);
            }
        });
    }

    private void checkInstantOrConfirm(TextView myMethodSet) {
        if(goalCreateResponse.getGoalMethod().equals("INSTANT")){
            myMethodSet.setText("즉시 인증");
        }else{
            myMethodSet.setText("확인 후 인증");
        }
    }
}
