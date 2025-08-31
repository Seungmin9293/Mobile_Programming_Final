package com.example.mobile_programming_final.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class StudyTask {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private Boolean isCompleted;

    @Column(nullable = false)
    private LocalDate date;

    public StudyTask(String text, Boolean isCompleted, LocalDate date) {
        this.text = text;
        this.isCompleted = isCompleted;
        this.date = date;
    }

    /**
     * DTO로부터 받은 데이터로 엔티티의 상태를 업데이트하는 메서드입니다.
     * @param text 업데이트할 학습 내용
     * @param isCompleted 업데이트할 완료 여부
     * @param date 업데이트할 날짜
     */
    public void update(String text, Boolean isCompleted, LocalDate date) {
        this.text = text;
        this.isCompleted = isCompleted;
        this.date = date;
    }
}