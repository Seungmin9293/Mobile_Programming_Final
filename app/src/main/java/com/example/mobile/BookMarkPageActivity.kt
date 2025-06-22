package com.example.mobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// 즐겨찾기 페이지
class BookMarkPageActivity : StarBaseActivity() {

    private lateinit var drawerLayout: DrawerLayout  // 드로어 레이아웃
    private lateinit var recyclerView: RecyclerView  // 즐겨찾기 목록 표시
    private lateinit var emptyText: TextView         // 즐겨찾기 목록 없을 때 텍스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bookmark)

        // 툴바,드로어
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) // 툴바 액션바 사용
        supportActionBar?.setDisplayShowTitleEnabled(false) // 기본 제목X

        drawerLayout = findViewById(R.id.activity_main_drawer_layout)
        findViewById<ImageButton>(R.id.addbtn1).setOnClickListener {
            // 알림 버튼 클릭 -> 드로어 열기
            drawerLayout.openDrawer(GravityCompat.END)
        }

        //즐겨찾기 리스트
        recyclerView = findViewById(R.id.recycler_view_favorites)
        emptyText = findViewById(R.id.text_favorites_empty)
        recyclerView.layoutManager = LinearLayoutManager(this)

        refreshPrefer() // 즐겨찾기 항목 불러옴

        //하단 바 클릭
        findViewById<ImageButton>(R.id.star).setOnClickListener {
            // 현재 화면이여서 동작 없음
        }
        findViewById<ImageButton>(R.id.home).setOnClickListener {
            // 홈 화면 이동, 중복 방지 기능
            startActivity(Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
        }
        findViewById<ImageButton>(R.id.person).setOnClickListener {
            // 마이페이지 화면 이동
            startActivity(Intent(this, MyPageActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshPrefer() // 화면 다시들어와도 즐겨찾기 갱신
    }

    // 즐겨찾기 데이터 갱신하는 함수 VISIBLE 사용
    private fun refreshPrefer() {
        try {
            val favorites = StarActivity.getFavorites().toMutableList() // 즐겨찾기 목록 가져오기

            if (favorites.isEmpty()) {
                // 즐겨찾기가 없을 경우 빈 텍스트 표시
                emptyText.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                // 즐겨찾기가 있을 경우 리스트 표시
                emptyText.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                val adapter = NotificationAdapter(favorites) // 어댑터 이용
                recyclerView.adapter = adapter
                recyclerView.requestLayout() // 레이아웃 갱신
            }
        } catch (e: Exception) {
        }
    }

    // 뒤로가기 버튼 눌렀을 때 드로어가 열려있으면 닫기
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }
}
