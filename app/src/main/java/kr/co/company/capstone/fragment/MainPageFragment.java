package kr.co.company.capstone.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;
import java.util.Objects;

import kr.co.company.capstone.R;
import kr.co.company.capstone.activity.MainActivity;
import kr.co.company.capstone.util.adapter.BackPressHandler;
import kr.co.company.capstone.util.adapter.GoalRecyclerViewAdapter;
import kr.co.company.capstone.util.adapter.ViewPagerAdapter;
import kr.co.company.capstone.dto.ErrorMessage;
import kr.co.company.capstone.dto.goal.*;
import kr.co.company.capstone.service.GoalInquiryService;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@NoArgsConstructor
public class MainPageFragment extends Fragment {

    private static final String LOG_TAG = "MainPageFragment";
    private LinearLayout layoutIndicator;
    private GoalRecyclerViewAdapter adapter;
    private RecyclerView todayRecyclerView, ongoingRecyclerView;
    ViewPager2 viewPagerBanner;
    TypedArray mainBannerImages;
    Activity activity;
    Group mainPageGroup;
    ProgressBar mainPageLoading;

    private final int[] images = new int[] {
            R.drawable.check_mate_banner,
            R.drawable.check_mate_banner_two,
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_icon, container, false);

        mainBannerImages = getResources().obtainTypedArray(R.array.bannerImages);
        layoutIndicator = view.findViewById(R.id.layoutIndicators);
        viewPagerBanner = view.findViewById(R.id.view_pager_banner);
        todayRecyclerView = view.findViewById(R.id.recycler_today);
        ongoingRecyclerView = view.findViewById(R.id.recycler_ongoing);
        mainPageLoading = view.findViewById(R.id.main_page_loading);
        mainPageGroup = view.findViewById(R.id.main_page_group);

        // 로딩 화면
        mainPageLoading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.green_400), PorterDuff.Mode.SRC_IN);
        callTodayGoalsApi();
        callOngoingGoalsApi();
        setBackPressHandler();

        ImageButton newGoalButton = view.findViewById(R.id.setNewGoalButton);
        newGoalButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.setNewGoalInfoFragment));
        return view;
    }

    private void setBackPressHandler() {
        BackPressHandler backPressHandler = new BackPressHandler(getActivity());
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backPressHandler.onBackPressed();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        todayRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        ongoingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        viewPagerBanner.setOffscreenPageLimit(1);
        viewPagerBanner.setAdapter(new ViewPagerAdapter(getContext(), mainBannerImages));


        viewPagerBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });
        setupIndicators(images.length);
    }

    private void callOngoingGoalsApi() {
        GoalInquiryService.getService().ongoingGoals()
                .enqueue(new Callback<GoalInfoListResponse<SimpleGoalInfoResponse>>() {
                    @Override
                    public void onResponse(Call<GoalInfoListResponse<SimpleGoalInfoResponse>> call, Response<GoalInfoListResponse<SimpleGoalInfoResponse>> response) {
                        if (response.isSuccessful()) {
                            GoalInfoListResponse<SimpleGoalInfoResponse> goalInfoListResponse = response.body();
                            if (goalInfoListResponse != null) {
                                List<SimpleGoalInfoResponse> goals = goalInfoListResponse.getGoals();
                                adapter = new GoalRecyclerViewAdapter(getContext(), goals);
                                ongoingRecyclerView.setAdapter(adapter);
                            }
                        } else {
                            OnErrorFragment onErrorFragment = new OnErrorFragment();
                            onErrorFragment.show(getChildFragmentManager(), "error");
                        }
                    }
                    @Override
                    public void onFailure(Call<GoalInfoListResponse<SimpleGoalInfoResponse>> call, Throwable t) {
                        Log.d(LOG_TAG, "fail to load ongoingGoals " + t.getMessage());
                    }
                });
    }

    private void callTodayGoalsApi() {
        GoalInquiryService.getService().todayGoals()
                .enqueue(new Callback<GoalInfoListResponse<TodayGoalInfoResponse>>() {
                    @Override
                    public void onResponse(Call<GoalInfoListResponse<TodayGoalInfoResponse>> call, Response<GoalInfoListResponse<TodayGoalInfoResponse>> response) {
                        if (response.isSuccessful()) {
                            GoalInfoListResponse<TodayGoalInfoResponse> goalInfoListResponse = response.body();
                            if (goalInfoListResponse != null) {
                                List<TodayGoalInfoResponse> goals = goalInfoListResponse.getGoals();
                                adapter = new GoalRecyclerViewAdapter(getContext(), goals);
                                todayRecyclerView.setAdapter(adapter);
                                mainPageGroup.setVisibility(View.VISIBLE);
                                mainPageLoading.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            Log.d(LOG_TAG, ErrorMessage.getErrorByResponse(response).toString());
                        }
                    }
            @Override
            public void onFailure(Call<GoalInfoListResponse<TodayGoalInfoResponse>> call, Throwable t) {
                Log.d(LOG_TAG, "fail to load todayGoals " + t.getMessage());
            }
        });
    }



    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(16, 8, 16, 8);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getActivity(),
                    R.drawable.bg_indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutIndicator.addView(indicators[i]);
        }
        setCurrentIndicator(0);
    }

    private void setCurrentIndicator(int position) {
        int childCount = layoutIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.bg_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.bg_indicator_inactive));
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onDestroyView(){
        super.onDestroyView();
    }


}
