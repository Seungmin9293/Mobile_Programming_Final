package com.example.mobile_programming_final.service;

import com.example.mobile_programming_final.dto.StudyTaskDto;
import com.example.mobile_programming_final.entity.StudyTask;
import com.example.mobile_programming_final.repository.StudyTaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyTaskService {

    private final StudyTaskRepository studyTaskRepository;

    public List<StudyTaskDto> getAllStudyTasks() {
        return studyTaskRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public StudyTaskDto getStudyTaskById(UUID id) {
        StudyTask studyTask = studyTaskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Study task not found for id: " + id));
        return convertToDto(studyTask);
    }

    @Transactional
    public StudyTaskDto createStudyTask(StudyTaskDto studyTaskDto) {
        StudyTask studyTask = new StudyTask(
                studyTaskDto.getText(),
                studyTaskDto.getIsCompleted(),
                studyTaskDto.getDate()
        );
        StudyTask savedStudyTask = studyTaskRepository.save(studyTask);
        return convertToDto(savedStudyTask);
    }

    @Transactional
    public StudyTaskDto updateStudyTask(UUID id, StudyTaskDto studyTaskDto) {
        StudyTask studyTask = studyTaskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Study task not found for id: " + id));

        studyTask.update(
                studyTaskDto.getText(),
                studyTaskDto.getIsCompleted(),
                studyTaskDto.getDate()
        );

        return convertToDto(studyTask);
    }

    @Transactional
    public void deleteStudyTask(UUID id) {
        if (!studyTaskRepository.existsById(id)) {
            throw new EntityNotFoundException("Study task not found for id: " + id);
        }

        studyTaskRepository.deleteById(id);
    }

    private StudyTaskDto convertToDto(StudyTask studyTask) {
        StudyTaskDto dto = new StudyTaskDto();
        dto.setId(studyTask.getId());
        dto.setText(studyTask.getText());
        dto.setIsCompleted(studyTask.getIsCompleted());
        dto.setDate(studyTask.getDate());
        return dto;
    }
}
