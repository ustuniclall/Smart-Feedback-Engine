package com.enterprise.smart_feedback_engine.service;

import com.enterprise.smart_feedback_engine.dto.DocumentResponseDto;
import com.enterprise.smart_feedback_engine.entity.Document;
import com.enterprise.smart_feedback_engine.entity.Role;
import com.enterprise.smart_feedback_engine.entity.Tag;
import com.enterprise.smart_feedback_engine.entity.User;
import com.enterprise.smart_feedback_engine.repository.DocumentRepository;
import com.enterprise.smart_feedback_engine.repository.DocumentSpecification;
import com.enterprise.smart_feedback_engine.repository.TagRepository;
import com.enterprise.smart_feedback_engine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final FileParserService fileParserService;

    @Transactional
    public DocumentResponseDto uploadDocument(String title, List<String> tagNames, MultipartFile file, String username) throws IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı: " + username));

        String extractedContent = fileParserService.extractText(file);

        Set<Tag> tags = new HashSet<>();
        if (tagNames != null && !tagNames.isEmpty()) {
            for (String tagName : tagNames) {
                Tag tag = tagRepository.findByName(tagName.trim())
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName.trim()).build()));
                tags.add(tag);
            }
        }

        Document document = Document.builder()
                .title(title)
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .content(extractedContent)
                .uploadedBy(user)
                .tags(tags)
                .build();

        Document savedDoc = documentRepository.save(document);
        return mapToDto(savedDoc);
    }

    @Transactional(readOnly = true)
    public Page<DocumentResponseDto> getAllDocuments(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı"));

        Page<Document> documents;
        if (user.getRole() == Role.ROLE_ADMIN) {
            documents = documentRepository.findAllByIsDeletedFalse(pageable);
        } else {
            documents = documentRepository.findAllByUploadedBy_UsernameAndIsDeletedFalse(username, pageable);
        }

        return documents.map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public DocumentResponseDto getDocumentById(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı"));

        Document document = documentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Doküman bulunamadı. ID: " + id));

        if (user.getRole() != Role.ROLE_ADMIN && !document.getUploadedBy().getUsername().equals(username)) {
            throw new AccessDeniedException("Bu dokümana erişim yetkiniz yok.");
        }

        return mapToDto(document);
    }

    private DocumentResponseDto mapToDto(Document doc) {
        return DocumentResponseDto.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .fileName(doc.getFileName())
                .fileType(doc.getFileType())
                .fileSize(doc.getFileSize())
                .content(doc.getContent())
                .uploadedBy(doc.getUploadedBy().getUsername())
                .tags(doc.getTags().stream().map(Tag::getName).collect(Collectors.toSet()))
                .createdDate(doc.getCreatedDate())
                .build();
    }
    @Transactional(readOnly = true)
    public Page<DocumentResponseDto> searchDocuments(String searchTerm, String tag, String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı"));

        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        Specification<Document> spec = DocumentSpecification.filterDocuments(searchTerm, tag, username, isAdmin);
        return documentRepository.findAll(spec, pageable).map(this::mapToDto);
    }

    @Transactional
    public void deleteDocument(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı"));

        Document document = documentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Doküman bulunamadı. ID: " + id));

        if (user.getRole() != Role.ROLE_ADMIN && !document.getUploadedBy().getUsername().equals(username)) {
            throw new AccessDeniedException("Bu dokümanı silme yetkiniz bulunmamaktadır.");
        }

        // Soft-delete: Fiziksel silmek yerine isDeleted bayrağını true yapıyoruz
        document.setDeleted(true);
        documentRepository.save(document);
    }
}