package com.example.mobile


import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView




open class StarBaseActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()

        val drawer = findViewById<DrawerLayout?>(R.id.activity_main_drawer_layout)

        // RecyclerView
        val rv = findViewById<RecyclerView?>(R.id.re_noti)
            ?: findViewById<RecyclerView?>(R.id.main_activity_recycler_view_notifications)
            ?: findViewById<RecyclerView?>(R.id.study_activity_recycler_view_notifications) //별 추가

        //알림 없음 TextView
        val empty = findViewById<TextView?>(R.id.renoti_text)
            ?: findViewById<TextView?>(R.id.main_activity_text_no_notifications)
            ?: findViewById<TextView?>(R.id.study_activity_text_no_notifications)        //별 추가

        if (drawer != null && rv != null && empty != null) {
            drawer.bindFavorites(rv, empty, this)
        }
    }
}
