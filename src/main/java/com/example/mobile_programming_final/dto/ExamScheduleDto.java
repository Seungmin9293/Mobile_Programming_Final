package com.example.mobile_programming_final.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ExamScheduleDto {

    private Long id;

    @NotBlank(message = "시험 이름 필수 항목입니다.")
    private String name;

    @NotNull(message = "시험 날짜는 필수 항목입니다.")
    @Future(message = "시험 날짜는 미래로 작성해야합니다.")
    private LocalDate examDate;

    @NotNull(message = "등록 날짜는 필수 항목입니다.")
    private LocalDate registrationDate;
}