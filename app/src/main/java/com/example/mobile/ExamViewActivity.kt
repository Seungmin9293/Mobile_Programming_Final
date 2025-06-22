package com.example.mobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// 시험일정화면
class ExamViewActivity : StarBaseActivity() {

    private lateinit var drawerLayout: DrawerLayout         // 알림창
    private lateinit var examContainer: LinearLayout        // 시험 항목 담는 레이아웃
    private lateinit var sEdit: EditText                    // 검색어 입력창
    private lateinit var sButton: ImageButton               // 검색 버튼

    // 시험 목록 더미 데이터
    companion object {
        // 기본 시험 리스트
        val examList = mutableListOf(
            "정보처리기사\n시험일자: 00.00.00\n접수일자: 00.00.00",

            "토익\n시험일자: 25.00.20\n접수일자: 25.00.20",

            "전기기사\n시험일자: 25.00.10\n접수일자: 25.00.15",

            "한국사능력검정시험\n시험일자: 25.00.\n접수일자: 25.00.00",

            "워드프로세서\n시험일자: 25.00.00\n접수일자: 25.00.00",

            "SQLD\n시험일자: 25.00.00\n접수일자: 25.00.00",

            "정보처리산업기사\n시험일자: 00.0-.25\n접수일자: 25.00.00",

            "Excel\n시험일자: 00.00.00\n접수일자: 00.00.00"
        )

        private val newAddedExams = mutableSetOf<String>() // 새로 추가된 시험 항목

        // 새로운 시험 항목 추가
        fun addNewExam(examInfo: String) {
            if (!examList.contains(examInfo)) {
                examList.add(0, examInfo) // 리스트 맨 앞에 추가
                newAddedExams.add(examInfo) // 새로추가 된 항목 표시용
            }
        }

        //새로운 항목인지 확인
        fun NewExam(examInfo: String): Boolean {
            return newAddedExams.contains(examInfo)
        }

        // 새로운 항목 표시 초기화
        fun clearNewExamFlags() {
            newAddedExams.clear()
        }
    }

    private var filteredExamList = mutableListOf<String>() // 검색 기능(필터링)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.examview)

