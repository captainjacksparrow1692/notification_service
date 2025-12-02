package uzumtech.notification.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uzumtech.notification.constant.enums.NotificationStatus;
import uzumtech.notification.constant.enums.NotificationType;
import uzumtech.notification.entity.Notification;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>,
        JpaSpecificationExecutor<Notification> {

    // =================== Старые методы ===================

    // Поиск по статусу
    List<Notification> findByStatus(NotificationStatus status);

    // Поиск по типу
    List<Notification> findByType(NotificationType type);

    // Поиск по статусу с сортировкой
    List<Notification> findByStatusOrderByCreatedAtAsc(NotificationStatus status);

    // Поиск по типу и статусу
    List<Notification> findByTypeAndStatus(NotificationType type, NotificationStatus status);

    // Поиск для повторной отправки
    @Query("""
            SELECT n FROM Notification n
            WHERE n.status = :status
            ORDER BY n.createdAt ASC
            """)
    List<Notification> findForRetry(NotificationStatus status);

    // Обновление статуса уведомления
    @Modifying
    @Query("""
            UPDATE Notification n
            SET n.status = :status
            WHERE n.id = :id
            """)
    void updateStatus(Long id, NotificationStatus status);

    // =================== Новый метод ===================

    // Поиск уведомлений по мерчанту, типу и диапазону дат
    List<Notification> findAllByMerchantIdAndTypeAndCreatedAtBetween(
            Long merchantId,
            NotificationType type,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Notification> findAllByMerchantIdAndTypeAndStatusAndCreatedAtBetween(Long merchantId, NotificationType notificationType, NotificationStatus notificationStatus, LocalDateTime start, LocalDateTime end);
}
