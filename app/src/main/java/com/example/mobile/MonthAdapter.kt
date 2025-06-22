package com.example.mobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

// 월별 달력 뷰 RecyclerView 어댑터
class MonthAdapter(
    private val baseCalendar: Calendar,                       // 기준 날짜
    private val numMonths: Int,                               // 표시 월 개수
    private val ClickCall: (DayData) -> Unit,                 // 날짜 클릭 콜백함수
    private val getMonth: (year: Int, month: Int) -> List<Int> // 일정 날짜 리스트 반환
) : RecyclerView.Adapter<MonthAdapter.MonthViewHolder>() {

    // 월 단위 ViewHolder 정의
    inner class MonthViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val monthYearText: TextView = itemView.findViewById(R.id.text_month_year_title)
        val daysRecycler: RecyclerView = itemView.findViewById(R.id.recycler_view_days) // 선택 달의 일자 출력

        init {
            daysRecycler.layoutManager = GridLayoutManager(itemView.context, 7) //일요일~토요일까지
            daysRecycler.setHasFixedSize(true)
        }

        // 월 데이터 바인딩
        fun bind(position: Int) {
            val calendar = baseCalendar.clone() as Calendar
            calendar.add(Calendar.MONTH, position - (numMonths / 2)) //월 이동

            val year = calendar.get(Calendar.YEAR)
            val month0Based = calendar.get(Calendar.MONTH)
            val month1Based = month0Based + 1               //1~12월

            monthYearText.text = "${year}년 ${month1Based}월"

            val daysInMonth = genDaysMonth(year, month0Based)
            val dayAdapter = DayAdapter(daysInMonth, ClickCall)
            daysRecycler.adapter = dayAdapter
        }

        // 날짜 셀
        private fun genDaysMonth(year: Int, month0Based: Int): List<DayData> {
            val dayList = mutableListOf<DayData>()
            val tempCalendar = Calendar.getInstance().apply {
                set(year, month0Based, 1)
            }

            // 이전 달 날짜 채움
            val firstDay = tempCalendar.get(Calendar.DAY_OF_WEEK) // 일요일~ 토요일
            val daysInPrMonthAdd = firstDay - Calendar.SUNDAY

            val prMonth = tempCalendar.clone() as Calendar
            prMonth.add(Calendar.MONTH, -1)
            val dayPrvMonth = prMonth.getMaximum(Calendar.DAY_OF_MONTH)
            for (i in 0 until daysInPrMonthAdd) {
                val day = dayPrvMonth - daysInPrMonthAdd + 1 + i
                dayList.add(DayData(
                    dayText = day.toString(),
                    year = prMonth.get(Calendar.YEAR),
                    month = prMonth.get(Calendar.MONTH) + 1,
                    dayOfMonth = day,
                    CurrentMonth = false //이전 달
                ))
            }

            //현재 달 날짜 채움
            val PlusCurrentMonth = getMonth(year, month0Based + 1)
            val daysInCurrentMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            for (day in 1..daysInCurrentMonth) {
                dayList.add(DayData(
                    dayText = day.toString(),
                    year = year,
                    month = month0Based + 1,
                    dayOfMonth = day,
                    haveSchedule = PlusCurrentMonth.contains(day),
                    CurrentMonth = true
                ))
            }

            //다음 달 날짜 채움
            val total = if (dayList.size <= 35) 35 else 42
            val cellsToFill = total - dayList.size
            val nextMonth = tempCalendar.clone() as Calendar
            nextMonth.add(Calendar.MONTH, 1)
            for (day in 1..cellsToFill) {
                dayList.add(DayData(
                    dayText = day.toString(),
                    year = nextMonth.get(Calendar.YEAR),
                    month = nextMonth.get(Calendar.MONTH) + 1,
                    dayOfMonth = day,
                    CurrentMonth = false // 다음 달
                ))
            }
            return dayList
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.page_month, parent, false)
        return MonthViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = numMonths
}
