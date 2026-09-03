package com.enterprise.smart_feedback_engine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Python API'nin beklediği { "text": "..." } formatına uygun Java modelini tanımladık.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiPredictionRequestDto {
    private String text;
}