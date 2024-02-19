package kr.co.company.capstone.dto.goal

import java.io.Serializable

data class MakeGoalRequest(
    val title : String,
    val category : String,
    val startDate : String,
    val endDate : String,
    val checkDays : String,
    val appointmentTime : String,
):Serializable{}

