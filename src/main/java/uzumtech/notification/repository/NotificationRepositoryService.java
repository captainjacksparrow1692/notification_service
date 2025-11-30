package uuzmtech.notification.service;

import uzumtech.notification.entity.Notification;
import uzumtech.notification.entity.NotificationStatus;
import uzumtech.notification.entity.NotificationType;
import uzumtech.notification.repository.NotificationRepository;
import uzumtech.notification.repository.NotificationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationRepositoryService {

    private final NotificationRepository repository;

    public Page<Notification> findAllPaged(
            NotificationStatus status,
            NotificationType type,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    ) {
        return repository.findAll(
                NotificationSpecification.byFilter(status, type, from, to),
                pageable
        );
    }

    @Transactional
    public Notification save(Notification notification) {
        return repository.save(notification);
    }

    @Transactional
    public void updateStatus(Long id, NotificationStatus status) {
        repository.updateStatus(id, status);
    }

    public Optional<Notification> findById(Long id) {
        return repository.findById(id);
    }

    public List<Notification> findForRetry(NotificationStatus status) {
        return repository.findForRetry(status);
    }
}
