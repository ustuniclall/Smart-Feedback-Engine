package com.enterprise.smart_feedback_engine.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🏛️ Enterprise AI Knowledge Base API")
                        .version("1.0.0")
                        .description("Akıllı Kurumsal Bilgi Bankası, Doküman Analiz ve Soru-Cevap (Q&A) Motoru")
                        .contact(new Contact().name("İclal Üstün").email("iclal@example.com")))
                .tags(List.of(
                        new Tag().name("1. Kimlik ve Güvenlik (Auth)").description("Kullanıcı kayıt ve JWT token oluşturma uç noktaları"),
                        new Tag().name("2. Doküman Yönetimi").description("PDF/TXT dosya yükleme, metin ayrıştırma ve arama işlemleri"),
                        new Tag().name("3. Yapay Zeka & Q&A").description("Özetleme, kategori tespiti ve dokümana soru sorma motoru"),
                        new Tag().name("4. Geri Bildirim & Değerlendirme").description("Geri bildirim ve duygu analizi servisleri")
                ))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}