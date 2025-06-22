package com.example.mobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import java.util.Calendar

// 시험 일정 페이지
class ScheduleActivity : StarBaseActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private var sampleNotifications = mutableListOf<String>()  //알림 예시
    private val scheduleItemLayouts = mutableListOf<ConstraintLayout>()  //스케줄 항목 뷰에 저장
    private var currentlyVisibleItemCount = 2  //초기에 보일 항목 수

    private lateinit var calendarAdapter: MonthAdapter
    private var selectedDateData: DayData? = null

    //시험 일정 정보 공유용 companion object
    companion object {
        val examSchedules = mutableListOf<ExamSchedule>()

        data class ExamSchedule(
            val name: String,
            val examDate: String,
            val registrationDate: String,
            val year: Int,
            val month: Int,
            val day: Int
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.schedule_page)

        //초기 함수 호출
        setToolbarDrawer()
        setupCalendar()
        setEditSchedule()
        setupBottomBar()
        NotiDrawer()
    }

    // 툴바 및 알림 드로어 설정
    private fun setToolbarDrawer() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerLayout = findViewById(R.id.activity_main_drawer_layout)
        val alarmButton: ImageButton = findViewById(R.id.toolbar_add_icon)

        //드로우 열기, 닫기
        alarmButton.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                drawerLayout.openDrawer(GravityCompat.END)
            }
        }
    }

    // 달력 뷰페이저
    private fun setupCalendar() {
        val viewPagerCalendar: ViewPager2 = findViewById(R.id.view_pager_calendar)
        val baseCalendar = Calendar.getInstance()
        val numMonthsToDisplay = 25  // 표시할 개월 개수

        calendarAdapter = MonthAdapter(
            baseCalendar,
            numMonthsToDisplay,
            this::onDateClicked,
            this::getMonth
        )
        viewPagerCalendar.adapter = calendarAdapter
        viewPagerCalendar.setCurrentItem(numMonthsToDisplay / 2, false)

        viewPagerCalendar.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                calendarAdapter.notifyDataSetChanged()
            }
        })
    }

    // 날짜 클릭 시 이벤트 처리
    private fun onDateClicked(dayData: DayData) {
        if (dayData.CurrentMonth) {
            selectedDateData = dayData
            val dateStr = "${dayData.year}년 ${dayData.month}월 ${dayData.dayOfMonth}일"

            // 선택된 날짜의 시험 정보 확인
            val examsForDate = examSchedules.filter {
                it.year == dayData.year && it.month == dayData.month && it.day == dayData.dayOfMonth
            }

            if (examsForDate.isNotEmpty()) {
                val examInfo = examsForDate.joinToString("\n\n") { exam ->
                    "${exam.name}\n시험일자: ${exam.examDate}\n접수일자: ${exam.registrationDate}"
                }

                AlertDialog.Builder(this)
                    .setTitle("시험 정보 (${dateStr})")
                    .setMessage(examInfo)
                    .setPositiveButton("확인", null)
                    .show()
            }

            Toast.makeText(this, "선택된 날짜: $dateStr", Toast.LENGTH_SHORT).show()
        }
    }

    // 해당월 시험 날짜 반환
    private fun getMonth(year: Int, month1Based: Int): List<Int> {
        return examSchedules
            .filter { it.year == year && it.month == month1Based }
            .map { it.day }
            .distinct()
    }

    // 스케줄 텍스트 항목, 저장 버튼
    private fun setEditSchedule() {
        val itemIds = listOf(
            R.id.schedule_item_layout_1, R.id.schedule_item_layout_2, R.id.schedule_item_layout_3,
            R.id.schedule_item_layout_4, R.id.schedule_item_layout_5, R.id.schedule_item_layout_6,
            R.id.schedule_item_layout_7
        )
        itemIds.forEach { id ->
            scheduleItemLayouts.add(findViewById(id))
        }

        //첫 번째 항목의 플러스 버튼으로 항목 추가하기
        val plusButton1: ImageButton = findViewById(R.id.button_action_item_1)
        plusButton1.setOnClickListener {
            showHideen()
        }

        //각 항목별 편집/저장
        for (i in 0 until scheduleItemLayouts.size) {
            val itemLayout = scheduleItemLayouts[i]
            val itemNumber = i + 1
            setupSchedule(itemLayout, itemNumber)
        }
    }

    //시험명 텍스트 및 저장 버튼
    private fun setupSchedule(itemLayout: ConstraintLayout, itemNumber: Int) {
        val textView: TextView = itemLayout.findViewById(
            resources.getIdentifier("schedule_text_item_$itemNumber", "id", packageName)
        )
        val saveButton: ImageButton = itemLayout.findViewById(
            resources.getIdentifier("button_save_item_$itemNumber", "id", packageName)
        )

        makeEdit(textView, itemNumber)

        saveButton.setOnClickListener {
            val scheduleText = textView.text.toString().trim()

            if (scheduleText.isEmpty() || scheduleText == "시험명을 입력하세요...") {
                Toast.makeText(this, "시험명을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedDateData == null || !selectedDateData!!.CurrentMonth) {
                Toast.makeText(this, "먼저 달력에서 날짜를 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 시험 정보 입력 다이얼로그
            ExamDialog(scheduleText, selectedDateData!!)
        }
    }

    // 텍스트뷰 클릭 시 시험명 입력 가능하게 변경
    private fun makeEdit(textView: TextView, itemNumber: Int) {
        textView.apply {
            text = "시험명을 입력하세요..."
            setTextColor(resources.getColor(android.R.color.darker_gray))
            isClickable = true
            isFocusable = true

            setOnClickListener {
                showEdit(this, itemNumber)
            }
        }
    }

    // 시험명 직접 입력 다이얼로그
    private fun showEdit(textView: TextView, itemNumber: Int) {
        val editText = EditText(this).apply {
            setText(if (textView.text == "시험명을 입력하세요...") "" else textView.text)
            hint = "시험명을 입력하세요"
            setPadding(24, 24, 24, 24)
        }

        AlertDialog.Builder(this)
            .setTitle("시험명 입력")
            .setView(editText)
            .setPositiveButton("확인") { _, _ ->
                val inputText = editText.text.toString().trim()
                if (inputText.isNotEmpty()) {
                    textView.text = inputText
                    textView.setTextColor(resources.getColor(android.R.color.black))
                } else {
                    textView.text = "시험명을 입력하세요..."
                    textView.setTextColor(resources.getColor(android.R.color.darker_gray))
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    //시험 정보 입력 다이얼로그 (시험일자 및 접수일자)
    private fun ExamDialog(examName: String, selectedDate: DayData) {
        val dialogView = ExamDialog()
        val editTextPair = dialogView.tag as Pair<EditText, EditText>
        val examDateEdit = editTextPair.first
        val registrationDateEdit = editTextPair.second

        val selectedDates = String.format("%02d.%02d.%02d",
            selectedDate.year % 100, selectedDate.month, selectedDate.dayOfMonth)
        examDateEdit.setText(selectedDates)

        AlertDialog.Builder(this)
            .setTitle("시험 정보 입력")
            .setView(dialogView)
            .setPositiveButton("저장") { _, _ ->
                val examDate = examDateEdit.text.toString().trim()
                val registrationDate = registrationDateEdit.text.toString().trim()

                if (examDate.isNotEmpty() && registrationDate.isNotEmpty()) {
                    saveExam(examName, examDate, registrationDate, selectedDate)
                } else {
                    Toast.makeText(this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    //다이얼로그 뷰
    private fun ExamDialog(): View {
        val dialogView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(72, 48, 72, 48)
        }

        val examLabel = TextView(this).apply {
            text = "시험 날짜"
            textSize = 16f
            setTextColor(resources.getColor(android.R.color.black))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val examDateEdit = EditText(this).apply {
            hint = "예: 25.07.15"
            setPadding(36, 36, 36, 36)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 144
            ).apply {
                topMargin = 24
                bottomMargin = 48
            }
        }

        val register = TextView(this).apply {
            text = "접수 날짜"
            textSize = 16f
            setTextColor(resources.getColor(android.R.color.black))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val registerinput = EditText(this).apply {
            hint = "예: 25.06.01"
            setPadding(36, 36, 36, 36)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 144
            ).apply {
                topMargin = 24
            }
        }

        dialogView.addView(examLabel)
        dialogView.addView(examDateEdit)
        dialogView.addView(register)
        dialogView.addView(registerinput)

        dialogView.tag = Pair(examDateEdit, registerinput)

        return dialogView
    }

    //시험 정보 저장 처리
    private fun saveExam(
        examName: String,
        examDate: String,
        registrationDate: String,
        selectedDate: DayData
    ) {
        val newSchedule = ExamSchedule(
            name = examName,
            examDate = examDate,
            registrationDate = registrationDate,
            year = selectedDate.year,
            month = selectedDate.month,
            day = selectedDate.dayOfMonth
        )

        examSchedules.add(0, newSchedule)

        val examInfo = "$examName\n시험일자: $examDate\n접수일자: $registrationDate"
        ExamViewActivity.examList.add(0, examInfo)

        calendarAdapter.notifyDataSetChanged()

        Toast.makeText(this,
            "${selectedDate.year}-${selectedDate.month}-${selectedDate.dayOfMonth}에 '$examName' 저장완료",
            Toast.LENGTH_LONG).show()

        resetTextView(examName)
    }

    //저장 후 텍스트뷰 초기화
    private fun resetTextView(examName: String) {
        for (i in 0 until currentlyVisibleItemCount) {
            val itemLayout = scheduleItemLayouts[i]
            val textView: TextView = itemLayout.findViewById(
                resources.getIdentifier("schedule_text_item_${i + 1}", "id", packageName)
            )
            if (textView.text.toString() == examName) {
                textView.text = "시험명을 입력하세요..."
                textView.setTextColor(resources.getColor(android.R.color.darker_gray))
                break
            }
        }
    }

    //숨겨진 항목 보이기
    private fun showHideen() {
        if (currentlyVisibleItemCount < scheduleItemLayouts.size) {
            val nextItemToMakeVisible = scheduleItemLayouts[currentlyVisibleItemCount]
            if (nextItemToMakeVisible.visibility == View.GONE) {
                nextItemToMakeVisible.visibility = View.VISIBLE
                currentlyVisibleItemCount++
                setupSchedule(nextItemToMakeVisible, currentlyVisibleItemCount)
            }
        } else {
            Toast.makeText(this, "더 이상 추가할 항목이 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    //하단 앱바
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

    //드로어 설정
    private fun NotiDrawer() {
        val recyclerViewNotifications: RecyclerView = findViewById(R.id.main_activity_recycler_view_notifications)
        val textNoNotifications: TextView = findViewById(R.id.main_activity_text_no_notifications)
        recyclerViewNotifications.layoutManager = LinearLayoutManager(this)

        // 예시 알림 추가
        if (sampleNotifications.isEmpty()) {
            sampleNotifications.add("ScheduleActivity: 새로운 공지사항")
            sampleNotifications.add("ScheduleActivity: D-Day 알림")
        }

        // 알림 표시 여부에 따라 설정
        if (sampleNotifications.isNotEmpty()) {
            val adapter = NotificationAdapter(sampleNotifications)
            recyclerViewNotifications.adapter = adapter
            recyclerViewNotifications.visibility = View.VISIBLE
            textNoNotifications.visibility = View.GONE
        } else {
            recyclerViewNotifications.visibility = View.GONE
            textNoNotifications.visibility = View.VISIBLE
        }
    }

    // 뒤로가기 시 드로어 열려있으면 닫기
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }
}
