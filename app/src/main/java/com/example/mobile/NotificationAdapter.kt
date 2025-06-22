package com.example.mobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//알림버튼 즐겨찾기 표시 RecyclerView 어댑터
class NotificationAdapter(private val notifications: List<String>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    // 각 알림 항목을 표현 ViewHolder
    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.image_notification_icon) // 아이콘
        val contentTextView: TextView = itemView.findViewById(R.id.text_notification_content) // 알림 텍스트
    }

    // ViewHolder생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(itemView)
    }

    //알림 내용 연결
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val currentNotification = notifications[position]
        holder.contentTextView.text = currentNotification
    }

    //전체 항목 수
    override fun getItemCount(): Int {
        return notifications.size
    }
}
