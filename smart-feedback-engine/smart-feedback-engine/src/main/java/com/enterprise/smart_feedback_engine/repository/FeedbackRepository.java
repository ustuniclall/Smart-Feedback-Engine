package com.enterprise.smart_feedback_engine.repository;

import com.enterprise.smart_feedback_engine.entity.Feedback;
import com.enterprise.smart_feedback_engine.entity.SentimentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//Geri bildirim kayıtları üzerinde sorgu yapar.
// Geri bildirimleri duygu durumuna (findBySentiment) veya bağlı olduğu kategoriye (findByCategoryId)
// göre filtreleyen metotları barındırır.
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findBySentiment(SentimentType sentiment);
    List<Feedback> findByCategoryId(Long categoryId);
}