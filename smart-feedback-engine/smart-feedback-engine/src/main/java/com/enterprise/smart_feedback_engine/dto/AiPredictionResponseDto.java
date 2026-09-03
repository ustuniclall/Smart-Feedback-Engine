package com.enterprise.smart_feedback_engine.dto;

import lombok.Getter;
import lombok.Setter;

//Python Flask mikroservisine istek attığımızda oradan dönecek olan
//JSON yapısını ({"sentiment": "POSITIVE", "confidence": 0.94}) karşılayan veri modelidir.
@Getter
@Setter
public class AiPredictionResponseDto {
    private String sentiment;
    private Double confidence;
}