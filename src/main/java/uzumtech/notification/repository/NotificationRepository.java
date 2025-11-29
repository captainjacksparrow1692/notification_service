package uzumtech.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uzumtech.notification.entity.Notification;
import uzumtech.notification.constant.enums.NotificationStatus;
import uzumtech.notification.constant.enums.NotificationType;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Найти все уведомления по статусу (например, FAILED)
    List<Notification> findByStatus(NotificationStatus status);

    // Найти уведомления по типу: EMAIL, SMS, PUSH
    List<Notification> findByType(NotificationType type);

    // Найти все PENDING (например для повторной отправки)
    List<Notification> findByStatusOrderByCreatedAtAsc(NotificationStatus status);

    // Найти только сообщения определённого типа и статуса
    List<Notification> findByTypeAndStatus(NotificationType type, NotificationStatus status);

}

