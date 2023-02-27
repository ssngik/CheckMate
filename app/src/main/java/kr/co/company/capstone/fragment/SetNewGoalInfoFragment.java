package kr.co.company.capstone.fragment;

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

import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import androidx.navigation.ui.NavigationUI;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import kr.co.company.capstone.activity.MainActivity;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import kr.co.company.capstone.GoalCreateDateValidator;
import kr.co.company.capstone.R;
import kr.co.company.capstone.dto.ErrorMessage;
import kr.co.company.capstone.dto.goal.GoalCreateRequest;
import kr.co.company.capstone.dto.goal.GoalCreateResponse;
import kr.co.company.capstone.dto.goal.GoalDetailResponse;
import kr.co.company.capstone.dto.goal.GoalModifyRequest;
import kr.co.company.capstone.service.GoalCreateService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetNewGoalInfoFragment extends Fragment {
    private EditText title;
    private Spinner category;
    private RadioButton instant, confirm;
    private String goalMethod = "";
    private int editFlag;
    private String endDateInModify;
    static final String LOG_TAG = "SetNewGoalInfoActivity";
    public String appointmentTime, startDate, endDate, categoryText,titleText ;
    public int minimumLikeInt;

    private GoalDetailResponse goal;
    private LinearLayout likeNumGroup;
    private StringBuilder dayOfWeek = new StringBuilder();
    private CheckBox monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    private CheckBox setTimeCheckBox;
    private ImageButton setTimeButton, setDate, setDateEdit;
    private TextView startDateInfoText, myTimePickerText, endDateInfoText;
    private TextView completeModify, makeGoal, inVisibleTimeText, setCompleteButton;
    private EditText minimumLike;
    private RadioGroup check_mode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            goal = (GoalDetailResponse) bundle.getSerializable("goalDetailResponse");
            editFlag = bundle.getInt("editFlag");
            Log.d(LOG_TAG, "goal : " + goal.toString());
            Log.d(LOG_TAG, "flag : " + editFlag);
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
        completeModify = view.findViewById(R.id.complete_modify_my_goal_info);
        makeGoal = view.findViewById(R.id.make_goal);
        title = view.findViewById(R.id.Title);
        instant = view.findViewById(R.id.radioButton);
        confirm = view.findViewById(R.id.radioButton2);
        check_mode = view.findViewById(R.id.check_mode);
        category = view.findViewById(R.id.spinner);
        likeNumGroup = view.findViewById(R.id.like_num_group);
        category = view.findViewById(R.id.spinner);
        minimumLike = view.findViewById(R.id.like_num);
        setDate = view.findViewById(R.id.set_date);
        setDateEdit = view.findViewById(R.id.set_date_edit);
        inVisibleTimeText = view.findViewById(R.id.my_time_picker_text_invisible);

        myTimePickerText = view.findViewById(R.id.my_time_picker_text);
        setTimeButton = view.findViewById(R.id.set_time_button);

        setTimeCheckBox = view.findViewById(R.id.time_check_box);
        Long today = MaterialDatePicker.todayInUtcMilliseconds();

        // 목표 수정 화면
        if (editFlag == 1) {
            switchToEditMode(goal);
            setDateEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MaterialDatePicker materialDatePickerEdit = MaterialDatePicker.Builder.datePicker()
                            .setTitleText("수정할 날짜를 골라주세요!")
                            //.setSelection(Long.parseLong(endDateInModify.replace("-", "")))
                            .setSelection(Long.parseLong(today.toString()))
                            .build();
                    materialDatePickerEdit.show(getChildFragmentManager(), "DATE_PICKER");
                    Log.d(LOG_TAG, endDateInModify);
                    Log.d(LOG_TAG, today.toString());
                    materialDatePickerEdit.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                        @Override
                        public void onPositiveButtonClick(Long selection) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = new Date();
                            date.setTime(selection);
                            endDate = simpleDateFormat.format(date);
                            endDateInfoText.setText(endDate);
                        }
                    });
                }
            });

            completeModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (goal.getGoalMethod().equals("CONFIRM")) {
                        minimumLikeInt = Integer.parseInt(SetNewGoalInfoFragment.this.minimumLike.getText().toString());
                    }
                    callSaveModifyInfo(minimumLikeInt);
                }
            });
        }

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


        check_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButton2) {
                    likeNumGroup.setVisibility(View.VISIBLE);
                } else {
                    likeNumGroup.setVisibility(View.GONE);
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        setCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryText = category.getSelectedItem().toString();
                titleText = title.getText().toString();
                checkInstantOrConfirm();
                if (goalMethod.isEmpty() || titleText.trim().length() == 0) {
                    alert(builder);
                    if (goalMethod.isEmpty())
                        Log.d(LOG_TAG, "method is empty");
                    if (titleText.trim().length() == 0)
                        Log.d(LOG_TAG, "titleTextIsEmpty");
                } else {
                    getDayOfWeekByCheckBox();
                    callSaveGoalAPI(view);
                }
            }
        });

        // 날짜 선택 부분
        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
                builder.setTitleText("Pick your date!");
                //미리 날짜 선택하는 부분
                builder.setSelection(Pair.create(MaterialDatePicker.todayInUtcMilliseconds(), getNextWeekEpochMilli()));
                MaterialDatePicker materialDatePicker = builder.setCalendarConstraints(getCalendarConstraints()).build();
                materialDatePicker.show(getChildFragmentManager(), "DATE_PICKER");
                //확인 눌렀을 때
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        dateFormat(selection);
                    }
                });
            }
        });
        setTimeCheckBoxValidation();

        setTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(view);
            }

        });
        return view;
    }

    private void checkInstantOrConfirm() {
        if (instant.isChecked())
            goalMethod = "INSTANT";
        else if (confirm.isChecked())
            goalMethod = "CONFIRM";
    }

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

    private void callSaveModifyInfo(int minimumLike) {
        GoalModifyRequest request = new GoalModifyRequest(endDateInfoText.getText().toString(), myTimePickerText.getText().toString(), minimumLike, !setTimeCheckBox.isChecked());
        GoalCreateService.getService().modifyGoal(goal.getId(), request)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d(LOG_TAG, "goal.getEndDate: " + goal.getEndDate());
                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
                            builder.setTitle("끝").setMessage("목표 설정을 변경했어요!")
                                    .setPositiveButton("넵", positiveButton);
                            androidx.appcompat.app.AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        } else {
                            Log.d(LOG_TAG, ErrorMessage.getErrorByResponse(response).toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.d(LOG_TAG, t.getMessage());
                    }
                });
    }


    // 수정 화면으로 전환
    private void switchToEditMode(GoalDetailResponse goal) {
        makeGoal.setText("목표 수정");
        //setText(goal.getCategory());
        category.setEnabled(false);
        title.setText(goal.getTitle());
        title.setEnabled(false);
        startDateInfoText.setText(goal.getStartDate());
        endDateInfoText.setText(goal.getEndDate());
        endDateInModify = goal.getEndDate();
        setDate.setVisibility(View.INVISIBLE);
        setDateEdit.setVisibility(View.VISIBLE);
        completeModify.setVisibility(View.VISIBLE);
        setCompleteButton.setVisibility(View.INVISIBLE);

        if (goal.getGoalMethod().equals("CONFIRM")) {
            confirm.setChecked(true);
            instant.setEnabled(false);
            likeNumGroup.setVisibility(View.VISIBLE);
            minimumLike.setText(goal.getMinimumLike().toString());
        } else {
            instant.setChecked(true);
            confirm.setEnabled(false);
        }

        char[] weekDays = goal.getWeekDays().toCharArray();
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

        monday.setEnabled(false);
        tuesday.setEnabled(false);
        wednesday.setEnabled(false);
        thursday.setEnabled(false);
        friday.setEnabled(false);
        saturday.setEnabled(false);
        sunday.setEnabled(false);

        setTimeCheckBoxValidation();
        myTimePickerText.setText(goal.getAppointmentTime() != null ? goal.getAppointmentTime() : "");

    }


    private DialogInterface.OnClickListener positiveButton = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            View view = getView();
            GoalDetailFragment goalDetailFragment = new GoalDetailFragment();
            Bundle goalIdArg = new Bundle();
            goalIdArg.putLong("goalId", goal.getId());
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

    public void callSaveGoalAPI(View view) {
        if (!minimumLike.getText().toString().isEmpty())
            minimumLikeInt = Integer.parseInt(minimumLike.getText().toString());
        GoalCreateRequest request = new GoalCreateRequest(categoryText, titleText, goalMethod, startDate, endDate, appointmentTime, String.valueOf(dayOfWeek), minimumLikeInt);
        GoalCreateService.getService().saveGoal(request)
                .enqueue(new Callback<GoalCreateResponse>() {
                    @Override
                    public void onResponse(Call<GoalCreateResponse> call, Response<GoalCreateResponse> response) {
                        if (response.isSuccessful()) {
                            GoalCreateResponse goalCreateResponse = response.body();
                            GoalCreateCompleteFragment goalCreateCompleteFragment = new GoalCreateCompleteFragment();
                            Bundle resBundle = new Bundle();
                            resBundle.putSerializable("goalCreateResponse", goalCreateResponse);
                            goalCreateCompleteFragment.setArguments(resBundle);
                            Navigation.findNavController(view).navigate(R.id.action_setNewGoalInfoFragment_to_goalCreateCompleteActivity, resBundle);
                        } else {
                            //Log.d(LOG_TAG, ErrorMessage.getErrorByResponse(response).toString());
                            Log.d(LOG_TAG, "onResponse : fail");
                        }
                    }

                    @Override
                    public void onFailure(Call<GoalCreateResponse> call, Throwable t) {
                        Log.d(LOG_TAG, "onFailure : " + t.getMessage());
                    }
                });
    }

    public void getDayOfWeekByCheckBox() {
        if (monday.isChecked()) dayOfWeek.append(monday.getText());
        if (tuesday.isChecked()) dayOfWeek.append(tuesday.getText());
        if (wednesday.isChecked()) dayOfWeek.append(wednesday.getText());
        if (thursday.isChecked()) dayOfWeek.append(thursday.getText());
        if (friday.isChecked()) dayOfWeek.append(friday.getText());
        if (saturday.isChecked()) dayOfWeek.append(saturday.getText());
        if (sunday.isChecked()) dayOfWeek.append(sunday.getText());
    }

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

    public void showTimePicker(View view) {
        new TimePickerFragment().show(getParentFragmentManager(), "TimePicker");
    }

    private void alert(AlertDialog.Builder builder) {
        builder.setTitle("띵");
        builder.setMessage("모든 항목을 작성해주세요.");
        builder.setPositiveButton("넵", null);
        builder.create().show();
    }


}