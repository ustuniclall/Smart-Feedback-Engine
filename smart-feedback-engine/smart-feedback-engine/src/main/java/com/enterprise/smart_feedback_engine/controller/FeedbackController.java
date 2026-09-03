package com.enterprise.smart_feedback_engine.controller;

import com.enterprise.smart_feedback_engine.dto.FeedbackRequestDto;
import com.enterprise.smart_feedback_engine.dto.FeedbackResponseDto;
import com.enterprise.smart_feedback_engine.entity.SentimentType;
import com.enterprise.smart_feedback_engine.service.FeedbackService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "4. Geri Bildirim & Değerlendirme", description = "Sistem geri bildirimleri")
@RestController
@RequestMapping("/api/v1/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<FeedbackResponseDto> createFeedback(@Valid @RequestBody FeedbackRequestDto requestDto) {
        FeedbackResponseDto response = feedbackService.createFeedback(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<FeedbackResponseDto>> getAllFeedbacks() {
        return ResponseEntity.ok(feedbackService.getAllFeedbacks());
    }

    @GetMapping("/sentiment/{sentiment}")
    public ResponseEntity<List<FeedbackResponseDto>> getFeedbacksBySentiment(@PathVariable SentimentType sentiment) {
        return ResponseEntity.ok(feedbackService.getFeedbacksBySentiment(sentiment));
    }
}