package uzumtech.notification.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uzumtech.notification.dto.merchant.MerchantCreateRequestDto;
import uzumtech.notification.dto.merchant.MerchantResponseDto;
import uzumtech.notification.dto.merchant.MerchantUpdateRequestDto;
import uzumtech.notification.entity.Merchant;

// Mapper для преобразования между Merchant Entity и DTO
@Mapper(componentModel = "spring")
public interface MerchantMapper {

    // Преобразовать CreateRequestDto в Entity
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "UpdatedAt", ignore = true)
    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "webhook", target = "webhook")
    @Mapping(source = "taxNumber", target = "taxNumber")
    @Mapping(source = "login", target = "login")
    @Mapping(source = "password", target = "passwordHash")
    Merchant toEntity(MerchantCreateRequestDto dto);

    // Обновить существующий Entity из UpdateRequestDto
    // Обновляет только не-null поля из DTO
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "UpdatedAt", ignore = true)
    @Mapping(target = "login", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "webhook", target = "webhook")
    @Mapping(source = "taxNumber", target = "taxNumber")
    void updateEntityFromDto(MerchantUpdateRequestDto dto, @MappingTarget Merchant entity);

    // Преобразовать Entity в ResponseDto
    // passwordHash НЕ включается!
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "webhook", target = "webhook")
    @Mapping(source = "taxNumber", target = "taxNumber")
    @Mapping(source = "login", target = "login")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "UpdatedAt", target = "updatedAt")
    MerchantResponseDto toResponseDto(Merchant entity);
}
