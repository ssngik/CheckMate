package kr.co.company.capstone.fragment;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.race604.drawable.wave.WaveDrawable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import kr.co.company.capstone.GlobalApplication;
import kr.co.company.capstone.R;
import kr.co.company.capstone.dto.goal.*;
import kr.co.company.capstone.util.adapter.CalendarRecyclerViewAdapter;
import kr.co.company.capstone.util.adapter.TeamMateRecyclerViewAdapter;
import kr.co.company.capstone.util.SharedPreferenceUtil;
import kr.co.company.capstone.dto.ErrorMessage;
import kr.co.company.capstone.dto.team_mate.TeamMateResponse;
import kr.co.company.capstone.service.GoalInquiryService;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GoalDetailFragment extends Fragment {
    private static final String LOG_TAG = "GoalDetailFragment";
    private TextView startDate, goalName, progressPercent, goalMethodInformation, methodInformationOnTop;
    private RecyclerView teamMatesRecyclerView, calendarRecyclerView;
    private Long teamMateId, goalId;
    private int editFlag;
    private TextView inviteButton;
    private Button registerButton;
    private String goalTitle = null;
    private WaveDrawable mWaveDrawable;
    private GoalDetailResponse goalDetailResponse;
    ProgressBar goalDetailLoading;
    Group goalDetailGroup;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        getActivity();
        Log.d(LOG_TAG, "onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate");
        if(getArguments()!=null){
            Bundle bundle = getArguments();
            goalId = bundle.getLong("goalId", 0L);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goal_detail, container, false);

        Log.i(LOG_TAG, "onCreateView");

        goalDetailGroup = view.findViewById(R.id.goal_detail_group);
        goalDetailLoading = view.findViewById(R.id.goal_detail_loading);
        goalDetailLoading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.green_400), PorterDuff.Mode.SRC_IN);
        progressPercent = view.findViewById(R.id.progress_percent);
        ImageView mImageView = view.findViewById(R.id.check_image);



        setPercentProgress(mImageView);
        goalDetailView(goalId, view);
        Log.d(LOG_TAG, "goalDetailView");
        // 수정 환경 변환 위한 Flag
        editFlag = 1;

        startDate = view.findViewById(R.id.start_date);
        goalName = view.findViewById(R.id.todo_name);
        goalMethodInformation = view.findViewById(R.id.goal_method_information);
        calendarRecyclerView = view.findViewById(R.id.calendar);
        calendarRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        teamMatesRecyclerView = view.findViewById(R.id.team_mates);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        teamMatesRecyclerView.setLayoutManager(gridLayoutManager);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(view).navigate(R.id.action_goalDetailActivity_to_navigation_home);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        inviteButton = view.findViewById(R.id.todo_invite);
        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InviteUserFragment inviteUserFragment = new InviteUserFragment();
                Bundle idBundle = new Bundle();
                idBundle.putLong("goalId", goalId);
                inviteUserFragment.setArguments(idBundle);
                Navigation.findNavController(view).navigate(R.id.action_goalDetailFragment_to_inviteUserFragment, idBundle);
            }
        });

        ImageButton timelineMoreButton = view.findViewById(R.id.timeline_more);
        timelineMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeLineFragment timeLineFragment = new TimeLineFragment();
                Bundle toTimeLine = new Bundle();
                toTimeLine.putLong("goalId", goalId);
                timeLineFragment.setArguments(toTimeLine);
                Navigation.findNavController(view).navigate(R.id.action_goalDetailFragment_to_timeLineFragment, toTimeLine);
            }
        });

        registerButton = view.findViewById(R.id.certification);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoMyGoalFragment doMyGoal = new DoMyGoalFragment();
                Bundle toCertification = new Bundle();
                toCertification.putLong("goalId", goalId);
                toCertification.putLong("teamMateId", teamMateId);
                toCertification.putString("goalTitle", goalTitle);
                doMyGoal.setArguments(toCertification);
                Navigation.findNavController(view).navigate(R.id.action_goalDetailFragment_to_doMyGoalFragment, toCertification);
            }
        });
