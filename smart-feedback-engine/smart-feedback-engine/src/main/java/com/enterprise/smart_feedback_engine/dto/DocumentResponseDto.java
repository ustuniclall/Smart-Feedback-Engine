package com.enterprise.smart_feedback_engine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponseDto {
    private Long id;
    private String title;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String content;
    private String uploadedBy;
    private Set<String> tags;
    private LocalDateTime createdDate;
}