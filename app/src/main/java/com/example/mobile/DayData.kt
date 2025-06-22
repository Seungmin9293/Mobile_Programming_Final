package com.example.mobile

// 달력의 각 날짜 정보를 저장하는 데이터 클래스
data class DayData(
    val dayText: String,              // 표시될 날짜 텍스트
    val year: Int,                    // 해당 년도
    val month: Int,                   // 해당 월
    val dayOfMonth: Int,             // 해당 일자
    var haveSchedule: Boolean = false, // 일정 유무
    val CurrentMonth: Boolean = true   // 현재 월에 속한 날짜인지
)
