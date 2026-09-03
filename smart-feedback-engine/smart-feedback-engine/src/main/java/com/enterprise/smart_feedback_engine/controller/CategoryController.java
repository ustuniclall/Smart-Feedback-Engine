package com.enterprise.smart_feedback_engine.controller;

import com.enterprise.smart_feedback_engine.dto.CategoryRequestDto;
import com.enterprise.smart_feedback_engine.dto.CategoryResponseDto;
import com.enterprise.smart_feedback_engine.entity.Category;
import com.enterprise.smart_feedback_engine.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        List<CategoryResponseDto> response = categoryService.getAllCategories().stream()
                .map(c -> CategoryResponseDto.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .description(c.getDescription())
                        .build())
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryRequestDto requestDto) {
        Category created = categoryService.createCategory(requestDto.getName(), requestDto.getDescription());
        CategoryResponseDto response = CategoryResponseDto.builder()
                .id(created.getId())
                .name(created.getName())
                .description(created.getDescription())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}