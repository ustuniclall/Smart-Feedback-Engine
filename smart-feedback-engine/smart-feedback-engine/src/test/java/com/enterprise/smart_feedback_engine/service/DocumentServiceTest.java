package com.enterprise.smart_feedback_engine.service;

import com.enterprise.smart_feedback_engine.dto.DocumentResponseDto;
import com.enterprise.smart_feedback_engine.entity.Document;
import com.enterprise.smart_feedback_engine.entity.Tag;
import com.enterprise.smart_feedback_engine.entity.User;
import com.enterprise.smart_feedback_engine.repository.DocumentRepository;
import com.enterprise.smart_feedback_engine.repository.TagRepository;
import com.enterprise.smart_feedback_engine.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private FileParserService fileParserService;

    @InjectMocks
    private DocumentService documentService;

    @Test
    @DisplayName("TXT/PDF dokumani basariyla yuklenmeli ve DTO donmeli")
    void shouldUploadDocumentSuccessfully() throws IOException {
        // Given
        User user = User.builder().id(1L).username("iclal").build();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-document.txt",
                "text/plain",
                "Kurumsal bilgi bankasi icerigi".getBytes()
        );

        Tag tag = Tag.builder().id(1L).name("genel").build();

        Document savedDoc = Document.builder()
                .id(1L)
                .title("Test Baslik")
                .fileName("test-document.txt")
                .fileType("text/plain")
                .content("Kurumsal bilgi bankasi icerigi")
                .uploadedBy(user)
                .tags(Collections.singleton(tag))
                .build();

        when(userRepository.findByUsername("iclal")).thenReturn(Optional.of(user));
        when(fileParserService.extractText(file)).thenReturn("Kurumsal bilgi bankasi icerigi");
        when(tagRepository.findByName("genel")).thenReturn(Optional.of(tag));
        when(documentRepository.save(any(Document.class))).thenReturn(savedDoc);

        // When
        DocumentResponseDto result = documentService.uploadDocument("Test Baslik", List.of("genel"), file, "iclal");

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test-document.txt", result.getFileName());
        assertEquals("Test Baslik", result.getTitle());
        verify(userRepository, times(1)).findByUsername("iclal");
        verify(fileParserService, times(1)).extractText(file);
        verify(documentRepository, times(1)).save(any(Document.class));
    }

    @Test
    @DisplayName("Dokuman silme islemi basariyla tamamlanmali")
    void shouldDeleteDocumentSuccessfully() {
        // Given
        Long docId = 1L;
        User user = User.builder().id(1L).username("iclal").build();
        Document document = Document.builder().id(docId).fileName("sample.pdf").uploadedBy(user).build();

        when(userRepository.findByUsername("iclal")).thenReturn(Optional.of(user));
        lenient().when(documentRepository.findById(docId)).thenReturn(Optional.of(document));
        lenient().when(documentRepository.findByIdAndIsDeletedFalse(docId)).thenReturn(Optional.of(document));

        // When & Then
        assertDoesNotThrow(() -> documentService.deleteDocument(docId, "iclal"));
        verify(userRepository, times(1)).findByUsername("iclal");
    }
}