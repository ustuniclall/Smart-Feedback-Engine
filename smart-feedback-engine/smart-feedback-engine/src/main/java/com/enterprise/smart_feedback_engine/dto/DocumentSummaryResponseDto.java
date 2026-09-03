package com.enterprise.smart_feedback_engine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentSummaryResponseDto {
    private Long documentId;
    private String title;
    private String detectedCategory;
    private String summary;
    private List<String> keyPoints;
}