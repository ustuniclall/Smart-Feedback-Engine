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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private FeedbackMapper feedbackMapper;

    @Mock
    private AiServiceClient aiServiceClient;

    @InjectMocks
    private FeedbackService feedbackService;

    @Test
    @DisplayName("Geri bildirim başarıyla oluşturulmalı ve AI duygu analizi atanmalı")
    void shouldCreateFeedbackSuccessfullyWithAiSentiment() {
        // Given
        FeedbackRequestDto requestDto = new FeedbackRequestDto();
        requestDto.setCustomerName("İclal Üstün");
        requestDto.setContent("Harika bir deneyim!");
        requestDto.setCategoryId(1L);

        Category category = Category.builder().id(1L).name("Yazılım").build();

        Feedback feedback = Feedback.builder()
                .customerName(requestDto.getCustomerName())
                .content(requestDto.getContent())
                .build();

        AiPredictionResponseDto aiResponse = new AiPredictionResponseDto();
        aiResponse.setSentiment("POSITIVE");
        aiResponse.setConfidence(0.85);

        Feedback savedFeedback = Feedback.builder()
                .id(1L)
                .customerName("İclal Üstün")
                .content("Harika bir deneyim!")
                .sentiment(SentimentType.POSITIVE)
                .confidenceScore(0.85)
                .category(category)
                .build();

        FeedbackResponseDto expectedResponse = new FeedbackResponseDto();
        expectedResponse.setId(1L);
        expectedResponse.setCustomerName("İclal Üstün");
        expectedResponse.setSentiment(SentimentType.POSITIVE);
        expectedResponse.setConfidenceScore(0.85);

        when(feedbackMapper.toEntity(requestDto)).thenReturn(feedback);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(aiServiceClient.predictSentiment(requestDto.getContent())).thenReturn(aiResponse);
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(savedFeedback);
        when(feedbackMapper.toDto(savedFeedback)).thenReturn(expectedResponse);

        // When
        FeedbackResponseDto result = feedbackService.createFeedback(requestDto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(SentimentType.POSITIVE, result.getSentiment());
        assertEquals(0.85, result.getConfidenceScore());

        verify(categoryRepository, times(1)).findById(1L);
        verify(aiServiceClient, times(1)).predictSentiment("Harika bir deneyim!");
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
    @DisplayName("Geçersiz kategori ID girildiğinde hata fırlatmalı")
    void shouldThrowExceptionWhenCategoryNotFound() {
        // Given
        FeedbackRequestDto requestDto = new FeedbackRequestDto();
        requestDto.setCategoryId(99L);
        requestDto.setContent("Test içeriği");

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> feedbackService.createFeedback(requestDto));
        verify(categoryRepository, times(1)).findById(99L);
        verify(feedbackRepository, never()).save(any(Feedback.class));
    }
}