package kr.co.company.capstone;

import android.annotation.SuppressLint;
import android.os.Parcel;

import com.google.android.material.datepicker.CalendarConstraints;

import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

@NoArgsConstructor
@SuppressLint("ParcelCreator")
public class GoalCreateDateValidator implements CalendarConstraints.DateValidator{
    @Override @SuppressLint("NewApi")
    public boolean isValid(long date) {
        return date > LocalDate.now().minusDays(1L).atStartOfDay().atZone(ZoneId.ofOffset("UTC", ZoneOffset.UTC)).toInstant().toEpochMilli();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
