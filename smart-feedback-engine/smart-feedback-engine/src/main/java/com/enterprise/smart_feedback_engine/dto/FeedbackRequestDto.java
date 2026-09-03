package com.enterprise.smart_feedback_engine.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

//İstemcinin yeni bir geri bildirim göndermek için dolduracağı şablondur.
// @NotBlank ve @Size gibi validasyonlarla hatalı veya boş verinin iş mantığına girmesini engeller.
@Getter
@Setter
public class FeedbackRequestDto {

    @NotBlank(message = "Müşteri adı boş bırakılamaz")
    @Size(max = 100, message = "Müşteri adı en fazla 100 karakter olabilir")
    private String customerName;

    private String customerEmail;

    @NotBlank(message = "Geri bildirim metni boş bırakılamaz")
    @Size(min = 5, message = "Geri bildirim en az 5 karakter olmalıdır")
    private String content;

    private Long categoryId;
}