        initViews()             // 뷰 바인딩
        initToolbarDrawer()     // 툴바 및 드로어 설정
        setupSearch()           // 검색 기능 연결
        displayExams()          // 시험 목록 출력
        BottomAppBar()          // 하단 바 기능 설정
        NotiDrawer()            // 알림창 데이터 세팅
    }

    override fun onResume() {
        super.onResume()
        // 화면 다시 열릴 때 최신 데이터로 갱신
        filteredExamList.clear()
        filteredExamList.addAll(examList)
        displayExams()
    }

    // 레이아웃 뷰와 kt 연결
    private fun initViews() {
        drawerLayout = findViewById(R.id.activity_main_drawer_layout)
        examContainer = findViewById(R.id.exam_container)
        sEdit = findViewById(R.id.search_edit_text)
        sButton = findViewById(R.id.search_button)

        filteredExamList.clear()
        filteredExamList.addAll(examList)
    }

    // 툴바 및 드로어 버튼 연결
    private fun initToolbarDrawer() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_exam_view)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val notificationButton: ImageButton = findViewById(R.id.toolbar_notification_icon)
        notificationButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END) // 알림 드로어 열기
        }
    }

    // 검색 연결
    private fun setupSearch() {
        sButton.setOnClickListener { performSearch() } // 버튼 클릭 시 검색

        // 키보드에서 엔터 눌렀을 때도 검색 실행
        sEdit.setOnEditorActionListener { _, _, _ ->
            performSearch()
            true
        }
    }

    // 검색 실행 함수
    private fun performSearch() {
        val query = sEdit.text.toString().trim()

        filteredExamList.clear()
        if (query.isEmpty()) {
            filteredExamList.addAll(examList) // 검색어 없으면 전체
        } else {
            filteredExamList.addAll(
                examList.filter { it.contains(query, ignoreCase = true) } // 검색어 포함 항목만 필터
            )
        }

        displayExams() // 화면 갱신

        if (query.isNotEmpty()) {
            Toast.makeText(this, "${filteredExamList.size}개의 검색 결과", Toast.LENGTH_SHORT).show()
        }
    }

    // 시험 항목 화면에 출력
    private fun displayExams() {
        examContainer.removeAllViews() // 기존 항목 제거

        if (filteredExamList.isEmpty()) {
            // 검색 결과가 없을 경우 표시
            val emptyView = TextView(this).apply {
                text = "등록된 시험이 없습니다."
                textSize = 16f
                gravity = android.view.Gravity.CENTER
                setPadding(24, 48, 24, 48)
                setTextColor(resources.getColor(android.R.color.darker_gray))
            }
            examContainer.addView(emptyView)
            return
        }

        // 항목 출력
        filteredExamList.forEachIndexed { index, examInfo ->
            val examItemView = ExamItem(examInfo, index)
            examContainer.addView(examItemView)

            // 항목 구분선 (마지막 제외)
            if (index < filteredExamList.size - 1) {
                val divider = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 3
                    )
                    setBackgroundColor(0xFFEEEEEE.toInt())
                }
                examContainer.addView(divider)
            }
        }
    }

    // 시험뷰 만드는 함수
    private fun ExamItem(examInfo: String, index: Int): View {
        val constraintLayout = ConstraintLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 24, 0, 24)
            }
        }

        val examName = examInfo.split("\n")[0]
        val isNewItem = NewExam(examInfo)

        // 별표 체크박스 즐겨찾기 기능
        val favoriteCheck = CheckBox(this).apply {
            id = View.generateViewId()
            isChecked = false
            buttonDrawable = resources.getDrawable(R.drawable.starblack)
            minimumHeight = 120

            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    StarActivity.addFavorite(examName)
                    Toast.makeText(this@ExamViewActivity, "$examName: 관심과목 등록", Toast.LENGTH_SHORT).show()
                    //등록완료 토스트 메세지 출력
                    setButtonDrawable(R.drawable.staryellow)
                } else {
                    StarActivity.removeFavorite(examName)
                    Toast.makeText(this@ExamViewActivity, "$examName: 관심과목 해제", Toast.LENGTH_SHORT).show()
                    //등록해제 토스트 메세지 출력
                    setButtonDrawable(R.drawable.starblack)
                }
                NotiDrawer() // 알림창 새로고침
            }
        }

        // 시험 정보 텍스트뷰
        val infoText = TextView(this).apply {
            id = View.generateViewId()
            text = if (isNewItem) "$examInfo\n[NEW]" else examInfo
            textSize = 16f
            setTextColor(resources.getColor(android.R.color.black))
            setPadding(24, 0, 24, 0)

            if (isNewItem) { //새항목 표시
                setBackgroundColor(0x1A2196F3)
                setPadding(24, 12, 24, 12)

                // 3초 후 새항목 표시 제거
                postDelayed({
                    newAddedExams.remove(examInfo)
                    text = examInfo
                    setBackgroundColor(0x00000000)
                    setPadding(24, 0, 24, 0)
                }, 3000)
            }
        }

        // 일정 등록 버튼 (+ 버튼)
        val addButton = ImageButton(this).apply {
            id = View.generateViewId()
            setImageResource(R.drawable.plus)
            background = resources.getDrawable(R.drawable.radius)
            layoutParams = ConstraintLayout.LayoutParams(120, 120)
            setOnClickListener {
                // 일정 등록 화면으로 이동하는 intnet
                val intent = Intent(this@ExamViewActivity, ScheduleActivity::class.java)
                intent.putExtra("exam_name", examName)
                intent.putExtra("exam_info", examInfo)
                startActivity(intent)
                Toast.makeText(this@ExamViewActivity, "$examName: 일정 등록으로 이동", Toast.LENGTH_SHORT).show()
            }
        }

        // 시험 항목 삭제 버튼
        val deleteButton = ImageButton(this).apply {
            id = View.generateViewId()
            setImageResource(R.drawable.remove)
            background = resources.getDrawable(R.drawable.radius)
            backgroundTintList = android.content.res.ColorStateList.valueOf(0xFFFF5722.toInt())
            layoutParams = ConstraintLayout.LayoutParams(120, 120)
            setOnClickListener {
                removeExam(examInfo, examName)
            }
        }

        // 제약 레이아웃에 추가
        constraintLayout.addView(favoriteCheck)
        constraintLayout.addView(infoText)
        constraintLayout.addView(addButton)
        constraintLayout.addView(deleteButton)

        // 위치 제약 설정
        val constraintSet = androidx.constraintlayout.widget.ConstraintSet()
        constraintSet.clone(constraintLayout)

        // 체크박스 위치
        constraintSet.connect(favoriteCheck.id, androidx.constraintlayout.widget.ConstraintSet.START,
            androidx.constraintlayout.widget.ConstraintSet.PARENT_ID, androidx.constraintlayout.widget.ConstraintSet.START)
        constraintSet.connect(favoriteCheck.id, androidx.constraintlayout.widget.ConstraintSet.TOP,
            androidx.constraintlayout.widget.ConstraintSet.PARENT_ID, androidx.constraintlayout.widget.ConstraintSet.TOP)
        constraintSet.connect(favoriteCheck.id, androidx.constraintlayout.widget.ConstraintSet.BOTTOM,
            androidx.constraintlayout.widget.ConstraintSet.PARENT_ID, androidx.constraintlayout.widget.ConstraintSet.BOTTOM)

        // 텍스트 위치
        constraintSet.connect(infoText.id, androidx.constraintlayout.widget.ConstraintSet.START,
            favoriteCheck.id, androidx.constraintlayout.widget.ConstraintSet.END, 24)
        constraintSet.connect(infoText.id, androidx.constraintlayout.widget.ConstraintSet.END,
            addButton.id, androidx.constraintlayout.widget.ConstraintSet.START, 24)
        constraintSet.connect(infoText.id, androidx.constraintlayout.widget.ConstraintSet.TOP,
            androidx.constraintlayout.widget.ConstraintSet.PARENT_ID, androidx.constraintlayout.widget.ConstraintSet.TOP)
        constraintSet.connect(infoText.id, androidx.constraintlayout.widget.ConstraintSet.BOTTOM,
            androidx.constraintlayout.widget.ConstraintSet.PARENT_ID, androidx.constraintlayout.widget.ConstraintSet.BOTTOM)

        // 추가 버튼 위치
        constraintSet.connect(addButton.id, androidx.constraintlayout.widget.ConstraintSet.END,
            deleteButton.id, androidx.constraintlayout.widget.ConstraintSet.START, 24)
        constraintSet.connect(addButton.id, androidx.constraintlayout.widget.ConstraintSet.TOP,
            androidx.constraintlayout.widget.ConstraintSet.PARENT_ID, androidx.constraintlayout.widget.ConstraintSet.TOP)
        constraintSet.connect(addButton.id, androidx.constraintlayout.widget.ConstraintSet.BOTTOM,
            androidx.constraintlayout.widget.ConstraintSet.PARENT_ID, androidx.constraintlayout.widget.ConstraintSet.BOTTOM)

        // 삭제 버튼 위치
        constraintSet.connect(deleteButton.id, androidx.constraintlayout.widget.ConstraintSet.END,
            androidx.constraintlayout.widget.ConstraintSet.PARENT_ID, androidx.constraintlayout.widget.ConstraintSet.END)
        constraintSet.connect(deleteButton.id, androidx.constraintlayout.widget.ConstraintSet.TOP,
            androidx.constraintlayout.widget.ConstraintSet.PARENT_ID, androidx.constraintlayout.widget.ConstraintSet.TOP)
        constraintSet.connect(deleteButton.id, androidx.constraintlayout.widget.ConstraintSet.BOTTOM,
            androidx.constraintlayout.widget.ConstraintSet.PARENT_ID, androidx.constraintlayout.widget.ConstraintSet.BOTTOM)

        constraintSet.applyTo(constraintLayout)

        return constraintLayout
    }

    // 시험 항목 삭제
    private fun removeExam(examInfo: String, examName: String) {
        examList.remove(examInfo)
        filteredExamList.remove(examInfo)
        newAddedExams.remove(examInfo)
        StarActivity.removeFavorite(examName)
        ScheduleActivity.examSchedules.removeAll { it.name == examName }

        displayExams()
        NotiDrawer()
        Toast.makeText(this, "$examName: 삭제되었습니다.", Toast.LENGTH_SHORT).show()
    }

    // 하단 네비게이션 바 동작 모든 화면 동일하게 설정
    private fun BottomAppBar() {
        val starButton: ImageButton = findViewById(R.id.star)
        val homeButton: ImageButton = findViewById(R.id.home)
        val personButton: ImageButton = findViewById(R.id.person)

        starButton.setOnClickListener {
            startActivity(Intent(this, BookMarkPageActivity::class.java))
        }
        homeButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        personButton.setOnClickListener {
            startActivity(Intent(this, MyPageActivity::class.java))
        }
    }

    // 즐겨찾기 목록 드로어 뷰 표시
    private fun NotiDrawer() {
        val notiRecyclerView: RecyclerView = findViewById(R.id.re_noti)
        val noNotiText: TextView = findViewById(R.id.renoti_text)
        notiRecyclerView.layoutManager = LinearLayoutManager(this)

        val favoriteList = StarActivity.getFavorites().toMutableList()

        if (favoriteList.isNotEmpty()) {
            val adapter = NotificationAdapter(favoriteList)
            notiRecyclerView.adapter = adapter
            notiRecyclerView.visibility = View.VISIBLE
            noNotiText.visibility = View.GONE
        } else {
            notiRecyclerView.visibility = View.GONE
            noNotiText.visibility = View.VISIBLE
        }
    }

    // 뒤로 가기 시 드로어 닫기
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }
}
