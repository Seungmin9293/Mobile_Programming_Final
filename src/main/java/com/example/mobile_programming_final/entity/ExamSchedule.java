package com.example.mobile_programming_final.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
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

    /**
     * DTO -> 엔티티 상태 업데이트 메서드
     * @param name 업데이트할 시험 이름
     * @param examDate 업데이트할 시험 날짜
     * @param registrationDate 업데이트할 등록 날짜
     */
    public void update(String name, LocalDate examDate, LocalDate registrationDate) {
        this.name = name;
        this.examDate = examDate;
        this.registrationDate = registrationDate;
    }
}