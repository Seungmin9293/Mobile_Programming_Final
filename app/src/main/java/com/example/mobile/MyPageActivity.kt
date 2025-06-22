package com.example.mobile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat

// 마이페이지
class MyPageActivity : StarBaseActivity() {

    private lateinit var drawerLayout: DrawerLayout // 알림 드로어 레이아웃
    private val REQUEST_NOTIFICATION_PERMISSION = 1001 // 알림 권한 요청 코드

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mypage)

        drawerLayout = findViewById(R.id.activity_main_drawer_layout)

        //툴바 설정
        val toolbar = findViewById<Toolbar>(R.id.toolbar_mypage)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //툴바 알림 버튼
        findViewById<ImageButton>(R.id.addbtn1).setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                drawerLayout.openDrawer(GravityCompat.END)
            }
        }

        //알림 권한 요청 버튼 퍼미션
        findViewById<Button>(R.id.btn_request_permission).setOnClickListener {
            // 권한 없으면 요청
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            } else { //허용되어 있으면 토스트 메세지
                Toast.makeText(this, "이미 허용된 권한입니댜.", Toast.LENGTH_SHORT).show()
            }
        }

        // 하단앱바
        findViewById<ImageButton>(R.id.home).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        findViewById<ImageButton>(R.id.star).setOnClickListener {
            startActivity(Intent(this, BookMarkPageActivity::class.java))
        }
        findViewById<ImageButton>(R.id.person).setOnClickListener {
            // 현재 페이지이므로 아무 동작 없음
        }

        // 앱 버전 표시
        val versionText = findViewById<TextView>(R.id.text_app_version)
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        versionText.text = "앱 버전: $versionName"
    }

    // 뒤로가기 버튼 누를 때 드로어 닫기 처리
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }
}
