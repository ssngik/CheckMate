package kr.co.company.capstone.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import kr.co.company.capstone.R;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog tpd = new TimePickerDialog(getActivity(),
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar, this,
                hour, minute, DateFormat.is24HourFormat(getActivity()));

        // 뒤 배경 삭제
        tpd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // 타임 피커의 타이틀 설정
        TextView tvTitle = new TextView(getActivity());
        tvTitle.setText("TimepickerDialog 타이틀");
        tvTitle.setPadding(5, 3, 5, 3);
        tvTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        return tpd;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        TextView timeSetCheck = getActivity().findViewById(R.id.my_time_picker_text);
        String stringMin;
        String stringHour;

        if (minute >= 10)
            stringMin = minute + "";
        else
            stringMin = "0" + minute;

        if (hourOfDay >= 10)
            stringHour = hourOfDay + "";
        else
            stringHour = "0" + hourOfDay;

        timeSetCheck.setText((stringHour)+":"+(stringMin));
    }
}
