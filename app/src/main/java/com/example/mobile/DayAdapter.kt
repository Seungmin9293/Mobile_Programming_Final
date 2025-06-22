package com.example.mobile

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// 날짜 셀 생성 RecyclerView 어댑터
class DayAdapter(
    private val days: List<DayData>,                  // 날짜 데이터 리스트
    private val onDateClick: (DayData) -> Unit        // 날짜 클릭 -> 콜백 함수
) : RecyclerView.Adapter<DayAdapter.DayViewHolder>() {

    // 각 날짜 셀을 관리 ViewHolder 클래스
    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayTextView: TextView = itemView.findViewById(R.id.text_day_cell)             // 날짜 텍스트 뷰
        val scheduleIndicator: View = itemView.findViewById(R.id.view_schedule_indicator) // 일정 표시용 점

        // 날짜 데이터 바인딩
        fun bind(dayData: DayData) {
            dayTextView.text = dayData.dayText
            itemView.isClickable = dayData.CurrentMonth  // 현재 월 날짜만 클릭 허용함

            if (dayData.CurrentMonth) {
                // 현재 달 날짜 스타일
                dayTextView.setTextColor(Color.BLACK)

                if (dayData.haveSchedule) {
                    // 일정이 있으면 초록색 점 텍스트 색상
                    scheduleIndicator.visibility = View.VISIBLE
                    dayTextView.setTextColor(Color.parseColor("#4CAF50"))
                } else {
                    // 일정이 없으면 점 숨김:visible이용, 기본 색상
                    scheduleIndicator.visibility = View.GONE
                    dayTextView.setTextColor(Color.BLACK)
                }

                // 날짜 클릭 리스너
                itemView.setOnClickListener {
                    onDateClick(dayData)
                }
            } else {
                // 다른 달 날짜는 회색 처리 클릭 불가
                dayTextView.setTextColor(Color.LTGRAY)
                scheduleIndicator.visibility = View.GONE
                itemView.setOnClickListener(null)
            }
        }
    }

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day_cell, parent, false)
        return DayViewHolder(itemView)
    }

    //데이터 바인딩
    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(days[position])
    }

    // 항목 수 반환
    override fun getItemCount(): Int = days.size
}
