package com.example.mobile

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog

import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import java.util.Calendar
import java.util.UUID


//스터디 테스크
data class StudyTask(
    val id: String = UUID.randomUUID().toString(), // 고유 식별자
    var text: String,                              // 스터디 내용
    var isCompleted: Boolean = false,              // 완료 여부
    val year: Int,                                 // 기록된 연도
    val month: Int,                                // 기록된 월
    val day: Int                                   // 기록된 일
)


class StudyActivity : StarBaseActivity() {


    // 드로어 레이아웃 (알림용)
    private lateinit var drawerLayout: DrawerLayout

    // ViewPager 달력 어댑터
    private lateinit var calendarAdapter: MonthAdapter

    // 사용자가 클릭한 날짜 정보
    private var selectedDate: DayData? = null

    // 샘플 알림 목록
    private var noti = mutableListOf<String>()

    // SharedPreferences:오늘한 공부 시간 저장
    private lateinit var sharePrefer: SharedPreferences

    // "오늘의 공부 기록" 표시할 TextView
    private lateinit var todayStudy: TextView


    private val studyLayouts = mutableListOf<ConstraintLayout>()   // 전체 레이아웃
    private val studyTextViews = mutableListOf<TextView>()         // 각 스터디 텍스트뷰
    private val studySaveButtons = mutableListOf<ImageButton>()    // 저장 버튼
    private val studyActionButtons = mutableListOf<ImageButton>()  // +, - 버튼

    //각 인덱스에 해당하는 StudyTask
    private val studyData = mutableListOf<StudyTask?>()

    //최대 추가 가능한 스터디 항목 수
    private var maxStudy = 6

    //초기 표시 기본 값
    private var visibleStudy = 1

    //날짜별 StudyTask 저장소
    companion object {
        val studyTasksByDate = mutableListOf<StudyTask>()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.study)


        sharePrefer = getSharedPreferences("StudyTimerPrefs", MODE_PRIVATE)

        // 오늘 공부 기록 표시용 텍스트뷰 연결
        todayStudy = findViewById(R.id.text_recent_record_value)

        //타이머 버튼 클릭 -> 타이머 페이지로 이동
        val timerButton: LinearLayout = findViewById(R.id.btn_timer)
        timerButton.setOnClickListener {
            startActivity(Intent(this, TimerActivity::class.java))
        }


        setToolbarDrawer()
        setupCalendar()
        setStudyList()
        setupBottomBar()
        setupNotificationDrawerContent()