//
//        ImageButton goalUpdateButton = view.findViewById(R.id.goal_update);
//        goalUpdateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SetNewGoalInfoFragment frag = new SetNewGoalInfoFragment();
//                Bundle bundle = new Bundle();
//
//                bundle.putSerializable("goalDetailResponse", goalDetailResponse);
//                bundle.putInt("editFlag", editFlag);
//                frag.setArguments(bundle);
//                Navigation.findNavController(view).navigate(R.id.action_goalDetailFragment_to_create_goal_info_fragment, bundle);
//                }
//            }
//        );
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
    }

    private void setPercentProgress(ImageView mImageView) {
        mWaveDrawable = new WaveDrawable(Objects.requireNonNull(getActivity()), R.drawable.check_icon);
        mWaveDrawable.setWaveAmplitude(10);
        mWaveDrawable.setWaveLength(200);
        mWaveDrawable.setWaveSpeed(3);
        mImageView.setImageDrawable(mWaveDrawable);
    }


    private void goalDetailView(long goalId, View view) {
        GoalInquiryService.getService().goalDetailView(goalId)
                .enqueue(new Callback<GoalDetailViewResponse>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(Call<GoalDetailViewResponse> call, Response<GoalDetailViewResponse> response) {
                        if (response.isSuccessful()) {
                            setGoalDetailData(response);
                            goalDetailGroup.setVisibility(View.VISIBLE);
                            goalDetailLoading.setVisibility(View.INVISIBLE);
                        } else {
                            if(response.code() == 404) {
                                Toast.makeText(GlobalApplication.getAppContext(), "해당 목표에 속한 팀원만 볼 수 있습니다", Toast.LENGTH_LONG).show();
                                Navigation.findNavController(view).popBackStack();
                            }
                            OnErrorFragment onErrorFragment = new OnErrorFragment();
                            onErrorFragment.show(getChildFragmentManager(), "error");
                            //Log.d(LOG_TAG, ErrorMessage.getErrorByResponse(response).toString());
                        }
                    }
                    @Override
                    public void onFailure(Call<GoalDetailViewResponse> call, Throwable t) {
                        OnErrorFragment onErrorFragment = new OnErrorFragment();
                        onErrorFragment.show(getChildFragmentManager(), "error");
                        //Log.d(LOG_TAG, t.getMessage());
                    }
                });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setGoalDetailData(Response<GoalDetailViewResponse> response) {
        GoalDetailViewResponse goalDetailViewResponse = response.body();

        setGoalInfo(goalDetailViewResponse.getGoalDetailResponse());
        setGoalCalendar(goalDetailViewResponse.getTeamMateCalendarResponse());
        double percent = goalDetailViewResponse.getProgress();
        progressPercent.setText(String.format("%.2f", percent) + "%");
        mWaveDrawable.setLevel((int) (percent * 100));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setGoalCalendar(TeamMateCalendarResponse teamMateCalendarResponse) {
        List<GoalDate> goalDateList = getGoalCalendarDateList(teamMateCalendarResponse);
        CalendarRecyclerViewAdapter calendarRecyclerViewAdapter = new CalendarRecyclerViewAdapter(getActivity(), goalDateList);
        calendarRecyclerView.setAdapter(calendarRecyclerViewAdapter);

        int position = goalDateList.indexOf(getCalendarOffset(goalDateList)) - 3;
        calendarRecyclerView.scrollToPosition(position);
    }

    private GoalDate getCalendarOffset(List<GoalDate> goalDateList) {
        return goalDateList.stream()
                .filter(date -> Objects.equals(date.getLocalDate(), LocalDate.now().toString()))
                .findAny()
                .orElse(null);
    }

    private List<GoalDate> getGoalCalendarDateList(TeamMateCalendarResponse teamMateCalendarResponse) {
        LocalDate startDate = LocalDate.parse(teamMateCalendarResponse.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String goalPeriod = teamMateCalendarResponse.getGoalPeriod();
        String teamMatePeriod = teamMateCalendarResponse.getTeamMatePeriod();

        return IntStream.range(0, goalPeriod.length())
                .mapToObj(i -> new GoalDate(startDate.plusDays(i).toString(),
                        goalPeriod.charAt(i) == '1',
                        teamMatePeriod.charAt(i) == '1'))
                .collect(Collectors.toList());
    }

    private void setGoalInfo(GoalDetailResponse goalDetailResponse) {
        if (goalDetailResponse != null) {
            Log.d(LOG_TAG, "goalDetailResponse : " + goalDetailResponse);
            this.goalDetailResponse = goalDetailResponse;
            teamMateId = setTeamMateId(goalDetailResponse);

            setRegisterButtonStatus(goalDetailResponse.getUploadable());
            setInviteButtonStatus(goalDetailResponse.isInviteable());

            startDate.setText(goalDetailResponse.getStartDate() + " 부터 진행 중 !");
            goalTitle = goalDetailResponse.getTitle();

            InstantOrConfirmGuide(goalDetailResponse);
            goalName.setText(goalTitle);

            TeamMateRecyclerViewAdapter adapter = new TeamMateRecyclerViewAdapter(getActivity(), goalDetailResponse.getTeamMates());
            teamMatesRecyclerView.setAdapter(adapter);
        }
    }

    private void InstantOrConfirmGuide(GoalDetailResponse goalDetailResponse) {
        if(goalDetailResponse.getGoalMethod().equals("CONFIRM")){
            goalMethodInformation.setText(goalTitle + " 목표는 \n팀원들의 확인을 받아야하는 목표에요.\n"+ goalDetailResponse.getMinimumLike()+ "개 이상의 좋아요를 받으면 성공입니다!");
        }else if(goalDetailResponse.getGoalMethod().equals("CONFIRM")&&goalDetailResponse.getGoalStatus().equals("ONGOING")){
            goalMethodInformation.setText(goalTitle+" 목표는\n즉시 인증할 수 있는 목표입니다. 지금 바로 인증해봅시다!");
        }else{
            goalMethodInformation.setText(goalTitle+" 목표는\n즉시 인증할 수 있는 목표입니다.\n목표를 수행하는 날이 온다면 잊지말고 인증해봐요!");
        }
    }

    private Long setTeamMateId(GoalDetailResponse goalDetailResponse) {
        return goalDetailResponse.getTeamMates().stream()
                .filter(tm -> tm.getUserId() == SharedPreferenceUtil.getLong(getActivity(), "userId")).findAny()
                .map(TeamMateResponse::getId)
                .orElseThrow(IllegalArgumentException::new);
    }

    private void setInviteButtonStatus(boolean invitable) {
        if (!invitable) inviteButton.setVisibility(View.INVISIBLE);
    }

    private void setRegisterButtonStatus(Uploadable uploadable) {
        if (!uploadable.isUploadable()) {
            registerButton.setEnabled(false);

            if (uploadable.isUploaded()) {
                registerButton.setText("오늘 이미 성공했어요 " + new String(Character.toChars(0x1F60A)));
                registerButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_round_green));
            } else if (!uploadable.isWorkingDay()) {
                registerButton.setText("인증요일이 아닙니다!");
                registerButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_round_gray));
            } else if (uploadable.isTimeOver()) {
                registerButton.setText("인증 시간이 지났어요 " + new String(Character.toChars(0x1F630)));
                registerButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_round_gray));
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.i(LOG_TAG, "onPause");
    }



}