package com.enterprise.smart_feedback_engine.dto;

import com.enterprise.smart_feedback_engine.entity.SentimentType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

//Veritabanındaki Feedback kaydını doğrudan istemciye açmak yerine;
//kategori adı, duygu analizi sonucu ve tarih gibi alanları içeren temiz, istemciye özel yanıt modelidir.
@Getter
@Setter
public class FeedbackResponseDto {

    private Long id;
    private String customerName;
    private String customerEmail;
    private String content;
    private SentimentType sentiment;
    private Double confidenceScore;
    private String categoryName;
    private LocalDateTime createdAt;
}