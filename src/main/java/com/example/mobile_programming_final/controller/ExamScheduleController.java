package com.example.mobile_programming_final.controller;

import com.example.mobile_programming_final.dto.ExamScheduleDto;
import com.example.mobile_programming_final.service.ExamScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ExamScheduleController {

    private final ExamScheduleService examScheduleService;

    /**
     * 모든 시험 일정을 조회하는 API
     * @return 시험 일정 목록과 HTTP 200 OK 상태 코드
     */
    @GetMapping
    public ResponseEntity<List<ExamScheduleDto>> getAllExamSchedules() {
        List<ExamScheduleDto> schedules = examScheduleService.getAllExamSchedules();
        return ResponseEntity.ok(schedules);
    }

    /**
     * ID로 특정 시험 일정을 조회하는 API
     * @param id 조회할 시험 일정의 ID
     * @return 해당 시험 일정 정보와 HTTP 200 OK 상태 코드
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExamScheduleDto> getExamScheduleById(@PathVariable Long id) {
        ExamScheduleDto schedule = examScheduleService.getExamScheduleById(id);
        return ResponseEntity.ok(schedule);
    }

    /**
     * 새로운 시험 일정을 생성하는 API
     * @param examScheduleDto 생성할 시험 일정 데이터
     * @return 생성된 시험 일정 정보와 HTTP 201 Created 상태 코드
     */
    @PostMapping
    public ResponseEntity<ExamScheduleDto> createExamSchedule(@RequestBody ExamScheduleDto examScheduleDto) {
        ExamScheduleDto createdSchedule = examScheduleService.createExamSchedule(examScheduleDto);
        return new ResponseEntity<>(createdSchedule, HttpStatus.CREATED);
    }

    /**
     * 기존 시험 일정을 수정하는 API
     * @param id 수정할 시험 일정의 ID
     * @param examScheduleDto 수정할 시험 일정 데이터
     * @return 수정된 시험 일정 정보와 HTTP 200 OK 상태 코드
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExamScheduleDto> updateExamSchedule(@PathVariable Long id, @RequestBody ExamScheduleDto examScheduleDto) {
        ExamScheduleDto updatedSchedule = examScheduleService.updateExamSchedule(id, examScheduleDto);
        return ResponseEntity.ok(updatedSchedule);
    }

    /**
     * ID로 특정 시험 일정을 삭제하는 API
     * @param id 삭제할 시험 일정의 ID
     * @return 내용 없이 HTTP 204 No Content 상태 코드
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExamSchedule(@PathVariable Long id) {
        examScheduleService.deleteExamSchedule(id);
        return ResponseEntity.noContent().build();
    }
}