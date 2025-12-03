package uzumtech.notification.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uzumtech.notification.dto.price.PriceCreateRequestDto;
import uzumtech.notification.dto.price.PriceResponseDto;
import uzumtech.notification.dto.price.PriceUpdateRequestDto;
import uzumtech.notification.entity.Price;

// Mapper для преобразования между Price Entity и DTO
@Mapper(componentModel = "spring")
public interface PriceMapper {

    // Преобразовать CreateRequestDto в Entity
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(source = "price", target = "price")
    @Mapping(target = "active", constant = "true")
    Price toEntity(PriceCreateRequestDto dto);

    // Обновить существующий Entity из UpdateRequestDto
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(source = "price", target = "price")
    void updateEntityFromDto(PriceUpdateRequestDto dto, @MappingTarget Price entity);

    // Преобразовать Entity в ResponseDto
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "active", target = "isActive")
    @Mapping(source = "createdAt", target = "createdAt")
    PriceResponseDto toResponseDto(Price entity);
}
