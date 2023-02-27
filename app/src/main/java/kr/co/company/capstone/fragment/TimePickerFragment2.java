package kr.co.company.capstone.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import kr.co.company.capstone.R;

public class TimePickerFragment2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_create_goal_info);
    }

    public void onBtnClicked(View v){
        TimePickerFragment newFragment = new TimePickerFragment();
        // show : fragmentManager에 추가된 대화상자 출력
        newFragment.show(getSupportFragmentManager(), "TimePicker");
    }

}
