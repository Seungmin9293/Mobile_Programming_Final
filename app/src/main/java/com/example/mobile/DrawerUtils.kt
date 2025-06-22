package com.example.mobile

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/*
  DrawerLayout 확장 함수: 즐겨찾기 목록 RecyclerView를 바인딩하고 표시함
  모든 Activity에서 호출할수 있음
 */
fun DrawerLayout.bindFavorites(
    recycler: RecyclerView,      // 즐겨찾기를 표시 RecyclerView
    emptyText: TextView,         // 즐겨찾기 없을 때 텍스트
    context: Context
) {
    val list = StarActivity.getFavorites().toMutableList() // 즐겨찾기 목록 호출

    if (list.isEmpty()) {
        // 즐겨찾기 없으면 텍스트 표시
        emptyText.visibility = View.VISIBLE
        recycler.visibility  = View.GONE
    } else {
        // 즐겨찾기 있으면 리스트 표시
        emptyText.visibility = View.GONE
        recycler.visibility  = View.VISIBLE
        recycler.adapter     = NotificationAdapter(list)

        // 레이아웃 매니저가 없다면 세로방향 레이아웃 적용
        if (recycler.layoutManager == null) {
            recycler.layoutManager = LinearLayoutManager(context)
        }
    }
}
