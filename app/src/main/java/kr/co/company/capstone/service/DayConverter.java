package kr.co.company.capstone.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DayConverter {
    MONDAY("MONDAY", "월"),
    TUESDAY("TUESDAY", "화"),
    WEDNESDAY("WEDNESDAY", "수"),
    THURSDAY("THURSDAY", "목"),
    FRIDAY("FRIDAY", "금"),
    SATURDAY("SATURDAY", "토"),
    SUNDAY("SUNDAY", "일");

    private final String eng;
    private final String kor;

    public static String convertEngToKor(String engDay) {
        return DayConverter.valueOf(engDay).getKor();
    }
}
