package com.example.mobile_programming_final.service;

import com.example.mobile_programming_final.dto.StudyTaskDto;
import com.example.mobile_programming_final.entity.StudyTask;
import com.example.mobile_programming_final.repository.StudyTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyTaskService {

    private final StudyTaskRepository studyTaskRepository;

    // 모든 학습 과제 조회
    public List<StudyTaskDto> getAllStudyTasks() {
        return studyTaskRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // ID로 특정 학습 과제 조회
    public StudyTaskDto getStudyTaskById(UUID id) {

        StudyTask studyTask = studyTaskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 학습 과제를 찾을 수 없습니다: " + id));
        return convertToDto(studyTask);
    }

    // 학습 과제 생성
    @Transactional
    public StudyTaskDto createStudyTask(StudyTaskDto studyTaskDto) {
        // DTO를 Entity로 변환
        StudyTask studyTask = new StudyTask(
                studyTaskDto.getText(),
                studyTaskDto.getIsCompleted(),
                studyTaskDto.getDate()
        );
        StudyTask savedStudyTask = studyTaskRepository.save(studyTask);
        return convertToDto(savedStudyTask);
    }

    // 학습 과제 수정
    @Transactional
    public StudyTaskDto updateStudyTask(UUID id, StudyTaskDto studyTaskDto) {
        StudyTask studyTask = studyTaskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 학습 과제를 찾을 수 없습니다: " + id));

        studyTask.update(studyTaskDto.getText(), studyTaskDto.getIsCompleted(), studyTaskDto.getDate());


        return convertToDto(studyTask);
    }

    // 학습 과제 삭제
    @Transactional
    public void deleteStudyTask(UUID id) {

        if (!studyTaskRepository.existsById(id)) {
            throw new EntityNotFoundException("해당 ID의 학습 과제를 찾을 수 없습니다: " + id);
        }

        studyTaskRepository.deleteById(id);
    }

    // DTO로 변환
    private StudyTaskDto convertToDto(StudyTask studyTask) {
        StudyTaskDto dto = new StudyTaskDto();
        dto.setId(studyTask.getId());
        dto.setText(studyTask.getText());
        dto.setIsCompleted(studyTask.getIsCompleted());
        dto.setDate(studyTask.getDate());
        return dto;
    }
}