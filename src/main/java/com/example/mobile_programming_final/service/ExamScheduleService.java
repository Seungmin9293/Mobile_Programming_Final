package com.example.mobile_programming_final.service;

import com.example.mobile_programming_final.dto.ExamScheduleDto;
import com.example.mobile_programming_final.entity.ExamSchedule;
import com.example.mobile_programming_final.repository.ExamScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamScheduleService {

    private final ExamScheduleRepository examScheduleRepository;

    public List<ExamScheduleDto> getAllExamSchedules() {
        return examScheduleRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ExamScheduleDto getExamScheduleById(Long id) {
        ExamSchedule examSchedule = examScheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Exam schedule not found for id: " + id));
        return convertToDto(examSchedule);
    }

    @Transactional
    public ExamScheduleDto createExamSchedule(ExamScheduleDto examScheduleDto) {
        ExamSchedule examSchedule = new ExamSchedule(
                examScheduleDto.getName(),
                examScheduleDto.getExamDate(),
                examScheduleDto.getRegistrationDate()
        );
        ExamSchedule savedExamSchedule = examScheduleRepository.save(examSchedule);
        return convertToDto(savedExamSchedule);
    }

    @Transactional
    public ExamScheduleDto updateExamSchedule(Long id, ExamScheduleDto examScheduleDto) {
        ExamSchedule examSchedule = examScheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Exam schedule not found for id: " + id));

        examSchedule.update(
                examScheduleDto.getName(),
                examScheduleDto.getExamDate(),
                examScheduleDto.getRegistrationDate()
        );

        return convertToDto(examSchedule);
    }

    @Transactional
    public void deleteExamSchedule(Long id) {
        if (!examScheduleRepository.existsById(id)) {
            throw new EntityNotFoundException("Exam schedule not found for id: " + id);
        }

        examScheduleRepository.deleteById(id);
    }

    private ExamScheduleDto convertToDto(ExamSchedule examSchedule) {
        ExamScheduleDto dto = new ExamScheduleDto();
        dto.setId(examSchedule.getId());
        dto.setName(examSchedule.getName());
        dto.setExamDate(examSchedule.getExamDate());
        dto.setRegistrationDate(examSchedule.getRegistrationDate());
        return dto;
    }
}
