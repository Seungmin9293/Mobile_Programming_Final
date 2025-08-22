package com.example.mobile_programming_final.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class StudyTaskDto {

    private UUID id;
    private String text;
    private Boolean isCompleted;
    private LocalDate date;
}