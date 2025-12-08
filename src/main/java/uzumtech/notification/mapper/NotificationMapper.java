package uzumtech.notification.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uzumtech.notification.dto.NotificationResponseDto;
import uzumtech.notification.dto.NotificationSendRequestDto;
import uzumtech.notification.entity.Merchant;
import uzumtech.notification.entity.Notification;
import uzumtech.notification.exception.merchant.MerchantNotFoundException;
import uzumtech.notification.repository.MerchantRepository;

/**
 * Mapper для преобразования DTO в Entity
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "message", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(source = "idempotencyKey", target = "idempotencyKey")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "body", target = "body")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "merchantId", target = "merchant", qualifiedByName = "merchantFromId")
    @Mapping(source = "receiver", target = "receiverInfo")
    Notification toEntity(NotificationSendRequestDto dto, @Context MerchantRepository merchantRepository);

    // Преобразование merchantId в Merchant entity
    @Named("merchantFromId")
    default Merchant mapMerchant(Long merchantId, @Context MerchantRepository merchantRepository) {
        return merchantRepository.findById(merchantId)
            .orElseThrow(() -> new MerchantNotFoundException("Merchant not found with id: " + merchantId));
    }

    // Преобразование Entity в ResponseDto
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "body", target = "body")
    @Mapping(source = "imageUrl", target = "imageUrl")
    @Mapping(source = "merchant.id", target = "merchantId")
    @Mapping(source = "receiverInfo", target = "receiverInfo")
    @Mapping(source = "message", target = "message")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    NotificationResponseDto toResponseDto(Notification entity);
}
