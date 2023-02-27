package kr.co.company.capstone;

import android.annotation.SuppressLint;
import android.os.Parcel;

import com.google.android.material.datepicker.CalendarConstraints;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@SuppressLint("ParcelCreator")
public class TimeLineDateValidator implements CalendarConstraints.DateValidator{
    List<Long> dates;

    @SuppressLint("NewApi")
    public TimeLineDateValidator(List<String> calendar) {
        dates = calendar.stream().map(day -> {
            List<Integer> date = Arrays.stream(day.split("-")).map(Integer::parseInt).collect(Collectors.toList());
            return LocalDate.of(date.get(0), date.get(1), date.get(2)).atStartOfDay().atZone(ZoneId.ofOffset("UTC", ZoneOffset.UTC)).toInstant()
                    .toEpochMilli();
        }).collect(Collectors.toList());
    }

    @Override
    public boolean isValid(long date) {
        return dates.contains(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
