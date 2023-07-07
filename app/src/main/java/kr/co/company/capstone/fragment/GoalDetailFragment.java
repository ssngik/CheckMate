package kr.co.company.capstone.fragment;

// FrameWork
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

// Android X
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Google Gson
import com.google.gson.Gson;

// WaveDrawable
import com.race604.drawable.wave.WaveDrawable;


// project
import kr.co.company.capstone.R;
import kr.co.company.capstone.dto.goal.*;
import kr.co.company.capstone.util.adapter.TeamMateRecyclerViewAdapter;
import kr.co.company.capstone.dto.team_mate.TeamMatesResponse;
import kr.co.company.capstone.service.GoalInquiryService;
import org.jetbrains.annotations.NotNull;

// Retrofit
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.*;

public class GoalDetailFragment extends Fragment {
    private static final String LOG_TAG = "GoalDetailFragment";
    private TextView startDate, goalName, progressPercent, goalMethodInformation;
    private RecyclerView teamMatesRecyclerView, calendarRecyclerView;
    private Long goalId, userTeamMateId, userId;
    private TextView inviteButton;
    private Button registerButton;
    private String goalTitle = null;
    private WaveDrawable mWaveDrawable;
    ProgressBar goalDetailLoading;
    Group goalDetailGroup;
    private ImageButton editMyGoalButton;
    GoalDetailResponse goalDetailResponse;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        Log.d(LOG_TAG, "onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments()!=null){
            Bundle bundle = getArguments();
            goalId = bundle.getLong("goalId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goal_detail, container, false);

        goalDetailGroup = view.findViewById (R.id.goal_detail_group);
        goalDetailLoading = view.findViewById (R.id.goal_detail_loading);
        goalDetailLoading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.green_400), PorterDuff.Mode.SRC_IN);
        progressPercent = view.findViewById (R.id.progress_percent);
        ImageView mImageView = view.findViewById (R.id.check_image);

        startDate = view.findViewById (R.id.start_date);
        goalName = view.findViewById (R.id.todo_name);
        goalMethodInformation = view.findViewById (R.id.goal_method_information);

