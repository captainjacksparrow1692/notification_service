package uzumtech.notification.service.price;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uzumtech.notification.constant.enums.NotificationStatus;
import uzumtech.notification.constant.enums.NotificationType;
import uzumtech.notification.entity.Notification;
import uzumtech.notification.repository.NotificationRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantSmsBillingService {

    private final NotificationRepository notificationRepository;
    private final PriceService priceService;

    /**
     * Рассчитать сумму за отправленные SMS, учитывая исторические цены.
     */
    public Long calculateSmsCost(Long merchantId, LocalDate from, LocalDate to) {

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(23, 59, 59);

        // получаем SMS за период
        List<Notification> sentSms = notificationRepository
                .findAllByMerchantIdAndTypeAndStatusAndCreatedAtBetween(
                        merchantId,
                        NotificationType.SMS,
                        NotificationStatus.SENT,
                        start,
                        end
                );

        long total = 0L;

        // для каждой SMS — своя цена
        for (Notification sms : sentSms) {
            Long priceOnDate = priceService.getPriceAt(sms.getCreatedAt());
            total += priceOnDate;
        }

        return total;
    }

    /**
     * Создать уведомление мерчанту о сумме за SMS за период
     */
    @Transactional
    public void notifyMerchant(Long merchantId, LocalDate from, LocalDate to) {
        Long total = calculateSmsCost(merchantId, from, to);

        Notification notification = new Notification();
        notification.setRecipient("merchant-" + merchantId);
        notification.setMessage("В этом периоде вы использовали SMS на сумму: " + total + " сум");
        notification.setType(NotificationType.SMS);
        notification.setStatus(NotificationStatus.SENT);

        notificationRepository.save(notification);
    }
}
