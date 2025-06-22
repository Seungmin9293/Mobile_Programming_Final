package com.example.mobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

//메모화면
class MemoActivity : StarBaseActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var memoEdit: EditText            //메모입력 에딧텍스트
    private lateinit var memoList: ListView            //메모목록 리스트뷰
    private lateinit var btnSave: Button               //메모저장 버튼
    private lateinit var btnToggle: Button             //메모목록/작성 화면 전환 버튼
    private lateinit var btnDelete: Button             //삭제모드 버튼

    //하단 앱바 버튼
    private lateinit var btnStar: ImageButton          //즐겨찾기 버튼
    private lateinit var btnHome: ImageButton          //홈 버튼
    private lateinit var btnPerson: ImageButton        //마이페이지 버튼
    //툴바버튼
    private lateinit var btnAlarm: ImageButton         //알림버튼

    //데이터 변수
    private val memos = mutableListOf<String>()        //메모데이터 저장 리스트
    private lateinit var memoAdapter: ArrayAdapter<String> //리스트뷰, 데이터 연결
    private var isListVisible = false                  //메모목록 vsibile
    private var isDeleteMode = false                   //삭제모드 활성화


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.memo)

        //메서드 호출
        initViews()        // 뷰 참조
        setToolbarDrawer() // 툴바, 드로어
        setupList()        // 메모리스트
        setupButton()      // 버튼 이벤트
        setupBottomBar()   // 하단 앱바 설정
    }


    private fun initViews() {

        drawerLayout = findViewById(R.id.activity_main_drawer_layout)

        //메모 뷰
        memoEdit = findViewById(R.id.edit_memo)           //메모 입력 필드
        memoList = findViewById(R.id.listview_memos)      //메모 목록 리스트뷰
        btnSave = findViewById(R.id.button_save_memo)     //저장 버튼
        btnToggle = findViewById(R.id.button_toggle_list) //목록 전환 버튼
        btnDelete = findViewById(R.id.button_delete_mode) //삭제 모드 버튼

        //하단 앱바 버튼
        btnStar = findViewById(R.id.star)                 //즐겨찾기 버튼
        btnHome = findViewById(R.id.home)                 //홈 버튼
        btnPerson = findViewById(R.id.person)             //마이페이지 버튼

        //툴바 알림 버튼
        btnAlarm = findViewById(R.id.toolbar_notification_icon)
    }


    private fun setToolbarDrawer() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_memo) //툴바
        setSupportActionBar(toolbar) // 액션바로 설정
        supportActionBar?.setDisplayShowTitleEnabled(false) // 제목 표시 x

        // 알림 버튼 클릭 이벤트 - 드로어 열기/닫기
        btnAlarm.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END) // 드로어 닫기
            } else {
                drawerLayout.openDrawer(GravityCompat.END)  // 드로어 열기
            }
        }
    }


    private fun setupList() {
        memoAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, memos)
        memoList.adapter = memoAdapter

        //메모 리스트 클릭 리스너
        memoList.setOnItemClickListener { _, _, position, _ ->
            if (isDeleteMode) {
                //삭제 모드일 때:선택된 메모 삭제 확인 다이얼로그 띄움
                showDeleteConfirmDialog(position)
            } else {
                showMemoDetailDialog(memos[position])
            }
        }
    }


    private fun setupButton() {
        //메모 저장버튼
        btnSave.setOnClickListener {
            val text = memoEdit.text.toString().trim() //앞뒤 공백 제거
            if (text.isNotEmpty()) {
                // 메모가 비어있지 않으면
                memos.add(text) // 메모 리스트에 추가
                memoAdapter.notifyDataSetChanged()
                if (memos.isNotEmpty()) {
                    memoList.smoothScrollToPosition(memos.size - 1)
                }

                memoEdit.text.clear() // 입력 필드 비우기
                hideKeyboard() // 키보드 숨기기

                // 리스트 안보이면 자동으로 보여줌
                if (!isListVisible) ListVisible()

                // 저장 완료 토스트 메시지
                Toast.makeText(this, "메모가 저장되었습니다", Toast.LENGTH_SHORT).show()
            } else {
                //메모가 비어있는 경우 경고 토스트 메세지
                Toast.makeText(this, "메모를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }


        btnToggle.setOnClickListener {
            ListVisible() // 메모 목록 보기/숨기기 전환
        }

        btnDelete.setOnClickListener {
            toggleDelete() // 삭제 모드
        }
    }


    private fun ListVisible() {
        isListVisible = !isListVisible // 상태 토글

        if (isListVisible) {
            memoList.visibility = View.VISIBLE     // 메모 목록 보이기
            memoEdit.visibility = View.GONE        // 입력 필드 숨기기
            btnToggle.text = "새메모작성 ▲"        // 버튼 텍스트 변경

            // 메모가 있을 때만 삭제 버튼 보임
            if (memos.isNotEmpty()) {
                btnDelete.visibility = View.VISIBLE
            }
            if (memos.isNotEmpty()) {
                memoList.post {
                    memoList.setSelection(memos.size - 1)
                }
            }
        } else {
            //메모입력 모드
            memoList.visibility = View.GONE        // 메모 목록 숨기기
            memoEdit.visibility = View.VISIBLE     // 입력 필드 보이기
            btnToggle.text = "기존 메모 보기 ▼"   // 버튼 텍스트 변경
            btnDelete.visibility = View.GONE       // 삭제 버튼 숨기기

            // 삭제 모드가 활성화 -> 해제
            if (isDeleteMode) {
                isDeleteMode = false
                btnDelete.text = "삭제"
                btnDelete.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFFFF5722.toInt())
            }
        }
    }

    private fun toggleDelete() {
        isDeleteMode = !isDeleteMode // 삭제 모드 상태

        if (isDeleteMode) {
            // 삭제 모드 활성화
            btnDelete.text = "삭제모드"
            btnDelete.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF4CAF50.toInt()) // 초록색
            Toast.makeText(this, "삭제할 메모를 선택하세요", Toast.LENGTH_SHORT).show()
        } else {
            // 삭제 모드 비활성화
            btnDelete.text = "삭제"
            btnDelete.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFFFF5722.toInt()) // 주황색
            Toast.makeText(this, "삭제 모드가 해제되었습니다", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showDeleteConfirmDialog(position: Int) {
        val memo = memos[position] // 삭제 메모 내용
        //미리보기 50글자 제한
        val preview = if (memo.length > 50) memo.take(50) + "..." else memo

        AlertDialog.Builder(this)
            .setTitle("메모 삭제")
            .setMessage("이 메모를 삭제하시겠습니까?\n\n\"$preview\"")
            .setPositiveButton("삭제") { _, _ ->
                // 삭제 확인 시
                memos.removeAt(position) // 리스트에서 메모 삭제
                memoAdapter.notifyDataSetChanged()
                Toast.makeText(this, "메모가 삭제되었습니다", Toast.LENGTH_SHORT).show()

                // 모든 메모 삭제되면 삭제 모드 해제
                if (memos.isEmpty()) {
                    toggleDelete()
                    btnDelete.visibility = View.GONE
                }
            }
            .setNegativeButton("취소", null)//취소
            .show()
    }


    //메모내용 다이얼로그
    private fun showMemoDetailDialog(memo: String) {
        AlertDialog.Builder(this)
            .setTitle("메모 내용")
            .setMessage(memo)
            .setPositiveButton("확인", null)
            .show()
    }


    //하단 앱바
    private fun setupBottomBar() {

        btnStar.setOnClickListener {
            startActivity(Intent(this, BookMarkPageActivity::class.java))
        } //즐겨찾기

        btnHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
               //홈화면
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP//홈 화면 중복방지
            })
        }

        //마이페이지
        btnPerson.setOnClickListener {
            startActivity(Intent(this, MyPageActivity::class.java))
        }
    }

    //키보드 숨기기
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(memoEdit.windowToken, 0)
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