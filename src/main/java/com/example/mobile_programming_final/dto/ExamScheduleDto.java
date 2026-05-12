package com.example.mobile_programming_final.dto;

import jakarta.validation.constraints.AssertTrue;
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

    @NotBlank(message = "Exam name is required.")
    private String name;

    @NotNull(message = "Exam date is required.")
    @Future(message = "Exam date must be in the future.")
    private LocalDate examDate;

    @NotNull(message = "Registration date is required.")
    private LocalDate registrationDate;

    @AssertTrue(message = "Registration date must be on or before the exam date.")
    public boolean isDateRangeValid() {
        if (examDate == null || registrationDate == null) {
            return true;
        }
        return !registrationDate.isAfter(examDate);
    }
}
