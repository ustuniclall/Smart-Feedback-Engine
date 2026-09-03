package com.enterprise.smart_feedback_engine.controller;

import com.enterprise.smart_feedback_engine.dto.FeedbackRequestDto;
import com.enterprise.smart_feedback_engine.dto.FeedbackResponseDto;
import com.enterprise.smart_feedback_engine.entity.SentimentType;
import com.enterprise.smart_feedback_engine.exception.GlobalExceptionHandler;
import com.enterprise.smart_feedback_engine.service.FeedbackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FeedbackControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private FeedbackService feedbackService;

    @InjectMocks
    private FeedbackController feedbackController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(feedbackController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/feedbacks - Gecerli istekte 201 Created donmeli")
    void shouldReturnCreatedWhenValidRequest() throws Exception {
        FeedbackRequestDto requestDto = new FeedbackRequestDto();
        requestDto.setCustomerName("İclal Üstün");
        requestDto.setContent("Sistem cok hizli calisiyor.");
        requestDto.setCategoryId(1L);

        FeedbackResponseDto responseDto = new FeedbackResponseDto();
        responseDto.setId(1L);
        responseDto.setCustomerName("İclal Üstün");
        responseDto.setContent("Sistem cok hizli calisiyor.");
        responseDto.setSentiment(SentimentType.POSITIVE);
        responseDto.setConfidenceScore(0.90);

        when(feedbackService.createFeedback(any(FeedbackRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/feedbacks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.customerName").value("İclal Üstün"))
                .andExpect(jsonPath("$.sentiment").value("POSITIVE"))
                .andExpect(jsonPath("$.confidenceScore").value(0.90));
    }
}