package com.example.mobile_programming_final.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExamSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate examDate;

    @Column(nullable = false)
    private LocalDate registrationDate;

    public ExamSchedule(String name, LocalDate examDate, LocalDate registrationDate) {
        this.name = name;
        this.examDate = examDate;
        this.registrationDate = registrationDate;
    }

    public void update(String name, LocalDate examDate, LocalDate registrationDate) {
        this.name = name;
        this.examDate = examDate;
        this.registrationDate = registrationDate;
    }
}
