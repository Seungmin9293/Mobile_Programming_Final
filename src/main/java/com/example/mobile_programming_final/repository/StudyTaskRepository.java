package com.example.mobile_programming_final.repository;



import com.example.mobile_programming_final.entity.StudyTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StudyTaskRepository extends JpaRepository<StudyTask, UUID> {
}