package uzumtech.notification.repository;

import uzumtech.notification.entity.Notification;
import uzumtech.notification.entity.NotificationStatus;
import uzumtech.notification.entity.NotificationType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class NotificationSpecification {

    public static Specification<Notification> byFilter(
            NotificationStatus status,
            NotificationType type,
            LocalDateTime from,
            LocalDateTime to
    ) {
        return Specification.where(byStatus(status))
                .and(byType(type))
                .and(createdFrom(from))
                .and(createdTo(to));
    }

    private static Specification<Notification> byStatus(NotificationStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    private static Specification<Notification> byType(NotificationType type) {
        return (root, query, cb) ->
                type == null ? null : cb.equal(root.get("type"), type);
    }

    private static Specification<Notification> createdFrom(LocalDateTime date) {
        return (root, query, cb) ->
                date == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), date);
    }

    private static Specification<Notification> createdTo(LocalDateTime date) {
        return (root, query, cb) ->
                date == null ? null : cb.lessThanOrEqualTo(root.get("createdAt"), date);
    }
}
