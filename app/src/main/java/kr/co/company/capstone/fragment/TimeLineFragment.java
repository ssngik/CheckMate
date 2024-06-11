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
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import io.reactivex.Completable;
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

// TODO: 2023/06/12 코드 정리 - 리팩토링
public class TimeLineFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView recyclerView;
    private TextView goalTitle, dateTime;
    private final String LOG_TAG = "TimeLineFragment";
    private long goalId, userId;
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
            userId = bundle.getLong("userId", 0L);
            if(bundle.getString("date")!=null) {
                date = bundle.getString("date").replaceAll("-", "");
            }
            Log.d(LOG_TAG, goalId + date);
        } else {
            showErrorDialog("문제가 발생했습니다.");
        }
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        ImageButton datePicker = view.findViewById(R.id.search_with_date_picker);
        goalTitle = view.findViewById(R.id.time_line_goal_title);
        dateTime = view.findViewById(R.id.date_text);
        timeLineLoading = view.findViewById(R.id.time_line_loading);
        timeLineGroup = view.findViewById(R.id.time_line_group);
        timeLineLoading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.green_400), PorterDuff.Mode.SRC_IN);

        // 오늘 날짜 초기화 및 Calendar OpenAt 날짜
        initializeDate();

        // RecyclerView, RefreshView 초기화
        setRecyclerView(view);
        setSwipeRefreshLayout(view);

        // Date Picker 세팅
        setDatePicker(datePicker);

        // getPosts Method 호출
        callGetPostsMethod(day);

        return view;
    }

    private void initializeDate() {
        String today = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }

        day = date == null ? today : date; // date 가 없을 경우 오늘 날짜로 설정
        nowDate = day;
        dateTime.setText(getProcessedDate(day));
    }


    private void showErrorDialog(String errorMessage){
        ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.Companion.getErrorMessage(errorMessage);
        errorDialogFragment.show(getParentFragmentManager(), "ErrorDialogFragment");
    }

    // RecyclerView 설정
    private void setRecyclerView(View view){
        recyclerView = view.findViewById(R.id.timeline_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
    }

    // SwipeRefreshLayout 설정
    private void setSwipeRefreshLayout(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    // swipe refresh 설정
    @Override
    public void onRefresh() {
        // getPosts 메소드 호출
        callGetPostsMethod(day);

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
                            String totalBinaryDate = goalPeriodResponse.getSchedule(); // 목표 수행일
                            LocalDate startDate = LocalDate.parse(goalPeriodResponse.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                            List<String> pickDate = IntStream.range(0, totalBinaryDate.length())
                                    .filter(i -> totalBinaryDate.charAt(i) == '1')
                                    .mapToObj(i -> startDate.plusDays(i).toString())
                                    .collect(Collectors.toList());

                            CalendarConstraints calendarConstraints = new CalendarConstraints.Builder()
                                    .setValidator(new TimeLineDateValidator(pickDate)).build();

                            setOnClickDatePicker(calendarConstraints);
                        } else {
                            showErrorDialog("문제가 발생했습니다.");
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

                                        //
                                        callGetPostsMethod(selectedDate);

                                    }
                                });
                            }
                        });
                    }
                    @Override
                    public void onFailure(Call<GoalPeriodResponse> call, Throwable t) {
                        showErrorDialog("문제가 발생했습니다.");
                    }
                });
    }

    @SuppressLint("NewApi")
    private long getCalendarOpenAt() {
        return LocalDate.parse(nowDate, DateTimeFormatter.ofPattern("yyyyMMdd"))
                .atStartOfDay().atZone(ZoneId.ofOffset("UTC", ZoneOffset.UTC)).toInstant()
                .toEpochMilli();
    }

    // 날짜 파싱
    @NotNull
    private String getProcessedDate(String day) {
        String year = day.substring(0, 4);
        String month = day.substring(4, 6);

        if (month.charAt(0) == '0') month = month.substring(1);

        String date = day.substring(6);

        if (date.charAt(0) == '0') date = date.substring(1);
        return year + "년 " + month + "월 " + date + "일의 기록";
    }


    // getPosts 호출
    private void callGetPostsMethod(String date) {
        PostInquiryService.getService().getPosts(goalId, date)
                .enqueue(new Callback<PostListInquiryResponse<PostInquiryResponse>>() {
                    @Override
                    public void onResponse(Call<PostListInquiryResponse<PostInquiryResponse>> call, Response<PostListInquiryResponse<PostInquiryResponse>> response) {
                        if (response.isSuccessful()) {
                            goalTitle.setText(response.body().getGoalTitle());

                            ArrayList<PostItem> posts = new ArrayList<>();

                            PostListInquiryResponse<PostInquiryResponse> postListInquiryResponse = response.body();
                            List<PostInquiryResponse> postList = postListInquiryResponse.getPosts();

                            Log.d(LOG_TAG, postListInquiryResponse.toString());
                            Log.d(LOG_TAG, "postListInquiryResponse!");

                            //Log.d(LOG_TAG, postList.get(0).toString());
                            Log.d(LOG_TAG, postList.toString());

                            postList.forEach(post -> posts.add(new PostItem(post.getPostId(), post.getUploaderNickname(), post.getContent(),
                                            post.getLikedUserIds().size(),
                                            post.getLikedUserIds().contains(userId),
                                            post.getUploadAt(),
                                            new ArrayList<>(post.getImageUrls().stream().map(PostSubItem::new).collect(Collectors.toList()))
                                    ))
                            );

                            Log.d(LOG_TAG, Long.valueOf(userId).toString());
                            Log.d(LOG_TAG, "userid!!!!!!!!");
                            PostsAdapter postsAdapter = new PostsAdapter(posts, goalId, getActivity());
                            recyclerView.setAdapter(postsAdapter);

                            // 로딩 끝, 타임라인 View Group 보이기
                            TimeLineViewVisible();

                        } else {
                            showErrorDialog("정보를 불러올 수 없습니다.");
                        }
                    }

                    @Override
                    public void onFailure(Call<PostListInquiryResponse<PostInquiryResponse>> call, Throwable t) {

                    }

                    private void TimeLineViewVisible() {
                        timeLineGroup.setVisibility(View.VISIBLE);
                        timeLineLoading.setVisibility(View.INVISIBLE);
                    }

                });
    }

}