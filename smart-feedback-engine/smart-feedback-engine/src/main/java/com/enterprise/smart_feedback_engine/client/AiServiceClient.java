package com.enterprise.smart_feedback_engine.client;

import com.enterprise.smart_feedback_engine.dto.AiPredictionRequestDto;
import com.enterprise.smart_feedback_engine.dto.AiPredictionResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

//Spring Boot 3'ün güncel ve performanslı aracı RestClient kullanılarak
//Python Flask servisinin /predict ucuna JSON gövdesiyle istek atan bir istemci yazdık.
//AI servisi yanıt vermezse uygulamanın çökmemesi için varsayılan bir duygu (NEUTRAL)
//dönen hata yakalama mekanizması (Fallback) ekledik.
@Component
public class AiServiceClient {

    private final RestClient restClient;

    public AiServiceClient(@Value("${ai-service.url:http://localhost:5000/predict}") String aiServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(aiServiceUrl)
                .build();
    }

    public AiPredictionResponseDto predictSentiment(String text) {
        try {
            return restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new AiPredictionRequestDto(text))
                    .retrieve()
                    .body(AiPredictionResponseDto.class);
        } catch (Exception e) {
            // Python servisi kapalıysa veya hata dönerse varsayılan değer atanır (Fallback)
            AiPredictionResponseDto fallback = new AiPredictionResponseDto();
            fallback.setSentiment("NEUTRAL");
            fallback.setConfidence(0.0);
            return fallback;
        }
    }
}