package com.example.mobile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import android.media.MediaPlayer


class TimerActivity : StarBaseActivity() {


    private lateinit var timer: Chronometer          //공부시간 타이머 chronmeter 사용
    private lateinit var start: ImageButton          //타이머 시작 버튼
    private lateinit var stop: ImageButton           //타이머 정지 버튼
    private lateinit var save: ImageButton           //시간 저장 버튼
    private lateinit var rt: TextView                //최근 저장된 공부 시간 표시
    private lateinit var drawerLayout: DrawerLayout  //드로어 레이아웃
    private lateinit var music: Switch               //음악 재생 스위치
    private lateinit var share: SharedPreferences    //공부 시간 기록 저장소

    private var isRunning = false                    //타이머 실행 여부 플래그
    private var pauseOffset: Long = 0                //일시정지된 시간 오프로 세팅
    private lateinit var mPlayer: MediaPlayer        //음악 재생 객체


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.timer)

        //SharedPreferences 초기화
        share = getSharedPreferences("StudyTimerPrefs", MODE_PRIVATE)

        //layout과 연결
        timer = findViewById(R.id.text_timer_display)
        start = findViewById(R.id.button_start_timer)
        stop = findViewById(R.id.button_stop_timer)
        save = findViewById(R.id.button_save_timer_record)
        rt = findViewById(R.id.text_recent_record_value)
        drawerLayout = findViewById(R.id.activity_main_drawer_layout)
        val alarmBtn: ImageButton = findViewById(R.id.addbtn1)
        val studyDiaryButton: Button = findViewById(R.id.button_study_diary)
        music = findViewById(R.id.switch_music)

        //음악 재생 파일 지정 (res/raw/song.mp3)
        mPlayer = MediaPlayer.create(this, R.raw.song)

        //툴바
        val toolbarTimer = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_timer)
        setSupportActionBar(toolbarTimer)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        loadRecentRecord()

        //음악 스위치 리스너
        music.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mPlayer.start()
                Toast.makeText(this, "재생 시작!", Toast.LENGTH_SHORT).show()
            } else {
                if (mPlayer.isPlaying) mPlayer.pause()  // stop() 사용 시 재시작 불가
            }
        }

        //타이머 시작버튼
        start.setOnClickListener {
            if (!isRunning) {
                timer.base = SystemClock.elapsedRealtime() - pauseOffset
                timer.start()
                isRunning = true
            }
        }

        //타이머 저장버튼
        stop.setOnClickListener {
            if (isRunning) {
                timer.stop()
                pauseOffset = SystemClock.elapsedRealtime() - timer.base
                isRunning = false
            }
        }

        //시간 저장버튼
        save.setOnClickListener {
            if (!isRunning && pauseOffset > 0) {
                val elapsedTime = pauseOffset
                val hours = (elapsedTime / 3600000).toInt()
                val minutes = ((elapsedTime % 3600000) / 60000).toInt()
                val seconds = ((elapsedTime % 60000) / 1000).toInt()

                val formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds)

                //시간 저장
                saveStudyTime(formattedTime)
                rt.text = formattedTime
                resetTimer()

                Toast.makeText(this, "공부 시간이 저장되었습니다: $formattedTime", Toast.LENGTH_SHORT).show()
            } else if (isRunning) {
                Toast.makeText(this, "타이머를 먼저 정지해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "저장할 시간이 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        //알림 리스너
        alarmBtn.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                drawerLayout.openDrawer(GravityCompat.END)
            }
        }

        //스터디 페이지로 이동
        studyDiaryButton.setOnClickListener {
            startActivity(Intent(this, StudyActivity::class.java))
        }

        //하단 앱바
        findViewById<ImageButton>(R.id.home).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        findViewById<ImageButton>(R.id.person).setOnClickListener {
            startActivity(Intent(this, MyPageActivity::class.java))
        }
        findViewById<ImageButton>(R.id.star).setOnClickListener {
            startActivity(Intent(this, BookMarkPageActivity::class.java))
        }
    }

    //공부시간 저장
    private fun saveStudyTime(formattedTime: String) {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        val todayKey = "study_time_$today"

        val existingTodayTime = share.getString(todayKey, "00:00:00") ?: "00:00:00"
        val newTotalTime = addTimes(existingTodayTime, formattedTime)

        //SharedPreferences 저장
        with(share.edit()) {
            putString("recent_study_time", formattedTime)          //최근 공부 시간
            putString(todayKey, newTotalTime)                     //날짜별 누적 공부 시간
            putString("today_total_study_time", newTotalTime)     //오늘 총 공부 시간
            apply()
        }
    }

    //시간 더하기 로직
    private fun addTimes(time1: String, time2: String): String {
        val parts1 = time1.split(":")
        val parts2 = time2.split(":")

        val hours1 = parts1[0].toInt()
        val minutes1 = parts1[1].toInt()
        val seconds1 = parts1[2].toInt()

        val hours2 = parts2[0].toInt()
        val minutes2 = parts2[1].toInt()
        val seconds2 = parts2[2].toInt()

        val totalSeconds = seconds1 + seconds2
        val totalMinutes = minutes1 + minutes2 + (totalSeconds / 60)
        val totalHours = hours1 + hours2 + (totalMinutes / 60)

        return String.format("%02d:%02d:%02d",
            totalHours,
            totalMinutes % 60,
            totalSeconds % 60)
    }

    //최근 저장 시간 호출
    private fun loadRecentRecord() {
        val recentTime = share.getString("recent_study_time", "00:00:00") ?: "00:00:00"
        rt.text = recentTime
    }

    //타이머 초기화
    private fun resetTimer() {
        timer.stop()
        timer.base = SystemClock.elapsedRealtime()
        pauseOffset = 0
        isRunning = false
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    //음악 해제
    override fun onDestroy() {
        super.onDestroy()
        if (::mPlayer.isInitialized) {
            mPlayer.release()
        }
    }
}
