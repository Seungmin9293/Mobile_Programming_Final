package com.example.mobile_programming_final.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class StudyTaskDto {

    private UUID id;

    @NotBlank(message = "Task text is required.")
    private String text;

    @NotNull(message = "Completion status is required.")
    private Boolean isCompleted;

    @NotNull(message = "Task date is required.")
    private LocalDate date;
}
