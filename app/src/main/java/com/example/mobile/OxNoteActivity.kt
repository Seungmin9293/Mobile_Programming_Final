package com.example.mobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class OxNoteActivity : StarBaseActivity() {

    // ───────── 1. 변수 정의 ─────────
    private lateinit var drawerLayout: DrawerLayout           // 알림 드로어
    private lateinit var addForm: LinearLayout                // 오답 추가 폼

    private lateinit var btnFab: FloatingActionButton         // 오답 추가 + 버튼
    private lateinit var btnSave: Button                      // 저장 버튼
    private lateinit var btnCancel: Button                    // 취소 버튼
    private lateinit var alarmBtn: ImageButton                // 드로어 열기

    private lateinit var ratingBar: RatingBar                 // 중요도
    private lateinit var spinnerFilter: Spinner               // 필터 드롭다운
    private lateinit var btnSearch: ImageButton               // 검색 버튼

    private lateinit var btnStar: ImageButton                 // 하단 즐겨찾기
    private lateinit var btnHome: ImageButton                 // 하단 홈
    private lateinit var btnPerson: ImageButton               // 하단 마이페이지

    //오답 슬롯
    private val itemIds = intArrayOf(
        R.id.mistake_1, R.id.mistake_2, R.id.mistake_3,
        R.id.mistake_4, R.id.mistake_5, R.id.mistake_6,
        R.id.mistake_7, R.id.mistake_8
    )
    private val dividerIds = intArrayOf(
        0, 0, R.id.divide_2, R.id.divide_3, R.id.divide_4,
        R.id.divide_5, R.id.divide_6, R.id.divide_7
    )

    private lateinit var checkBoxes: List<CheckBox> //체크박스 8개
    private val pref by lazy { getSharedPreferences("oxnote", MODE_PRIVATE) }

    // ───────── 2. 생명주기 ─────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.oxnote)

        initViews()              //뷰 연결
        setToolbarDrawer()      //툴바/드로어 설정
        setupAddToggle()        //오답 추가 토글/저장
        setupBottomBar()        //하단 버튼
        CheckStates()           //체크 상태 복원
        setupSearch()           //필터 검색
    }

    // ───────── 3. 초기화 ─────────
    private fun initViews() {
        // 모든 뷰 연결
        drawerLayout = findViewById(R.id.activity_main_drawer_layout)
        addForm      = findViewById(R.id.add_mistake_section)
        btnFab       = findViewById(R.id.fab)
        btnSave      = findViewById(R.id.btnSave)
        btnCancel    = findViewById(R.id.btnCancel)
        alarmBtn     = findViewById(R.id.addbtn1)
        ratingBar    = findViewById(R.id.ratingBar)
        spinnerFilter= findViewById(R.id.spinnerFilter)
        btnSearch    = findViewById(R.id.btnSearch)
        btnStar      = findViewById(R.id.star)
        btnHome      = findViewById(R.id.home)
        btnPerson    = findViewById(R.id.person)

        // 체크박스들 연결
        checkBoxes = itemIds.mapIndexed { idx, _ ->
            findViewById(
                resources.getIdentifier("cbReviewed_${idx + 1}", "id", packageName)
            )
        }

        btnSave.isEnabled = true
    }

    // 툴바
    private fun setToolbarDrawer() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        alarmBtn.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.END))
                drawerLayout.closeDrawer(GravityCompat.END)
            else
                drawerLayout.openDrawer(GravityCompat.END)
        }
    }

    // ───────── 5. 오답 추가 폼 ─────────
    private fun setupAddToggle() {
        btnFab.setOnClickListener {
            // 오답추가 보여주기/숨기기
            addForm.visibility = if (addForm.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        btnCancel.setOnClickListener {
            addForm.visibility = View.GONE
            Toast.makeText(this, "취소되었습니다.", Toast.LENGTH_SHORT).show()
        }

        //저장 클릭 리스너
        btnSave.setOnClickListener {
            // 입력값 호출
            val title   = findViewById<EditText>(R.id.etTitle).text.toString().ifBlank { "(제목)" }
            val exam    = findViewById<AutoCompleteTextView>(R.id.actvExam).text.toString()
            val subject = findViewById<AutoCompleteTextView>(R.id.actvSubject).text.toString()
            val stars   = ratingBar.rating.toInt().coerceIn(1, 5)
            val dateStr = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(Date())

            // 리스트에 추가
            if (!addMistakeList(title, exam, subject, dateStr, stars)) {
                Toast.makeText(this, "추가할 공간이 없습니다 (최대 8개).", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            addForm.visibility = View.GONE
            Toast.makeText(this, "저장 완료!", Toast.LENGTH_SHORT).show()
        }
    }

    // ───────── 6. 오답 리스트 삽입 ─────────
    private fun addMistakeList(title: String, exam: String, subject: String, date: String, stars: Int): Boolean {
        val slotIndex = itemIds.indexOfFirst {
            val title = findViewById<TextView>(
                resources.getIdentifier("tvTitle_${itToIndex(it)}", "id", packageName)
            ).text
            title.isNullOrBlank()
        }
        if (slotIndex == -1) return false

        val slotLayout = findViewById<ConstraintLayout>(itemIds[slotIndex])

        fun innerId(name: String) =
            resources.getIdentifier("${name}_${slotIndex + 1}", "id", packageName)

        slotLayout.findViewById<TextView>(innerId("tvTitle")).text = title
        slotLayout.findViewById<TextView>(innerId("tvExam")).text = exam
        slotLayout.findViewById<TextView>(innerId("tvSubject")).text =
            if (subject.isBlank()) "" else "과목: $subject"
        slotLayout.findViewById<TextView>(innerId("tvDate")).text = date
        slotLayout.findViewById<TextView>(innerId("importance_text")).text =
            "★★★★★".take(stars).padEnd(5, '☆')

        slotLayout.visibility = View.VISIBLE
        if (slotIndex >= 2) {
            dividerIds[slotIndex].takeIf { it != 0 }?.let {
                findViewById<View>(it).visibility = View.VISIBLE
            }
        }

        return true
    }

    private fun itToIndex(itemId: Int): Int = itemIds.indexOf(itemId) + 1

    // ───────── 7. 필터 검색 ─────────
    private fun setupSearch() {
        val options = arrayOf("전체", "중요도 ★4 이상", "중요도 ★3 이하")
        spinnerFilter.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)

        btnSearch.setOnClickListener {
            filterList(spinnerFilter.selectedItemPosition)
        }
    }

    private fun filterList(option: Int) {
        for (i in itemIds.indices) {
            val slot = findViewById<ConstraintLayout>(itemIds[i])
            val titleView = slot.findViewById<TextView>(
                resources.getIdentifier("tvTitle_${i + 1}", "id", packageName)
            )
            if (titleView.text.isNullOrBlank()) {
                slot.visibility = View.GONE
                dividerIds.getOrNull(i)?.takeIf { it != 0 }?.let { id ->
                    findViewById<View>(id).visibility = View.GONE
                }
                continue
            }

            val starCnt = slot.findViewById<TextView>(
                resources.getIdentifier("importance_text_${i + 1}", "id", packageName)
            ).text.count { it == '★' }

            val show = when (option) {
                1 -> starCnt >= 4
                2 -> starCnt <= 3
                else -> true
            }

            slot.visibility = if (show) View.VISIBLE else View.GONE
            dividerIds.getOrNull(i)?.takeIf { it != 0 }?.let { id ->
                findViewById<View>(id).visibility = slot.visibility
            }
        }
    }

    // ───────── 8. 체크박스 초기화 ─────────
    private fun CheckStates() {
        itemIds.forEachIndexed { idx, id ->
            clearSlotTexts(idx)
            findViewById<ConstraintLayout>(id).visibility = View.GONE
            dividerIds.getOrNull(idx)?.takeIf { it != 0 }?.let { div ->
                findViewById<View>(div).visibility = View.GONE
            }
        }
    }

    private fun clearSlotTexts(index: Int) {
        val names = arrayOf("tvTitle", "tvExam", "tvSubject", "tvDate", "importance_text")
        names.forEach { base ->
            findViewById<TextView>(
                resources.getIdentifier("${base}_${index + 1}", "id", packageName)
            ).text = ""
        }
    }

    // ───────── 9. 하단 버튼 ─────────
    private fun setupBottomBar() {
        btnStar.setOnClickListener {
            startActivity(Intent(this, BookMarkPageActivity::class.java))
        }//즐겨찾기
        btnHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }//홈버튼
        btnPerson.setOnClickListener {
            startActivity(Intent(this, MyPageActivity::class.java))
        }//마이페이지
    }

    // ───────── 10. 뒤로가기 ─────────
    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(GravityCompat.END) -> drawerLayout.closeDrawer(GravityCompat.END)
            addForm.visibility == View.VISIBLE -> addForm.visibility = View.GONE
            else -> super.onBackPressed()
        }
    }
}
