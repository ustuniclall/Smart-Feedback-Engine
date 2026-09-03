package com.enterprise.smart_feedback_engine.repository;

import com.enterprise.smart_feedback_engine.entity.Document;
import com.enterprise.smart_feedback_engine.entity.Tag;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class DocumentSpecification {

    public static Specification<Document> filterDocuments(String searchTerm, String tag, String username, boolean isAdmin) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Soft-delete kontrolü (Silinmemiş olanlar)
            predicates.add(criteriaBuilder.isFalse(root.get("isDeleted")));

            // 2. Yetki kontrolü (Admin değilse sadece kendi dokümanları)
            if (!isAdmin && username != null) {
                predicates.add(criteriaBuilder.equal(root.get("uploadedBy").get("username"), username));
            }

            // 3. Genel Arama (Başlıkta veya İçerikte geçiyor mu?)
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String likePattern = "%" + searchTerm.trim().toLowerCase() + "%";
                Predicate titleMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), likePattern);
                Predicate contentMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("content")), likePattern);
                predicates.add(criteriaBuilder.or(titleMatch, contentMatch));
            }

            // 4. Etiket Filtresi
            if (tag != null && !tag.trim().isEmpty()) {
                Join<Document, Tag> tagJoin = root.join("tags");
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(tagJoin.get("name")), tag.trim().toLowerCase()));
            }

            // Sorgunun tekil sonuç dönmesi için distinct ekle
            query.distinct(true);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}