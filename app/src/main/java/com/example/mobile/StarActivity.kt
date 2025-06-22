package com.example.mobile

import android.content.Context
import android.content.SharedPreferences

object StarActivity {

    private const val PREFS_NAME = "favorite_prefs" //SharedPreferences
    private const val KEY_FAVORITES = "key_favorites" //즐겨찾기 키
    private lateinit var prefs: SharedPreferences

    // 앱 시작 시 메인에서 한번 호출
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // 즐겨찾기 목록 가져오기
    fun getFavorites(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    // 즐겨찾기 추가
    fun addFavorite(examName: String) {
        val favorites = getFavorites().toMutableSet()
        favorites.add(examName)
        prefs.edit().putStringSet(KEY_FAVORITES, favorites).apply()
    }

    // 즐겨찾기 삭제
    fun removeFavorite(examName: String) {
        val favorites = getFavorites().toMutableSet()
        favorites.remove(examName)
        prefs.edit().putStringSet(KEY_FAVORITES, favorites).apply()
    }
}