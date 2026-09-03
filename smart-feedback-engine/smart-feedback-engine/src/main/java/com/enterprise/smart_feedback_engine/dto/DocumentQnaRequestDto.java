package com.enterprise.smart_feedback_engine.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentQnaRequestDto {

    @NotBlank(message = "Soru alanı boş bırakılamaz.")
    private String question;
}