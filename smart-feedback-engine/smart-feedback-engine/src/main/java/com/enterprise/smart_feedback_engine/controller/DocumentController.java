package com.enterprise.smart_feedback_engine.controller;

import com.enterprise.smart_feedback_engine.dto.DocumentResponseDto;
import com.enterprise.smart_feedback_engine.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "2. Doküman Yönetimi", description = "PDF/TXT yükleme, listeleme ve filtreleme")
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Document Controller", description = "Doküman Yükleme ve Yönetim Uç Noktaları")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Yeni doküman (PDF/TXT) yükle ve içeriğini kaydet")
    public ResponseEntity<DocumentResponseDto> uploadDocument(
            @RequestParam("title") String title,
            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentService.uploadDocument(title, tags, file, userDetails.getUsername()));
    }

    @GetMapping
    @Operation(summary = "Dokümanları listele (Admin hepsini, normal kullanıcı kendisininkileri görür)")
    public ResponseEntity<Page<DocumentResponseDto>> getAllDocuments(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(documentService.getAllDocuments(userDetails.getUsername(), pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "ID ile doküman detayını ve içeriğini getir")
    public ResponseEntity<DocumentResponseDto> getDocumentById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(documentService.getDocumentById(id, userDetails.getUsername()));
    }
    @GetMapping("/search")
    @Operation(summary = "Dokümanlarda gelişmiş arama ve filtreleme (Başlık, içerik, etiket, sayfalama)")
    public ResponseEntity<Page<DocumentResponseDto>> searchDocuments(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String tag,
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdDate") Pageable pageable) {

        return ResponseEntity.ok(documentService.searchDocuments(query, tag, userDetails.getUsername(), pageable));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Dokümanı sil (Soft-delete)")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        documentService.deleteDocument(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{id}/analyze")
    @Operation(summary = "Yüklenen dokümanı Python AI servisi ile analiz et (Özet ve Sınıflandırma)")
    public ResponseEntity<Object> analyzeDocument(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        DocumentResponseDto doc = documentService.getDocumentById(id, userDetails.getUsername());

        // Python AI mikroservisine doğrudan backend üzerinden istek atıyoruz (CORS sorunu olmaz)
        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
        String pythonUrl = "http://localhost:5000/analyze-document";

        java.util.Map<String, String> request = java.util.Map.of("content", doc.getContent() != null ? doc.getContent() : "");

        try {
            Object aiResponse = restTemplate.postForObject(pythonUrl, request, Object.class);
            return ResponseEntity.ok(aiResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(java.util.Map.of("summary", "AI servisine ulaşılamadı.", "category", "Belirsiz"));
        }
    }
}