package com.example.mobile_programming_final.controller;

import com.example.mobile_programming_final.dto.StudyTaskDto;
import com.example.mobile_programming_final.service.StudyTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class StudyTaskController {

    private final StudyTaskService studyTaskService;


    @GetMapping
    public ResponseEntity<List<StudyTaskDto>> getAllStudyTasks() {
        List<StudyTaskDto> tasks = studyTaskService.getAllStudyTasks();
        return ResponseEntity.ok(tasks);
    }


    @GetMapping("/{id}")
    public ResponseEntity<StudyTaskDto> getStudyTaskById(@PathVariable UUID id) {
        StudyTaskDto task = studyTaskService.getStudyTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PostMapping
    public ResponseEntity<StudyTaskDto> createStudyTask(@RequestBody StudyTaskDto studyTaskDto) {
        StudyTaskDto createdTask = studyTaskService.createStudyTask(studyTaskDto);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<StudyTaskDto> updateStudyTask(@PathVariable UUID id, @RequestBody StudyTaskDto studyTaskDto) {
        StudyTaskDto updatedTask = studyTaskService.updateStudyTask(id, studyTaskDto);
        return ResponseEntity.ok(updatedTask);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudyTask(@PathVariable UUID id) {
        studyTaskService.deleteStudyTask(id);
        return ResponseEntity.noContent().build();
    }
}