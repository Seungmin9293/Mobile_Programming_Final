package com.example.mobile_programming_final.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ExamScheduleDto {

    private Long id;
    private String name;
    private LocalDate examDate;
    private LocalDate registrationDate;
}