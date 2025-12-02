package uzumtech.notification.service.price;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uzumtech.notification.constant.enums.NotificationStatus;
import uzumtech.notification.constant.enums.NotificationType;
import uzumtech.notification.entity.Notification;
import uzumtech.notification.entity.Price;
import uzumtech.notification.exception.notification.PriceNotFoundException;
import uzumtech.notification.repository.NotificationRepository;
import uzumtech.notification.repository.PriceRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final PriceRepository priceRepository;
    private final NotificationRepository notificationRepository;

    /**
     * Получить активный прайс (например, 85 сум за 1 SMS)
     */
    public Price getActivePrice() {
        return priceRepository.findByIsActiveTrue()
                .orElseThrow(() -> new PriceNotFoundException("Активный прайс не найден"));
    }

    /**
     * Рассчитать сумму за отправленные SMS уведомления мерчанта за произвольный период
     *
     * @param merchantId - ID мерчанта
     * @param from       - дата начала периода
     * @param to         - дата конца периода
     * @return сумма в суммах
     */
    public Long calculateSmsCost(Long merchantId, LocalDate from, LocalDate to) {
        Price price = getActivePrice(); // получаем цену за одно SMS

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(23, 59, 59);

        // Берём только отправленные SMS уведомления мерчанта
        List<Notification> sentSms = notificationRepository
                .findAllByMerchantIdAndTypeAndStatusAndCreatedAtBetween(
                        merchantId,
                        NotificationType.SMS,
                        NotificationStatus.SENT,
                        start,
                        end
                );

        int count = sentSms.size();       // количество отправленных SMS
        return price.getPrice() * (long) count; // итоговая сумма
    }

    /**
     * Опционально: создать уведомление о сумме для мерчанта
     */
    @Transactional
    public void notifyMerchantMonthlyCost(Long merchantId, LocalDate from, LocalDate to) {
        Long total = calculateSmsCost(merchantId, from, to);

        Notification notification = new Notification();
        notification.setRecipient("merchant-" + merchantId);
        notification.setMessage("В этом периоде вы использовали SMS на сумму: " + total + " сум");
        notification.setType(NotificationType.SMS);

        notificationRepository.save(notification);
    }
}
