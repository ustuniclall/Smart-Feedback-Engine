package com.enterprise.smart_feedback_engine.service;

import com.enterprise.smart_feedback_engine.client.AiServiceClient;
import com.enterprise.smart_feedback_engine.dto.AiPredictionResponseDto;
import com.enterprise.smart_feedback_engine.dto.FeedbackRequestDto;
import com.enterprise.smart_feedback_engine.dto.FeedbackResponseDto;
import com.enterprise.smart_feedback_engine.entity.Category;
import com.enterprise.smart_feedback_engine.entity.Feedback;
import com.enterprise.smart_feedback_engine.entity.SentimentType;
import com.enterprise.smart_feedback_engine.mapper.FeedbackMapper;
import com.enterprise.smart_feedback_engine.repository.CategoryRepository;
import com.enterprise.smart_feedback_engine.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final CategoryRepository categoryRepository;
    private final FeedbackMapper feedbackMapper;
    private final AiServiceClient aiServiceClient;

    @Transactional
    public FeedbackResponseDto createFeedback(FeedbackRequestDto requestDto) {
        // 1. DTO -> Entity dönüşümü
        Feedback feedback = feedbackMapper.toEntity(requestDto);

        // 2. Kategori kontrolü ve ilişkilendirme
        if (requestDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(requestDto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Kategori bulunamadı: ID " + requestDto.getCategoryId()));
            feedback.setCategory(category);
        }

        // 3. Python AI Servisine istek atma ve duygu analizi sonucu alma
        AiPredictionResponseDto aiResponse = aiServiceClient.predictSentiment(requestDto.getContent());

        try {
            feedback.setSentiment(SentimentType.valueOf(aiResponse.getSentiment().toUpperCase()));
        } catch (IllegalArgumentException | NullPointerException e) {
            feedback.setSentiment(SentimentType.NEUTRAL);
        }
        feedback.setConfidenceScore(aiResponse.getConfidence());

        // 4. Veritabanına kaydetme
        Feedback savedFeedback = feedbackRepository.save(feedback);

        // 5. Entity -> Response DTO dönüşümü
        return feedbackMapper.toDto(savedFeedback);
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponseDto> getAllFeedbacks() {
        return feedbackMapper.toDtoList(feedbackRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponseDto> getFeedbacksBySentiment(SentimentType sentiment) {
        return feedbackMapper.toDtoList(feedbackRepository.findBySentiment(sentiment));
    }
}