package com.enterprise.smart_feedback_engine.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
public class KnowledgeAiClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public KnowledgeAiClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:5000") // Python veya Harici AI Servisi
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public String summarizeDocument(String content) {
        try {
            String response = restClient.post()
                    .uri("/summarize")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("text", content))
                    .retrieve()
                    .body(String.class);

            JsonNode node = objectMapper.readTree(response);
            return node.path("summary").asText();
        } catch (Exception e) {
            log.warn("AI Servisine bağlanılamadı, fallback özet üretiliyor: {}", e.getMessage());
            return generateFallbackSummary(content);
        }
    }

    public String answerQuestion(String context, String question) {
        try {
            String response = restClient.post()
                    .uri("/ask")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("context", context, "question", question))
                    .retrieve()
                    .body(String.class);

            JsonNode node = objectMapper.readTree(response);
            return node.path("answer").asText();
        } catch (Exception e) {
            log.warn("AI Q&A servisine bağlanılamadı, kural tabanlı yanıt üretiliyor: {}", e.getMessage());
            return generateFallbackAnswer(context, question);
        }
    }

    public String detectCategory(String content) {
        String lower = content.toLowerCase();
        if (lower.contains("javascript") || lower.contains("html") || lower.contains("software") || lower.contains("kod") || lower.contains("api")) {
            return "Yazılım / Teknoloji";
        } else if (lower.contains("sözleşme") || lower.contains("hukuk") || lower.contains("kanun") || lower.contains("madde")) {
            return "Hukuk & Mevzuat";
        } else if (lower.contains("bütçe") || lower.contains("maliyet") || lower.contains("fatura") || lower.contains("gelir")) {
            return "Finans & Muhasebe";
        } else if (lower.contains("toplantı") || lower.contains("gündem") || lower.contains("karar")) {
            return "Yönetim & Toplantı Notları";
        }
        return "Genel Doküman";
    }

    private String generateFallbackSummary(String content) {
        String cleanContent = content.replaceAll("\\r\\n|\\r|\\n", " ").trim();
        if (cleanContent.length() > 300) {
            return "Özet: " + cleanContent.substring(0, 300) + "...";
        }
        return "Özet: " + cleanContent;
    }

    private String generateFallbackAnswer(String context, String question) {
        String lowerQuestion = question.toLowerCase();
        String[] lines = context.split("\n");
        for (String line : lines) {
            for (String word : lowerQuestion.split(" ")) {
                if (word.length() > 3 && line.toLowerCase().contains(word)) {
                    return "Dokümandan bulunan ilgili bilgi: " + line.trim();
                }
            }
        }
        return "Sorduğunuz soruya doküman içeriğinde doğrudan bir eşleşme bulunamadı.";
    }
}