        calendarRecyclerView = view.findViewById (R.id.calendar);
        calendarRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));

        teamMatesRecyclerView = view.findViewById (R.id.team_mates);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        teamMatesRecyclerView.setLayoutManager(gridLayoutManager);

        // 목표 수행률 체크 이미지 설정
        setPercentProgress(mImageView);

        // 데이터 호출해 UI에 표시
        goalDetailView(goalId);

        // OnBackPressedCallback 설정
        setOnBackPressedCallback(view);

        // 초대 버튼 눌렀을 때
        setClickInviteButton(view);

        // 타임라인 더보기 버튼 눌렀을 때
        setClickTimelineButton(view);

        // 목표 인증하기 버튼 눌렀을 때
        setClickRegisterButton(view);

        // 목표 수정 버튼 눌렀을 때
        setClickEditMyGoalButton(view);

        return view;

    }

    private void setClickEditMyGoalButton(View view) {
        editMyGoalButton = view.findViewById(R.id.goal_update);
        editMyGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditMyGoalFragment editMyGoalFragment = new EditMyGoalFragment();

                Bundle bundle = new Bundle();
                bundle.putSerializable("goalDetailResponse", goalDetailResponse);
                editMyGoalFragment.setArguments(bundle);
                Navigation.findNavController(view).navigate(R.id.action_goalDetailFragment_to_editMyGoalFragment, bundle );
            }
        });
    }

    // OnBackPressedCallback 설정
    private void setOnBackPressedCallback(View view) {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(view).navigate(R.id.action_goalDetailActivity_to_navigation_home);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    // 목표 인증하기 버튼 눌렀을 때
    private void setClickRegisterButton(View view) {
        registerButton = view.findViewById(R.id.certification);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoMyGoalFragment doMyGoal = new DoMyGoalFragment();
                Bundle toCertification = new Bundle();

                toCertification.putLong("goalId", goalId);
                toCertification.putLong("teamMateId", userTeamMateId);
                toCertification.putString("goalTitle", goalTitle);

                doMyGoal.setArguments(toCertification);
                Navigation.findNavController(view).navigate(R.id.action_goalDetailFragment_to_doMyGoalFragment, toCertification);
            }
        });
    }

    // 타임라인 더보기 버튼 눌렀을 때
    private void setClickTimelineButton(View view) {
        ImageButton timelineMoreButton = view.findViewById(R.id.timeline_more);
        timelineMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeLineFragment timeLineFragment = new TimeLineFragment();

                Bundle toTimeLine = new Bundle();
                toTimeLine.putLong("goalId", goalId);
                toTimeLine.putLong("userId", userId);
                timeLineFragment.setArguments(toTimeLine);

                Navigation.findNavController(view).navigate(R.id.action_goalDetailFragment_to_timeLineFragment, toTimeLine);
            }
        });
    }

    // 초대하기 버튼 눌렀을 때
    private void setClickInviteButton(View view) {
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
    }

    // 목표 수행률 체크 이미지 설정
    private void setPercentProgress(ImageView mImageView) {
        mWaveDrawable = new WaveDrawable(Objects.requireNonNull(getActivity()), R.drawable.check_icon);
        mWaveDrawable.setWaveAmplitude(10);
        mWaveDrawable.setWaveLength(200);
        mWaveDrawable.setWaveSpeed(3);
        mImageView.setImageDrawable(mWaveDrawable);
    }

    // 데이터 호출해 UI 표시
    private void goalDetailView(long goalId){
        GoalInquiryService.getService().goalDetail(goalId)
                .enqueue(new Callback<GoalDetailResponse>() {
                    @Override
                    public void onResponse(Call<GoalDetailResponse> call, Response<GoalDetailResponse> response) {
                        if(response.isSuccessful()){

                            // 목표 상세 response
                            goalDetailResponse = response.body();
                            // TeamMatesResponse 리스트에서 JSON 데이터를 파싱하여 mates 데이터 값을 가져옴
                            parseTeamMateResponse(goalDetailResponse.getMates());

                            // goalDetailResponse 의 필드 값으로 UI에 표시
                            setGoalDetailData(goalDetailResponse);

                            // 초대 가능 여부에 따라 초대 버튼 활성 / 비활성 설정
                            setInviteButtonStatus(goalDetailResponse.isInviteable());

                            // 사용자 목표 수행 상황에 따른 목표 수행하기 버튼 활성 제어
                            setRegisterButtonStatus(goalDetailResponse.getUploadable());

                            // 로딩 화면 제거
                            goalDetailGroup.setVisibility(View.VISIBLE);
                            goalDetailLoading.setVisibility(View.INVISIBLE);
                        }
                    }
                    @Override
                    public void onFailure(Call<GoalDetailResponse> call, Throwable t) {
                        Log.d(LOG_TAG, t.getMessage());
                        OnErrorFragment onErrorFragment = new OnErrorFragment();
                        onErrorFragment.show(getChildFragmentManager(), "error");
                    }
                });
    }

    // TeamMatesResponse 리스트에서 JSON 데이터를 파싱하여 mates 데이터 값을 가져옴
    private void parseTeamMateResponse(List<TeamMatesResponse> teamMatesResponse) {
        
        // 팀원 수
        int members = teamMatesResponse.size();

        // 전체 팀원을 담을 List
        List<TeamMatesResponse> teamMates = new ArrayList<>();
        
        for(int i=0; i<members; i++) 
        {
            // 파싱
            Gson gson = new Gson();
            String json = gson.toJson(teamMatesResponse.get(i));
            TeamMatesResponse teamMateResponse = gson.fromJson(json, TeamMatesResponse.class);

            // 사용자 userId ( 여기에선 타임라인 좋아요 본인 식별을 위한 값)
            userId = teamMateResponse.getUserId();

            // 사용자 정보
            if(i==0) userTeamMateId = teamMateResponse.getMateId();
            // 전체 팀원 List 에 추가
            teamMates.add(teamMateResponse);
        }
        
        // set Adapter
        TeamMateRecyclerViewAdapter adapter = new TeamMateRecyclerViewAdapter(getActivity(), teamMates);
        teamMatesRecyclerView.setAdapter(adapter);
    }

    // goalDetailResponse 의 필드 값으로 UI에 표시
    private void setGoalDetailData(GoalDetailResponse response) {

        // UI 에 사용자 목표 정보 표시
        startDate.setText(response.getStartDate());
        goalName.setText(response.getTitle());

        // 퍼센트 표시
        long percent = response.getProgress();
        progressPercent.setText(String.valueOf(percent));
        mWaveDrawable.setLevel((int) (percent * 100));
    }

    // 초대버튼 활성, 비활성
    private void setInviteButtonStatus(boolean invitable) {
        if (!invitable) inviteButton.setVisibility(View.INVISIBLE);
    }

    // 사용자 목표 수행 상황에 따른 목표 수행하기 버튼 제어
    private void setRegisterButtonStatus(Uploadable uploadable) {
        if (!uploadable.isUploadable()) {
            registerButton.setEnabled(false);

            if (uploadable.isUploaded()) {
                registerButton.setText("오늘 이미 성공했어요 " + new String(Character.toChars(0x1F60A)));
                registerButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_round_green));
            }
            else if (!uploadable.isWorkingDay()) {
                registerButton.setText("인증요일이 아닙니다!");
                registerButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_round_gray));
            }
            else if (uploadable.isTimeOver()) {
                registerButton.setText("인증 시간이 지났어요 " + new String(Character.toChars(0x1F630)));
                registerButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_round_gray));
            }
        }
    }


}