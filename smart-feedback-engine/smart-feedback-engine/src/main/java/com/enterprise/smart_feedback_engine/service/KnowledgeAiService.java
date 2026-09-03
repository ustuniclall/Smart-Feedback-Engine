package com.enterprise.smart_feedback_engine.service;

import com.enterprise.smart_feedback_engine.client.KnowledgeAiClient;
import com.enterprise.smart_feedback_engine.dto.DocumentQnaRequestDto;
import com.enterprise.smart_feedback_engine.dto.DocumentQnaResponseDto;
import com.enterprise.smart_feedback_engine.dto.DocumentSummaryResponseDto;
import com.enterprise.smart_feedback_engine.entity.AiAnalysisLog;
import com.enterprise.smart_feedback_engine.entity.Document;
import com.enterprise.smart_feedback_engine.repository.AiAnalysisLogRepository;
import com.enterprise.smart_feedback_engine.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KnowledgeAiService {

    private final DocumentRepository documentRepository;
    private final AiAnalysisLogRepository aiAnalysisLogRepository;
    private final KnowledgeAiClient knowledgeAiClient;

    @Transactional
    public DocumentSummaryResponseDto summarizeDocument(Long documentId) {
        Document document = documentRepository.findByIdAndIsDeletedFalse(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Doküman bulunamadı. ID: " + documentId));

        String category = knowledgeAiClient.detectCategory(document.getContent());
        String summary = knowledgeAiClient.summarizeDocument(document.getContent());

        AiAnalysisLog log = AiAnalysisLog.builder()
                .analysisType("SUMMARY")
                .detectedCategory(category)
                .response(summary)
                .document(document)
                .build();

        aiAnalysisLogRepository.save(log);

        List<String> keyPoints = Arrays.stream(summary.split("•"))
                .filter(s -> !s.isBlank())
                .map(String::trim)
                .toList();

        return DocumentSummaryResponseDto.builder()
                .documentId(document.getId())
                .title(document.getTitle())
                .detectedCategory(category)
                .summary(summary)
                .keyPoints(keyPoints)
                .build();
    }

    @Transactional
    public DocumentQnaResponseDto askQuestion(Long documentId, DocumentQnaRequestDto request) {
        Document document = documentRepository.findByIdAndIsDeletedFalse(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Doküman bulunamadı. ID: " + documentId));

        String answer = knowledgeAiClient.answerQuestion(document.getContent(), request.getQuestion());

        AiAnalysisLog log = AiAnalysisLog.builder()
                .analysisType("QNA")
                .prompt(request.getQuestion())
                .response(answer)
                .document(document)
                .build();

        aiAnalysisLogRepository.save(log);

        return DocumentQnaResponseDto.builder()
                .documentId(document.getId())
                .question(request.getQuestion())
                .answer(answer)
                .sourceContext(document.getTitle() + " (" + document.getFileName() + ")")
                .build();
    }
}