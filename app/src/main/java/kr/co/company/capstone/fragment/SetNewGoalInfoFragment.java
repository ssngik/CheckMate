package kr.co.company.capstone.fragment;

// Android SDK import 문
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

// androidx import 문
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

// 기타 라이브러리 import 문
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

// 프로젝트 내부 import 문
import kr.co.company.capstone.GoalCreateDateValidator;
import kr.co.company.capstone.R;
import kr.co.company.capstone.dto.ErrorMessage;
import kr.co.company.capstone.dto.goal.GoalCreateRequest;
import kr.co.company.capstone.dto.goal.GoalCreateResponse;
import kr.co.company.capstone.dto.goal.GoalDetailResponse;
import kr.co.company.capstone.service.GoalCreateService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetNewGoalInfoFragment extends Fragment {

    // LOG_TAG
    static final String LOG_TAG = "SetNewGoalInfoActivity";

    // UI components
    private EditText title;
    private Spinner category;
    private RadioButton instant, confirm;
    private CheckBox monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    private CheckBox setTimeCheckBox;
    private ImageButton setTimeButton, setDate;
    private TextView startDateInfoText, myTimePickerText, endDateInfoText;
    private TextView inVisibleTimeText;
    private TextView setCompleteButton;
    private EditText minimumLike;
    private RadioGroup check_mode;
    private LinearLayout likeNumGroup;

    // 목표 생성 관련
    private String goalMethod = "";
    private String categoryText, titleText, appointmentTime, startDate, endDate;
    private int minimumLikeInt;
    private StringBuilder dayOfWeek = new StringBuilder();

    private GoalDetailResponse goal;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            goal = (GoalDetailResponse) bundle.getSerializable("goalDetailResponse");
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_goal_info, container, false);

        //  --------------------------- 요일 선택 ---------------------------
        monday = view.findViewById(R.id.monday);
        tuesday = view.findViewById(R.id.tuesday);
        wednesday = view.findViewById(R.id.wednesday);
        thursday = view.findViewById(R.id.thursday);
        friday = view.findViewById(R.id.friday);
        saturday = view.findViewById(R.id.saturday);
        sunday = view.findViewById(R.id.sunday);

        startDateInfoText = view.findViewById(R.id.start_date_info_text);
        endDateInfoText = view.findViewById(R.id.end_date_info_text);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        setCompleteButton = view.findViewById(R.id.set_complete_button);
        TextView makeGoal = view.findViewById(R.id.make_goal);
        title = view.findViewById(R.id.Title);
        instant = view.findViewById(R.id.radioButton);
        confirm = view.findViewById(R.id.radioButton2);
        check_mode = view.findViewById(R.id.check_mode);
        category = view.findViewById(R.id.spinner);
        likeNumGroup = view.findViewById(R.id.like_num_group);
        category = view.findViewById(R.id.spinner);
        minimumLike = view.findViewById(R.id.like_num);
        setDate = view.findViewById(R.id.set_date);
        inVisibleTimeText = view.findViewById(R.id.my_time_picker_text_invisible);

        myTimePickerText = view.findViewById(R.id.my_time_picker_text);
        setTimeButton = view.findViewById(R.id.set_time_button);

        setTimeCheckBox = view.findViewById(R.id.time_check_box);

        // 카테고리 초기화
        initializeCategoryAdapter(view);

        // 확인 / 즉시 인증 체크 박스 ChangedListener
        setCheckedChangeListener();

        // 생성 완료 버튼 리스너 초기화
        initializeDoneButtonListener();

        // 날짜 선택 Material Date Picker 초기화
        initializeDatePickerListener();

        // 인증 시간 설정 여부 체크 박스 설정
        setTimeCheckBoxValidation();

        // 타임 피커 클릭 리스너
        setTimeButton.setOnClickListener(v -> showTimePicker(view));

        return view;
    }

    private void initializeCategoryAdapter(View view) {
        Spinner spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.Category_Spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setCheckedChangeListener() {
        check_mode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButton2) // 확인 후 인증인 경우
            {
                likeNumGroup.setVisibility(View.VISIBLE);
            }
            else // 즉시 인증인 경우
            {
                likeNumGroup.setVisibility(View.GONE);
            }
        });
    }

    // 생성 완료 버튼 리스너
    private void initializeDoneButtonListener() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        setCompleteButton.setOnClickListener(view1 -> {
            categoryText = category.getSelectedItem().toString();
            titleText = title.getText().toString();

            // 즉시 인증 || 확인 후 인증 여부
            checkInstantOrConfirm();

            if (titleText.trim().length() == 0) {
                alert(builder);
            } else {
                getDayOfWeekByCheckBox();
                callSaveGoalAPI(view1);
            }
        });
    }

    private void initializeDatePickerListener() {
        // 날짜 선택 부분
        setDate.setOnClickListener(view1 -> {
            MaterialDatePicker.Builder<Pair<Long, Long>> builder1 = MaterialDatePicker.Builder.dateRangePicker();
            builder1.setTitleText("Pick your date!");

            // 초기 날짜 선택하는 부분
            builder1.setSelection(Pair.create(MaterialDatePicker.todayInUtcMilliseconds(), getNextWeekEpochMilli()));
            MaterialDatePicker materialDatePicker = builder1.setCalendarConstraints(getCalendarConstraints()).build();
            materialDatePicker.show(getChildFragmentManager(), "DATE_PICKER");

            //확인 눌렀을 때
            materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                @Override
                public void onPositiveButtonClick(Pair<Long, Long> selection) {
                    dateFormat(selection);
                }
            });
        });
    }

    // 즉시 인증 / 확인 후 인증 여부
    private void checkInstantOrConfirm() {
        if (instant.isChecked())
            goalMethod = "INSTANT";
        else if (confirm.isChecked())
            goalMethod = "CONFIRM";
    }

    // 인증 시간 설정 여부 체크 박스 설정
    private void setTimeCheckBoxValidation() {
        setTimeCheckBox.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!setTimeCheckBox.isChecked()) {
//                    myTimePickerText.setText("");
                    myTimePickerText.setVisibility(View.INVISIBLE);
                    inVisibleTimeText.setVisibility(View.VISIBLE);
                    setTimeButton.setEnabled(false);
                } else {
                    myTimePickerText.setVisibility(View.VISIBLE);
                    inVisibleTimeText.setVisibility(View.INVISIBLE);
                    setTimeButton.setEnabled(true);
                }
            }
        });
    }



    private final DialogInterface.OnClickListener positiveButton = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            View view = getView();

            GoalDetailFragment goalDetailFragment = new GoalDetailFragment();

            Bundle goalIdArg = new Bundle();
            goalIdArg.putLong("goalId", goal.getGoalId());
            goalDetailFragment.setArguments(goalIdArg);

            assert view != null;
            Navigation.findNavController(view).navigate(R.id.action_setNewGoalInfoFragment_to_goalDetailFragment, goalIdArg);
        }

    };

    @NotNull
    private CalendarConstraints getCalendarConstraints() {
        return new CalendarConstraints.Builder()
                .setValidator(new GoalCreateDateValidator()).build();
    }

    @SuppressLint("NewApi")
    private long getNextWeekEpochMilli() {
        return LocalDate.now().plusDays(7).atStartOfDay().atZone(ZoneId.ofOffset("UTC", ZoneOffset.UTC)).toInstant()
                .toEpochMilli();
    }

    private void callSaveGoalAPI(View view) {
        if (!minimumLike.getText().toString().isEmpty())
            minimumLikeInt = Integer.parseInt(minimumLike.getText().toString());

        GoalCreateRequest request = new GoalCreateRequest(categoryText, titleText, startDate, endDate, String.valueOf(dayOfWeek), appointmentTime);
        // 새 목표 생성
        GoalCreateService.getService().saveGoal(request)
                .enqueue(new Callback<GoalCreateResponse>() {
                    @Override
                    public void onResponse(Call<GoalCreateResponse> call, Response<GoalCreateResponse> response) {
                        if (response.isSuccessful()) {
                            // 목표 생성 완료 화면으로 이동
                            goToGoalCreateComplete(response, request, view);

                        } else {
                            Log.d(LOG_TAG, ErrorMessage.getErrorByResponse(response).toString());
                            Log.d(LOG_TAG, "onResponse : fail");
                        }
                    }

                    @Override
                    public void onFailure(Call<GoalCreateResponse> call, Throwable t) {
                        Log.d(LOG_TAG, "onFailure : " + t.getMessage());
                    }
                });
    }

    private void goToGoalCreateComplete(Response<GoalCreateResponse> response, GoalCreateRequest request, View view) {
        GoalCreateResponse goalCreateResponse = response.body();

        // 목표 ID
        long goalId = goalCreateResponse.getGoalId();

        GoalCreateCompleteFragment goalCreateCompleteFragment = new GoalCreateCompleteFragment();
        Bundle resBundle = new Bundle();

        // 목표 ID
        resBundle.putLong("goalId", goalId);

        // request 객체
        resBundle.putSerializable("request", request);

        // 목표 생성 완료로 이동
        goalCreateCompleteFragment.setArguments(resBundle);
        Navigation.findNavController(view).navigate(R.id.action_setNewGoalInfoFragment_to_goalCreateCompleteActivity, resBundle);
    }

    // 목표 수행 요일 저장
    public void getDayOfWeekByCheckBox() {
        if (monday.isChecked()) dayOfWeek.append(monday.getText());
        if (tuesday.isChecked()) dayOfWeek.append(tuesday.getText());
        if (wednesday.isChecked()) dayOfWeek.append(wednesday.getText());
        if (thursday.isChecked()) dayOfWeek.append(thursday.getText());
        if (friday.isChecked()) dayOfWeek.append(friday.getText());
        if (saturday.isChecked()) dayOfWeek.append(saturday.getText());
        if (sunday.isChecked()) dayOfWeek.append(sunday.getText());
    }

    // 날짜 Format
    private void dateFormat(Pair<Long, Long> selection) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDateValue = new Date();
        Date endDateValue = new Date();

        startDateValue.setTime(selection.first);
        endDateValue.setTime(selection.second);

        startDate = simpleDateFormat.format(startDateValue);
        endDate = simpleDateFormat.format(endDateValue);

        startDateInfoText.setText(startDate);
        endDateInfoText.setText(endDate);
    }

    // TimePicker
    public void showTimePicker(View view) {
        new TimePickerFragment().show(getParentFragmentManager(), "TimePicker");
    }

    // setAlert Message
    private void alert(AlertDialog.Builder builder) {
        builder.setTitle("띵");
        builder.setMessage("모든 항목을 작성해주세요.");
        builder.setPositiveButton("넵", null);
        builder.create().show();
    }


}