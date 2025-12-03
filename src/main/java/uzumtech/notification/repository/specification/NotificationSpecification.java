package uzumtech.notification.repository.specification;

import jakarta.persistence.criteria.Predicate;
import uzumtech.notification.entity.Notification;
import uzumtech.notification.constant.enums.NotificationStatus;
import uzumtech.notification.constant.enums.NotificationType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class NotificationSpecification {

    public static Specification<Notification> byFilter(
        NotificationStatus status,
        NotificationType type,
        LocalDateTime from,
        LocalDateTime to
    ) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }

            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), from));
            }

            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), to));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}