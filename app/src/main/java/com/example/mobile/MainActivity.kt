package com.example.mobile

// 안드로이드 기본 라이브러리 import
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

//어플 메인 화면
class MainActivity : StarBaseActivity() {


    private lateinit var drawerLayout: DrawerLayout // 드로어 레이아웃
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 앱 실행시 한번만 초기화 즐겨찾기 데이터 관리
        StarActivity.init(applicationContext)

        //툴바
        val mainToolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mainToolbar) // 액션바 사용
        supportActionBar?.setDisplayShowTitleEnabled(false) // 제목 비활성화

        //드로어 레이아웃
        drawerLayout = findViewById(R.id.activity_main_drawer_layout)
        val alarmBtn: ImageButton = findViewById(R.id.addbtn1) // 알림 버튼

        // 알림 버튼 클릭 리스너
        alarmBtn.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END) // 드로어 닫기
            } else {
                drawerLayout.openDrawer(GravityCompat.END) // 드로어 열기
            }
        }

        // 추가 버튼 클릭시 일정관리 화면으로 이동함
        findViewById<ImageButton>(R.id.d_day_plus_button).setOnClickListener {
            startActivity(Intent(this, ScheduleActivity::class.java))
        }

        // 일정관리 클릭 리스너
        findViewById<LinearLayout>(R.id.schedule_section).setOnClickListener {
            startActivity(Intent(this, ScheduleActivity::class.java)) // 일정관리 화면 이동
        }

        // 학습관리 클릭 리스너
        findViewById<LinearLayout>(R.id.study_section).setOnClickListener {
            startActivity(Intent(this, StudyActivity::class.java)) // 학습관리 화면 이동
        }

        // 타이머 클릭 리스너
        findViewById<LinearLayout>(R.id.timer_section).setOnClickListener {
            startActivity(Intent(this, TimerActivity::class.java)) // 타이머 화면 이동
        }

        // 시험보기 클릭 리스너
        findViewById<LinearLayout>(R.id.examview).setOnClickListener {
            startActivity(Intent(this, ExamViewActivity::class.java)) // 시험보기 화면 이동
        }

        // 메모 클릭 리스너
        findViewById<LinearLayout>(R.id.memo_section).setOnClickListener {
            startActivity(Intent(this, MemoActivity::class.java)) // 메모화면 이동
        }

        // 오답노트 클릭 리스너
        findViewById<LinearLayout>(R.id.oxnote_section).setOnClickListener {
            startActivity(Intent(this, OxNoteActivity::class.java)) //오답노트 화면 이동
        }

        //하단 앱바
        //즐겨찾기 클릭 리스너
        findViewById<ImageButton>(R.id.star).setOnClickListener {
            startActivity(Intent(this, BookMarkPageActivity::class.java)) //즐겨찾기 페이지 이동
        }


        // 마이페이지 버튼 클릭 이벤트
        findViewById<ImageButton>(R.id.person).setOnClickListener {
            startActivity(Intent(this, MyPageActivity::class.java)) //마이페이지 이동
        }
    }

    override fun onResume() {
        super.onResume()
        // 즐겨찾기 갱신
        NotiDrawer()
    }

    private fun NotiDrawer() {
        val notiRecyclerView: RecyclerView = findViewById(R.id.re_noti) // 알림 목록 리사이클러뷰
        val noNotiText: TextView = findViewById(R.id.renoti_text) // 알림 없으면 텍스트

        //레이아웃 매니저 세로 방향으로
        notiRecyclerView.layoutManager = LinearLayoutManager(this)

        //즐겨찾기 데이터 호출
        val favoriteList = StarActivity.getFavorites().toMutableList()

        //데이터 o,x 에 따라 visible이용
        if (favoriteList.isNotEmpty()) {
            // 즐겨찾기가 있는 경우
            val adapter = NotificationAdapter(favoriteList) //알림 어댑터 사용
            notiRecyclerView.adapter = adapter //리사이클러뷰에 어댑터 설정
            notiRecyclerView.visibility = View.VISIBLE // 리사이클러뷰 보이기
            noNotiText.visibility = View.GONE // 텍스트 숨기기
        } else {
            // 즐겨찾기가 없는 경우
            notiRecyclerView.visibility = View.GONE //리사이클러뷰 숨기기
            noNotiText.visibility = View.VISIBLE //텍스트 보이기
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            //드로어 닫기
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }
}