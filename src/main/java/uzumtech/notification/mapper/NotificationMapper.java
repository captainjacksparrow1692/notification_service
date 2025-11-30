package uzumtech.notification.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uzumtech.notification.dto.NotificationSendRequestDto;
import uzumtech.notification.entity.Merchant;
import uzumtech.notification.entity.Notification;
import uzumtech.notification.repository.MerchantRepository;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(source = "merchantId", target = "merchant", qualifiedByName = "merchantFromId")
    @Mapping(source = "receiver", target = "receiverInfo")
    Notification toEntity(NotificationSendRequestDto dto, @Context MerchantRepository merchantRepository);

    @Named("merchantFromId")
    default Merchant mapMerchant(Long merchantId, @Context MerchantRepository merchantRepository) {
        return merchantRepository.findById(merchantId)
            .orElseThrow(() -> new IllegalArgumentException("Merchant not found: " + merchantId));
    }

}