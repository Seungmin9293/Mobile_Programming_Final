package com.example.mobile_programming_final.controller;

import com.example.mobile_programming_final.dto.ExamScheduleDto;
import com.example.mobile_programming_final.service.ExamScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ExamScheduleController {

    private final ExamScheduleService examScheduleService;

    @GetMapping
    public ResponseEntity<List<ExamScheduleDto>> getAllExamSchedules() {
        List<ExamScheduleDto> schedules = examScheduleService.getAllExamSchedules();
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamScheduleDto> getExamScheduleById(@PathVariable Long id) {
        ExamScheduleDto schedule = examScheduleService.getExamScheduleById(id);
        return ResponseEntity.ok(schedule);
    }

    @PostMapping
    public ResponseEntity<ExamScheduleDto> createExamSchedule(@Valid @RequestBody ExamScheduleDto examScheduleDto) {
        ExamScheduleDto createdSchedule = examScheduleService.createExamSchedule(examScheduleDto);
        return new ResponseEntity<>(createdSchedule, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamScheduleDto> updateExamSchedule(@PathVariable Long id, @Valid @RequestBody ExamScheduleDto examScheduleDto) {
        ExamScheduleDto updatedSchedule = examScheduleService.updateExamSchedule(id, examScheduleDto);
        return ResponseEntity.ok(updatedSchedule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExamSchedule(@PathVariable Long id) {
        examScheduleService.deleteExamSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
