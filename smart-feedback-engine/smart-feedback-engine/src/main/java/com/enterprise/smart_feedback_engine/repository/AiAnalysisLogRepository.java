package com.enterprise.smart_feedback_engine.repository;

import com.enterprise.smart_feedback_engine.entity.AiAnalysisLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiAnalysisLogRepository extends JpaRepository<AiAnalysisLog, Long> {
    List<AiAnalysisLog> findAllByDocument_Id(Long documentId);
}