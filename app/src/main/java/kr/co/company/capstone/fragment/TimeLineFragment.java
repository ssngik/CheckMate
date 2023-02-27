package kr.co.company.capstone.fragment;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import kr.co.company.capstone.dto.goal.GoalPeriodResponse;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import kr.co.company.capstone.R;
import kr.co.company.capstone.util.SharedPreferenceUtil;
import kr.co.company.capstone.TimeLineDateValidator;
import kr.co.company.capstone.dto.ErrorMessage;
import kr.co.company.capstone.dto.post.PostInquiryResponse;
import kr.co.company.capstone.dto.post.PostItem;
import kr.co.company.capstone.dto.post.PostListInquiryResponse;
import kr.co.company.capstone.dto.post.PostSubItem;
import kr.co.company.capstone.service.GoalInquiryService;
import kr.co.company.capstone.service.PostInquiryService;
import kr.co.company.capstone.util.adapter.PostsAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimeLineFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView recyclerView;
    private TextView goalTitle, dateTime;
    private final String LOG_TAG = "TimeLineFragment";
    private long goalId;
    private String nowDate, date, day;
    SwipeRefreshLayout swipeRefreshLayout;
    Group timeLineGroup;
    ProgressBar timeLineLoading;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            goalId = bundle.getLong("goalId", 0L);
            if(bundle.getString("date")!=null)date = bundle.getString("date").replaceAll("-","");
        } else {
            Log.d(LOG_TAG, "there is no getArguments");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        recyclerView = view.findViewById(R.id.timeline_recyclerView);
        ImageButton datePicker = view.findViewById(R.id.search_with_date_picker);
        goalTitle = view.findViewById(R.id.time_line_goal_title);
        dateTime = view.findViewById(R.id.date_text);
        timeLineLoading = view.findViewById(R.id.time_line_loading);
        timeLineGroup = view.findViewById(R.id.time_line_group);
        timeLineLoading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.green_400), PorterDuff.Mode.SRC_IN);
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        day = date == null ? today : date;
        nowDate = day;
        dateTime.setText(getProcessedDate(day));

        setDatePicker(datePicker);
        callTimeLineApi(day);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));

        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onRefresh() {
        Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
        callTimeLineApi(day);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        dateTime.setText(getProcessedDate(day));
        swipeRefreshLayout.setRefreshing(false);
    }

    private void setDatePicker(ImageButton datePicker) {
        datePicker.bringToFront();
        GoalInquiryService.getService().goalGoalCalendar(goalId)
                .enqueue(new Callback<GoalPeriodResponse>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(Call<GoalPeriodResponse> call, Response<GoalPeriodResponse> response) {
                        if (response.isSuccessful()) {
                            GoalPeriodResponse goalPeriodResponse = response.body();
                            String totalBinaryDate = goalPeriodResponse.getGoalPeriod();
                            LocalDate startDate = LocalDate.parse(goalPeriodResponse.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                            List<String> pickDate = IntStream.range(0, totalBinaryDate.length())
                                    .filter(i -> totalBinaryDate.charAt(i) == '1')
                                    .mapToObj(i -> startDate.plusDays(i).toString())
                                    .collect(Collectors.toList());

                            CalendarConstraints calendarConstraints = new CalendarConstraints.Builder()
                                    .setValidator(new TimeLineDateValidator(pickDate)).build();

                            setOnClickDatePicker(calendarConstraints);
                        } else {
                            OnErrorFragment onErrorFragment = new OnErrorFragment();
                            onErrorFragment.show(getChildFragmentManager(), "error");
                        }
                    }

                    private void setOnClickDatePicker(CalendarConstraints calendarConstraints) {
                        datePicker.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                MaterialDatePicker materialDatePicker = MaterialDatePicker.Builder.datePicker()
                                        .setCalendarConstraints(calendarConstraints)
                                        .setTitleText("이동할 날짜를 골라주세요!")
                                        .setSelection(getCalendarOpenAt()).build();

                                materialDatePicker.show(getParentFragmentManager(), "DATE_PICKER");
                                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                                    @Override
                                    public void onPositiveButtonClick(Long selection) {
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                                        Date date = new Date();
                                        date.setTime(selection);
                                        String selectedDate = simpleDateFormat.format(date);
                                        nowDate = selectedDate;
                                        dateTime.setText(getProcessedDate(selectedDate));
                                        callTimeLineApi(selectedDate);

                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<GoalPeriodResponse> call, Throwable t) {
                        OnErrorFragment onErrorFragment = new OnErrorFragment();
                        onErrorFragment.show(getChildFragmentManager(), "error");
                    }
                });
    }

    @SuppressLint("NewApi")
    private long getCalendarOpenAt() {
        return LocalDate.parse(nowDate, DateTimeFormatter.ofPattern("yyyyMMdd"))
                .atStartOfDay().atZone(ZoneId.ofOffset("UTC", ZoneOffset.UTC)).toInstant()
                .toEpochMilli();
    }

    @NotNull
    private String getProcessedDate(String day) {
        String year = day.substring(0, 4);
        String month = day.substring(4, 6);
        if (month.charAt(0) == '0') month = month.substring(1);
        String date = day.substring(6, day.length());
        if (date.charAt(0) == '0') date = date.substring(1);
        return year + "년 " + month + "월 " + date + "일의";
    }

    ArrayList<PostItem> posts = new ArrayList<>();

    private void callTimeLineApi(String date) {
        PostInquiryService.getService().getPosts(goalId, date)
                .enqueue(new Callback<PostListInquiryResponse>() {
                    @Override
                    public void onResponse(Call<PostListInquiryResponse> call, Response<PostListInquiryResponse> response) {
                        if (response.isSuccessful()) {
                            Log.d(LOG_TAG, goalTitle.toString());
                            goalTitle.setText(response.body().getGoalTitle());
                            List<PostInquiryResponse> postList = response.body().getPosts();
                            posts.clear();
                            postList.forEach(post -> posts.add(new PostItem(post.getPostId(), post.getUploaderNickname(), post.getText(),
                                            post.getLikedUsers().size(),
                                            post.getLikedUsers().contains(SharedPreferenceUtil.getLong(getActivity(), "userId")),
                                            post.getUploadAt(),
                                            new ArrayList<>(post.getImageUrls().stream().map(PostSubItem::new).collect(Collectors.toList()))
                                    ))
                            );
                            PostsAdapter postsAdapter = new PostsAdapter(posts, getActivity(), response.body().getMinimumLike());
                            recyclerView.setAdapter(postsAdapter);
                            timeLineGroup.setVisibility(View.VISIBLE);
                            timeLineLoading.setVisibility(View.INVISIBLE);
                        } else {
                            OnErrorFragment onErrorFragment = new OnErrorFragment();
                            onErrorFragment.show(getChildFragmentManager(), "error");
                        }
                    }

                    @Override
                    public void onFailure(Call<PostListInquiryResponse> call, Throwable t) {
                        OnErrorFragment onErrorFragment = new OnErrorFragment();
                        onErrorFragment.show(getChildFragmentManager(), "error");
                    }
                });
    }
}