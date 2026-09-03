package com.enterprise.smart_feedback_engine.controller;

import com.enterprise.smart_feedback_engine.dto.DocumentQnaRequestDto;
import com.enterprise.smart_feedback_engine.dto.DocumentQnaResponseDto;
import com.enterprise.smart_feedback_engine.dto.DocumentSummaryResponseDto;
import com.enterprise.smart_feedback_engine.service.KnowledgeAiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "3. Yapay Zeka & Q&A", description = "LLM destekli özet ve soru-cevap")
@RestController
@RequestMapping("/api/v1/ai/documents")
@RequiredArgsConstructor
@Tag(name = "AI Knowledge Controller", description = "Doküman Özetleme, Kategori Tespiti ve Soru-Cevap (Q&A) Uç Noktaları")
public class KnowledgeAiController {

    private final KnowledgeAiService knowledgeAiService;

    @PostMapping("/{id}/summarize")
    @Operation(summary = "Dokümanı analiz et, kategorisini bul ve yönetici özeti çıkar")
    public ResponseEntity<DocumentSummaryResponseDto> summarizeDocument(@PathVariable Long id) {
        return ResponseEntity.ok(knowledgeAiService.summarizeDocument(id));
    }

    @PostMapping("/{id}/ask")
    @Operation(summary = "Doküman içeriğine dayalı soru sor (Q&A)")
    public ResponseEntity<DocumentQnaResponseDto> askQuestion(
            @PathVariable Long id,
            @Valid @RequestBody DocumentQnaRequestDto request) {

        return ResponseEntity.ok(knowledgeAiService.askQuestion(id, request));
    }
}