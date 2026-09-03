package com.enterprise.smart_feedback_engine.repository;

import com.enterprise.smart_feedback_engine.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//Kategori tablosuna erişimi yönetir.
//Ek olarak kategori adına göre arama (findByName) ve mükerrer kayıt kontrolü (existsByName) sağlar.
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    boolean existsByName(String name);
}