# ExamPlan - 시험 일정 관리 앱

> 시험 일정과 학습을 체계적으로 관리할 수 있는 Android 앱

## 📋 프로젝트 개요

ExamPlan은 학습자들이 시험 일정을 효율적으로 관리하고 공부 계획을 세울 수 있도록 도와주는 Android 애플리케이션입니다. 직관적인 UI와 다양한 학습 도구를 통해 체계적인 학습 관리를 지원합니다.

## ✨ 주요 기능

### 🗓️ 시험 일정 관리
- **달력 뷰**: 월별 시험 일정을 한눈에 확인
- **일정 등록**: 시험명, 시험일자, 접수일자 설정
- **시험 목록**: 전체 시험 목록 조회 및 검색
- **D-Day 카운터**: 메인 화면에서 중요한 시험까지의 남은 일수 표시

### 📚 학습 관리
- **스터디 페이지**: 일별 학습 목표 설정 및 관리
- **타이머 기능**: 공부 시간 측정 및 기록
- **학습 기록**: 일별/누적 공부 시간 통계
- **음악 재생**: 집중력 향상을 위한 배경음악 기능

### 📝 학습 도구
- **메모장**: 간단한 메모 작성 및 관리
- **오답노트**: 틀린 문제 정리 및 중요도 설정
- **즐겨찾기**: 관심 시험 과목 관리

### 🔔 알림 시스템
- **즐겨찾기 알림**: 관심 등록한 시험 정보 알림
- **일정 알림**: 중요한 시험 일정 리마인더

## 🛠️ 기술 스택

- **언어**: Kotlin
- **플랫폼**: Android (API 24+)
- **UI Framework**: 
  - Android Views
  - RecyclerView, ViewPager2
  - ConstraintLayout, DrawerLayout
- **데이터 저장**: SharedPreferences
- **미디어**: MediaPlayer (배경음악)

## 🏗️ 프로젝트 구조

```
app/src/main/java/com/example/mobile/
├── MainActivity.kt              # 메인 화면
├── ScheduleActivity.kt          # 시험 일정 관리
├── StudyActivity.kt             # 학습 관리
├── TimerActivity.kt             # 타이머 기능
├── ExamViewActivity.kt          # 시험 목록 조회
├── MemoActivity.kt              # 메모 기능
├── OxNoteActivity.kt            # 오답노트
├── BookMarkPageActivity.kt      # 즐겨찾기
├── MyPageActivity.kt            # 마이페이지
├── adapters/
│   ├── DayAdapter.kt           # 달력 날짜 어댑터
│   ├── MonthAdapter.kt         # 달력 월 어댑터
│   └── NotificationAdapter.kt   # 알림 어댑터
├── models/
│   ├── DayData.kt              # 날짜 데이터 모델
│   └── RecordItem.kt           # 기록 데이터 모델
└── utils/
    ├── StarActivity.kt         # 즐겨찾기 관리
    ├── StarBaseActivity.kt     # 베이스 액티비티
    └── DrawerUtils.kt          # 드로어 유틸리티
```

## 🖥️ 주요 화면

### 메인 화면
- D-Day 표시
- 각 기능으로의 빠른 접근 메뉴
- 하단 네비게이션 바

### 시험 일정 화면
- 월별 달력 뷰
- 일정 등록 및 수정
- 시험 정보 상세 보기

### 학습 관리 화면
- 오늘의 학습 목표 설정
- 공부 시간 기록 조회
- 달력을 통한 학습 이력 확인

### 타이머 화면
- 공부 시간 측정
- 일시정지/재시작 기능
- 학습 기록 저장
- 배경음악 재생

## 🎨 UI/UX 특징

- **Material Design** 기반의 직관적인 인터페이스
- **일관된 색상 체계**: 파란색 계열의 브랜드 컬러 사용
- **접근성**: 명확한 버튼 라벨링 및 적절한 터치 영역

## 🚀 설치 및 실행

### 요구사항
- Android Studio Arctic Fox 이상
- Android SDK API 24 (Android 7.0) 이상
- Kotlin 1.9.0


## 📱 앱 사용법

### 첫 사용 시
1. 앱 설치 후 알림 권한 허용 (마이페이지에서 설정 가능)
2. 메인 화면에서 원하는 기능 선택
3. 시험 일정 등록으로 시작 권장

### 시험 등록
1. 시험일정 메뉴 선택
2. 달력에서 날짜 선택
3. 시험명 입력 후 저장
4. 시험일자 및 접수일자 설정

### 학습 관리
1. 스터디 메뉴에서 학습 목표 설정
2. 타이머로 공부 시간 측정
3. 학습 완료 후 기록 저장

## 🔧 주요 클래스 설명

### MainActivity
- 앱의 진입점
- 각 기능으로의 네비게이션 제공
- 즐겨찾기 알림 표시

### ScheduleActivity
- 시험 일정 관리의 핵심 클래스
- ViewPager2를 이용한 월별 달력 구현
- 일정 등록 및 수정 기능

### TimerActivity
- Chronometer를 이용한 정확한 시간 측정
- SharedPreferences를 통한 학습 기록 저장
- MediaPlayer를 이용한 배경음악 재생

### StarActivity (Object)
- 즐겨찾기 데이터 관리를 위한 싱글톤 객체
- SharedPreferences를 통한 데이터 영속성

## 📊 데이터 관리

### SharedPreferences 사용
- **즐겨찾기 데이터**: `favorite_prefs`
- **학습 시간 기록**: `StudyTimerPrefs`
- **오답노트 설정**: `oxnote`

### 데이터 구조
```kotlin
// 시험 일정 데이터
data class ExamSchedule(
    val name: String,
    val examDate: String,
    val registrationDate: String,
    val year: Int,
    val month: Int,
    val day: Int
)

// 학습 과제 데이터
data class StudyTask(
    val id: String,
    var text: String,
    var isCompleted: Boolean,
    val year: Int,
    val month: Int,
    val day: Int
)
```
