package com.example.mobile_programming_final.service;

import com.example.mobile_programming_final.dto.ExamScheduleDto;
import com.example.mobile_programming_final.entity.ExamSchedule;
import com.example.mobile_programming_final.repository.ExamScheduleRepository;
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

    // 모든 시험 일정 조회
    public List<ExamScheduleDto> getAllExamSchedules() {
        return examScheduleRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // ID로 특정 시험 일정 조회
    public ExamScheduleDto getExamScheduleById(Long id) {
        ExamSchedule examSchedule = examScheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid exam schedule Id:" + id));
        return convertToDto(examSchedule);
    }

    // 시험 일정 생성
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

    // 시험 일정 수정
    @Transactional
    public ExamScheduleDto updateExamSchedule(Long id, ExamScheduleDto examScheduleDto) {
        ExamSchedule examSchedule = examScheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid exam schedule Id:" + id));

        examSchedule.setName(examScheduleDto.getName());
        examSchedule.setExamDate(examScheduleDto.getExamDate());
        examSchedule.setRegistrationDate(examScheduleDto.getRegistrationDate());

        ExamSchedule updatedExamSchedule = examScheduleRepository.save(examSchedule);
        return convertToDto(updatedExamSchedule);
    }

    // 시험 일정 삭제
    @Transactional
    public void deleteExamSchedule(Long id) {
        ExamSchedule examSchedule = examScheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid exam schedule Id:" + id));
        examScheduleRepository.delete(examSchedule);
    }

    // Entity를 DTO로 변환
    private ExamScheduleDto convertToDto(ExamSchedule examSchedule) {
        ExamScheduleDto dto = new ExamScheduleDto();
        dto.setId(examSchedule.getId());
        dto.setName(examSchedule.getName());
        dto.setExamDate(examSchedule.getExamDate());
        dto.setRegistrationDate(examSchedule.getRegistrationDate());
        return dto;
    }
}