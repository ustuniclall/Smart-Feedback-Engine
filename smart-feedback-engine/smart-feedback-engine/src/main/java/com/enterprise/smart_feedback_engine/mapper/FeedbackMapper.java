package com.enterprise.smart_feedback_engine.mapper;

import com.enterprise.smart_feedback_engine.dto.FeedbackRequestDto;
import com.enterprise.smart_feedback_engine.dto.FeedbackResponseDto;
import com.enterprise.smart_feedback_engine.entity.Feedback;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

//FeedbackRequestDto $\rightarrow$ Feedback (Entity) ve Feedback (Entity) $\rightarrow$ FeedbackResponseDto
//dönüşümlerini otomatikleştirir. @Mapping anotasyonları ile category.name alanını DTO'daki
//categoryName alanına eşler.
@Mapper(componentModel = "spring")
public interface FeedbackMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "sentiment", ignore = true)
    @Mapping(target = "confidenceScore", ignore = true)
    Feedback toEntity(FeedbackRequestDto dto);

    @Mapping(source = "category.name", target = "categoryName")
    FeedbackResponseDto toDto(Feedback entity);

    List<FeedbackResponseDto> toDtoList(List<Feedback> entities);
}