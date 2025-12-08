package uzumtech.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uzumtech.notification.constant.enums.NotificationStatus;
import uzumtech.notification.constant.enums.NotificationType;
import uzumtech.notification.entity.Notification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>,
        JpaSpecificationExecutor<Notification> {

    // Поиск SMS уведомлений по мерчанту, типу, статусу и диапазону дат для биллинга
    // Используется в MerchantSmsBillingService
    List<Notification> findAllByMerchant_IdAndTypeAndStatusAndCreatedAtBetween(
            Long merchantId,
            NotificationType type,
            NotificationStatus status,
            LocalDateTime start,
            LocalDateTime end
    );

    // Поиск уведомления по idempotency key для обеспечения идемпотентности
    Optional<Notification> findByIdempotencyKey(String idempotencyKey);
}
