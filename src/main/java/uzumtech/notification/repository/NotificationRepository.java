package uzumtech.notification.repository;

import uzumtech.notification.entity.Notification;
import uzumtech.notification.constant.enums.NotificationStatus;
import uzumtech.notification.constant.enums.NotificationType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>,
        JpaSpecificationExecutor<Notification> {

    List<Notification> findByStatus(NotificationStatus status);

    List<Notification> findByType(NotificationType type);

    List<Notification> findByStatusOrderByCreatedAtAsc(NotificationStatus status);

    List<Notification> findByTypeAndStatus(NotificationType type, NotificationStatus status);

    @Query("""
            SELECT n FROM Notification n
            WHERE n.status = :status
            ORDER BY n.createdAt ASC
            """)
    List<Notification> findForRetry(NotificationStatus status);

    @Modifying
    @Query("""
            UPDATE Notification n
            SET n.status = :status
            WHERE n.id = :id
            """)
    void updateStatus(Long id, NotificationStatus status);
}

