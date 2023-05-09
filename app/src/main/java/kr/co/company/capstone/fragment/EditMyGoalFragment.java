package kr.co.company.capstone.fragment;

import android.os.Bundle;
import android.widget.*;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.navigation.Navigation;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import kr.co.company.capstone.R;
import kr.co.company.capstone.dto.ErrorMessage;
import kr.co.company.capstone.dto.goal.GoalDetailResponse;
import kr.co.company.capstone.dto.goal.GoalModifyRequest;
import kr.co.company.capstone.service.GoalCreateService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class EditMyGoalFragment extends Fragment {

    private ImageButton setTimeButton;
    private String myAppointmentTime;
    private static GoalDetailResponse goalDetailResponse;
    final boolean[] timeReset = new boolean[2]; // 0 번째 : 초기 인증시간 선택 여부 , 1 번째 : 최종 인증시간 선택 여부
    public EditMyGoalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            goalDetailResponse = (GoalDetailResponse) bundle.getSerializable("goalDetailResponse");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_my_goal, container, false);


        // 목표 인증시간 TextView
        final TextView myTimePickerText;

        // 목표 종료 날짜
        final TextView endDateInfoText = view.findViewById(R.id.end_date_info_text);

        // 목표 인증시간 CheckBox
        final CheckBox myTimeCheckBox;

        //  --------------------------- 요일 선택 ---------------------------
        final CheckBox monday = view.findViewById (R.id.monday);
        final CheckBox tuesday = view.findViewById (R.id.tuesday);
        final CheckBox wednesday = view.findViewById (R.id.wednesday);
        final CheckBox thursday = view.findViewById (R.id.thursday);
        final CheckBox friday = view.findViewById (R.id.friday);
        final CheckBox saturday = view.findViewById (R.id.saturday);
        final CheckBox sunday = view.findViewById (R.id.sunday);
        //  --------------------------- 요일 선택 ---------------------------

        // 사용자 인증 시간 입력 Text
        myTimePickerText = view.findViewById (R.id.my_time_picker_text);
        // 인증 시간 사용 여부 체크박스
        myTimeCheckBox = view.findViewById (R.id.time_check_box);

        // 사용자 목표 정보 데이터 초기화 ( 수정 안 되는 부분 )
        initializeViews(view);

        // 인증시간 체크박스 초기화, timeReset 초기 설정
        initializeTimeCheckBox(myTimeCheckBox);

        // 목표 종료 날짜 수정 버튼 클릭 리스너 초기화
        setDateEditButtonClickListener(view, endDateInfoText);

        // TimePicker 초기화
        setUpTimePickerListener(view, myTimePickerText);

        // 인증시간 삭제 여부 / 시간 설정 여부에 따른 체크 박스 리스너 설정
        setUpTimeCheckBoxListener(myTimePickerText, myTimeCheckBox);

        // 내 목표 정보에서 받아온 요일 정보 CheckBox 설정
        setCheckWeekDays(monday, tuesday, wednesday, thursday, friday, saturday, sunday);

        // 수정 완료시
        setDoneButton(view, myTimePickerText, endDateInfoText, myTimeCheckBox);

        return view;
    }

    // 날짜 수정 버튼 리스너 초기화
    private void setDateEditButtonClickListener(View view, TextView endDateInfoText) {
        ImageButton setEditDate = view.findViewById (R.id.set_date_edit);
        setEditDate.setOnClickListener(v -> setMaterialDatePicker(endDateInfoText));
    }

    // MaterialDatePicker 초기화
    private void setMaterialDatePicker(TextView endDateInfoText) {
        Long today = MaterialDatePicker.todayInUtcMilliseconds();

        MaterialDatePicker materialDatePickerEdit = MaterialDatePicker.Builder.datePicker()
                .setTitleText("수정할 종료 날짜를 골라주세요 !")
                .setSelection(today) // 오늘 날짜
                .build();

        materialDatePickerEdit.show(getChildFragmentManager(), "DATE_PICKER");
        materialDatePickerEdit.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<Long>) selection -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            date.setTime(selection);

            String endDate = simpleDateFormat.format(date);
            endDateInfoText.setText(endDate);
        });
    }

    // 수정 완료시
    private void setDoneButton(View view, TextView myTimePickerText, TextView endDateInfoText, CheckBox myTimeCheckBox) {
        TextView doneButton = view.findViewById(R.id.set_complete_button);
        // API 호출
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeCheckBoxValidity(myTimeCheckBox);
                callApi(endDateInfoText, myTimePickerText, view);
            }
        });
    }

    private void callApi(TextView endDateInfoText, TextView myTimePickerText, View view) {
        GoalModifyRequest goalModifyRequest = new GoalModifyRequest(endDateInfoText.getText().toString(), timeReset[1], myTimePickerText.getText().toString() );
        GoalCreateService.getService().modifyGoal(goalDetailResponse.getGoalId(), goalModifyRequest)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.isSuccessful())
                        {
                            // 수정 성공 안내 후 GoalDetail 로 이동
                            setAlertDialogAndReplaceView(view, "완료!", "목표를 변경했어요 !");
                        }else
                        {
                            // 에러코드
                            String errorCode = ErrorMessage.getErrorByResponse(response).getCode();

                            // 변경할 수 없는 기간인 경우
                            if(Objects.equals(errorCode, "C-003"))
                                setAlertDialogAndReplaceView(view, "띵", "변경할 수 없는 기간이에요.");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        // onFailure
                        setAlertDialogAndReplaceView(view, "띵", "문제가 발생했어요.");
                    }

                });
    }

    // AlertDialog 및 View 변경
    private void setAlertDialogAndReplaceView(View view, String title, String message) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());

        builder.setTitle(title).setMessage(message)
                .setPositiveButton("넵", (dialog, which) -> {
                    // Replace View to GoalDetailFragment
                    GoalDetailFragment goalDetailFragment = new GoalDetailFragment();

                    Bundle bundle = new Bundle();
                    bundle.putLong("goalId", goalDetailResponse.getGoalId());

                    goalDetailFragment.setArguments(bundle);
                    Navigation.findNavController(view).navigate(R.id.action_editMyGoalFragment_to_goalDetailFragment, bundle);
                });
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // setTimePicker
    private void showTimePicker() {
        new TimePickerFragment().show(getParentFragmentManager(), "TimePicker");
    }


    // 인증시간 삭제 여부 / 시간 설정 여부에 따른 체크 박스 리스너 설정
    private void setUpTimeCheckBoxListener(TextView myTimePickerText, CheckBox myTimeCheckBox) {
        myTimeCheckBox.setOnClickListener(v -> {
            // 인증 시간 체크 박스 선택 시
            if (myTimeCheckBox.isChecked())
            {
                myTimePickerText.setVisibility(View.VISIBLE); // 사용자 설정 인증 시간 표시
                setTimeButton.setEnabled(true); // 시간 변경 이미지 버튼 활성화
            }else // 인증 시간 체크 박스 미선택 시
            {
                myTimePickerText.setVisibility(View.INVISIBLE); // 사용자 설정 인증 시간 미표시
                setTimeButton.setEnabled(false); // 시간 변경 이미지 버튼 비활성화
            }
        });
    }

    private void timeCheckBoxValidity(CheckBox myTimeCheckBox){
        if ( !myTimeCheckBox.isChecked()){ // 체크되어 있지 않은 경우
            if (timeReset[0]){ // 초기 사용자의 인증 시간이 있는 경우{
                timeReset[1] = true;
            }else{
                timeReset[1] = false;
            }
        }
    }

    /*
        사용자 목표 Detail 정보 View 초기화 부분

     */

    // 인증시간 체크박스 초기화
    private void initializeTimeCheckBox(CheckBox timeCheckBox){
        if (goalDetailResponse.getAppointmentTime() != null){
            timeReset[0] = true;
            timeCheckBox.setChecked(true);
        }else{
            timeReset[0] = false;
        }
    }


    // TimePicker 초기화
    private void setUpTimePickerListener(View view, TextView myTimePickerText) {

        // TimePickerButton 초기화
        setTimeButton = view.findViewById (R.id.set_time_button);

        // 인증 시간 존재 여부에 따른 초기화
        if(goalDetailResponse.getAppointmentTime() != null){
            myTimePickerText.setText(goalDetailResponse.getAppointmentTime());
            myAppointmentTime = myTimePickerText.getText().toString();
        }else{
            myTimePickerText.setText("인증시간 없음");
            myAppointmentTime = "null";
            setTimeButton.setEnabled(false);
        }

        // TimePickerButton ClickListener
        setTimeButton.setOnClickListener(v -> showTimePicker());

    }

    // 사용자 목표 정보 초기화
    private void initializeViews(View view) {
        final TextView myGoalTitleText; // 목표 제목 정보
        final TextView myCategoryText;  // 카테고리 정보
        final TextView myStartDateInfo; // 목표 시작 날짜 정보
        final TextView endDateInfoText; // 목표 종료 날짜 정보

        // 카테고리 설정
        myCategoryText = view.findViewById (R.id.my_category_text);
        myCategoryText.setText(goalDetailResponse.getCategory());

        // 목표 이름 설정
        myGoalTitleText = view.findViewById (R.id.Title);
        myGoalTitleText.setText(goalDetailResponse.getTitle());

        // 목표 기간 설정
        myStartDateInfo = view.findViewById (R.id.start_date_info_text);
        myStartDateInfo.setText(goalDetailResponse.getStartDate());
        endDateInfoText = view.findViewById (R.id.end_date_info_text);
        endDateInfoText.setText(goalDetailResponse.getEndDate());


    }


    // 요일 초기화
    private void setCheckWeekDays(CheckBox monday, CheckBox tuesday, CheckBox wednesday, CheckBox thursday, CheckBox friday, CheckBox saturday, CheckBox sunday) {
        char[] weekDays = goalDetailResponse.getWeekDays().toCharArray();
        for (char weekDay : weekDays) {
            switch (weekDay) {
                case '월':
                    monday.setChecked(true);
                    break;
                case '화':
                    tuesday.setChecked(true);
                    break;
                case '수':
                    wednesday.setChecked(true);
                    break;
                case '목':
                    thursday.setChecked(true);
                    break;
                case '금':
                    friday.setChecked(true);
                    break;
                case '토':
                    saturday.setChecked(true);
                    break;
                case '일':
                    sunday.setChecked(true);
                    break;
            }
        }
    }


}