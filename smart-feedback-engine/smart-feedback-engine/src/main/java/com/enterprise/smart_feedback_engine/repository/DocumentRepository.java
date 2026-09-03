package com.enterprise.smart_feedback_engine.repository;

import com.enterprise.smart_feedback_engine.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {
    Page<Document> findAllByUploadedBy_UsernameAndIsDeletedFalse(String username, Pageable pageable);
    Page<Document> findAllByIsDeletedFalse(Pageable pageable);
    Optional<Document> findByIdAndIsDeletedFalse(Long id);
}