        // 오늘 공부 시간 불러오기
        TodayStudy()
    }

    // 화면으로 돌아올 때 공부 기록 다시 갱신
    override fun onResume() {
        super.onResume()
        TodayStudy()
    }

    // SharedPreferences에서 오늘의 공부 시간 불러와 TextView에 표시
    private fun TodayStudy() {
        val todayTotalTime = sharePrefer.getString("today_total_study_time", "00:00:00") ?: "00:00:00"
        todayStudy.text = "오늘의 공부 기록: $todayTotalTime"
    }

    // 툴바 + 알림 드로어 버튼 설정
    private fun setToolbarDrawer() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        drawerLayout = findViewById(R.id.activity_main_drawer_layout)

        // 알림 아이콘 클릭 시 드로어 열기/닫기
        val alarmButton: ImageButton = findViewById(R.id.addbtn1)
        alarmButton.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                drawerLayout.openDrawer(GravityCompat.END)
            }
        }
    }

    //달력 ViewPager2
    private fun setupCalendar() {
        val viewPagerCalendar: ViewPager2 = findViewById(R.id.view_pager_calendar)
        val baseCalendar = Calendar.getInstance()
        val numMonthsToDisplay = 25

        calendarAdapter = MonthAdapter(
            baseCalendar,
            numMonthsToDisplay,
            this::StudyDateClick,  // 날짜 클릭 콜백
            this::getDayMonth      // 일정 있는 날짜 계산
        )
        viewPagerCalendar.adapter = calendarAdapter
        viewPagerCalendar.setCurrentItem(numMonthsToDisplay / 2, false)

        viewPagerCalendar.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                calendarAdapter.notifyDataSetChanged()
            }
        })
    }

    // 날짜 클릭 시 -> 해당 날짜의 StudyTask 목록 조회 및 다이얼로그 표시
    private fun StudyDateClick(dayData: DayData) {
        if (dayData.CurrentMonth) {
            selectedDate = dayData
            val dateStr = "${dayData.year}년 ${dayData.month}월 ${dayData.dayOfMonth}일"

            val studiesForDate = studyTasksByDate.filter {
                it.year == dayData.year && it.month == dayData.month && it.day == dayData.dayOfMonth
            }

            if (studiesForDate.isNotEmpty()) {
                val studyInfo = studiesForDate.joinToString("\n") { study ->
                    "${study.text}${if (study.isCompleted) " ✓" else ""}"
                }

                AlertDialog.Builder(this)
                    .setTitle("스터디 기록 (${dateStr})")
                    .setMessage(studyInfo)
                    .setPositiveButton("확인", null)
                    .show()
            }

            Toast.makeText(this, "선택된 날짜: $dateStr", Toast.LENGTH_SHORT).show()
        }
    }

    // 특정 연/월의 스터디 기록 날짜 목록 반환
    private fun getDayMonth(year: Int, month1Based: Int): List<Int> {
        return studyTasksByDate
            .filter { it.year == year && it.month == month1Based }
            .map { it.day }
            .distinct()
    }

    // 각 스터디 항목 레이아웃, 버튼, 이벤트 설정
    private fun setStudyList() {
        val itemLayoutIds = listOf(
            R.id.study_task_item_layout_1,
            R.id.study_task_item_layout_2,
            R.id.study_task_item_layout_3
        )
        maxStudy = itemLayoutIds.size
        studyData.clear()
        repeat(maxStudy) { studyData.add(null) }

        // 뷰 리스트 초기화
        studyLayouts.clear()
        studyTextViews.clear()
        studySaveButtons.clear()
        studyActionButtons.clear()

        itemLayoutIds.forEachIndexed { index, layoutId ->
            val layout = findViewById<ConstraintLayout>(layoutId)
            studyLayouts.add(layout)

            val textView = layout.findViewById<TextView>(resources.getIdentifier("text_study_task_item_${index + 1}", "id", packageName))
            val saveButton = layout.findViewById<ImageButton>(resources.getIdentifier("button_save_study_task_item_${index + 1}", "id", packageName))
            val actionButton = layout.findViewById<ImageButton>(resources.getIdentifier("button_action_study_task_item_${index + 1}", "id", packageName))

            studyTextViews.add(textView)
            studySaveButtons.add(saveButton)
            studyActionButtons.add(actionButton)

            layout.visibility = if (index < visibleStudy) View.VISIBLE else View.GONE

            // 텍스트뷰 클릭 시 입력 다이얼로그 알림
            textView.apply {
                text = "스터디 내용을 입력하세요..."
                setTextColor(resources.getColor(android.R.color.darker_gray))
                isClickable = true
                setOnClickListener {
                    showAddEditDialog(studyData[index], index)
                }
            }

            // 저장 버튼 클릭 리스너
            saveButton.setOnClickListener {
                val taskText = textView.text.toString().trim()
                if (taskText.isEmpty() || taskText == "스터디 내용을 입력하세요...") {
                    Toast.makeText(this, "스터디 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (selectedDate == null || !selectedDate!!.CurrentMonth) {
                    Toast.makeText(this, "먼저 달력에서 날짜를 선택해주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener //토스트 메세지 출력
                }
                saveStudy(taskText, selectedDate!!, index)
            }

            if (index == 0) {
                actionButton.setImageResource(R.drawable.plus)
                actionButton.setOnClickListener {
                    var targetIndex = studyData.indexOfFirst { it == null }
                    if (targetIndex == -1 && visibleStudy < maxStudy) {
                        targetIndex = visibleStudy
                        studyLayouts[targetIndex].visibility = View.VISIBLE
                        visibleStudy++
                    }

                    if (targetIndex != -1) {
                        showAddEditDialog(null, targetIndex)
                    } else {
                        Toast.makeText(this, "모두 추가되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                actionButton.setImageResource(R.drawable.remove)
                actionButton.setOnClickListener {
                    deleteStudy(index)
                }
            }
        }

        refreshAllStudy()
    }

    //스터디 입력,수정 다이얼로그 표시
    private fun showAddEditDialog(existingTask: StudyTask?, taskIndex: Int) {
        val editText = EditText(this)
        editText.setText(
            if (existingTask == null && studyTextViews[taskIndex].text == "스터디 내용을 입력하세요...") ""
            else studyTextViews[taskIndex].text
        )
        AlertDialog.Builder(this)
            .setTitle(if (existingTask == null) "스터디 내용 입력" else "스터디 수정")
            .setView(editText)
            .setPositiveButton("확인") { _, _ ->
                val taskText = editText.text.toString()
                if (taskText.isNotBlank()) {
                    studyTextViews[taskIndex].text = taskText
                    studyTextViews[taskIndex].setTextColor(resources.getColor(android.R.color.black))
                } else {
                    studyTextViews[taskIndex].text = "스터디 내용을 입력하세요..."
                    studyTextViews[taskIndex].setTextColor(resources.getColor(android.R.color.darker_gray))
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    // StudyTask 저장
    private fun saveStudy(taskText: String, selectedDate: DayData, index: Int) {
        val newTask = StudyTask(
            text = taskText,
            year = selectedDate.year,
            month = selectedDate.month,
            day = selectedDate.dayOfMonth
        )
        studyTasksByDate.add(0, newTask)
        calendarAdapter.notifyDataSetChanged()
        Toast.makeText(this, "${selectedDate.year}-${selectedDate.month}-${selectedDate.dayOfMonth}에 '$taskText' 저장완료", Toast.LENGTH_LONG).show()
        studyTextViews[index].text = "스터디 내용을 입력하세요..."
        studyTextViews[index].setTextColor(resources.getColor(android.R.color.darker_gray))
    }

    // StudyTask 삭제
    private fun deleteStudy(index: Int) {
        if (index in 0 until maxStudy) {
            val task = studyData[index]
            if (task != null) {
                AlertDialog.Builder(this)
                    .setTitle("스터디 삭제")
                    .setMessage("'${task.text}' 스터디를 삭제하시겠습니까?")
                    .setPositiveButton("삭제") { _, _ ->
                        studyData[index] = null
                        refreshStudy(index)
                        Toast.makeText(this, "스터디 삭제됨.", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("취소", null)
                    .show()
            }
        }
    }

    private fun refreshStudy(index: Int) {
        val task = studyData[index]
        val textView = studyTextViews[index]
        val saveButton = studySaveButtons[index]

        if (task != null) {
            textView.text = task.text
            textView.paintFlags = if (task.isCompleted)
                textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            else
                textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        } else {
            textView.text = "스터디 내용을 입력하세요..."
            textView.setTextColor(resources.getColor(android.R.color.darker_gray))
            textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        saveButton.setImageResource(R.drawable.save)
    }

    // 모든 스터디 새로고침
    private fun refreshAllStudy() {
        for (i in 0 until maxStudy) {
            refreshStudy(i)
        }
    }

    //하단앱바
    private fun setupBottomBar() {
        val starButton: ImageButton = findViewById(R.id.star)
        val homeButton: ImageButton = findViewById(R.id.home)
        val personButton: ImageButton = findViewById(R.id.person)

        starButton.setOnClickListener {
            startActivity(Intent(this, BookMarkPageActivity::class.java))
        }
        homeButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            })
        }
        personButton.setOnClickListener {
            startActivity(Intent(this, MyPageActivity::class.java))
        }
    }

    //알림 드로어
    private fun setupNotificationDrawerContent() {
        val recyclerView: RecyclerView = findViewById(R.id.study_activity_recycler_view_notifications)
        val emptyText: TextView = findViewById(R.id.study_activity_text_no_notifications)

        recyclerView.layoutManager = LinearLayoutManager(this)

        if (noti.isEmpty()) {
            noti.add("StudyActivity: 새로운 알림 1")
            noti.add("StudyActivity: 새로운 알림 2")
        }

        if (noti.isNotEmpty()) {
            recyclerView.adapter = NotificationAdapter(noti)
            recyclerView.visibility = View.VISIBLE
            emptyText.visibility = View.GONE
        } else {
            recyclerView.visibility = View.GONE
            emptyText.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }
}
