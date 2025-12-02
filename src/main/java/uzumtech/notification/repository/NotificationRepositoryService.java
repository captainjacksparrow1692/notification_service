package uzumtech.notification.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uzumtech.notification.constant.enums.NotificationStatus;
import uzumtech.notification.constant.enums.NotificationType;
import uzumtech.notification.entity.Notification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationRepositoryService {

    private final NotificationRepository repository;

    /**
     * Получить уведомления с фильтром по статусу, типу и диапазону дат
     * с постраничной пагинацией
     */
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

    /**
     * Сохранить новое уведомление
     */
    @Transactional
    public Notification save(Notification notification) {
        return repository.save(notification);
    }

    /**
     * Обновить статус уведомления по ID
     */
    @Transactional
    public boolean updateStatus(Long id, NotificationStatus status) {
        if (repository.existsById(id)) {
            repository.updateStatus(id, status);
            return true;
        }
        return false;
    }

    /**
     * Найти уведомление по ID
     */
    public Optional<Notification> findById(Long id) {
        return repository.findById(id);
    }

    /**
     * Получить уведомления для повторной отправки
     */
    public List<Notification> findForRetry(NotificationStatus status) {
        return repository.findForRetry(status);
    }

    /**
     * Получить все SMS уведомления мерчанта за определённый период
     */
    public List<Notification> findAllSmsByMerchantAndPeriod(
            Long merchantId,
            LocalDateTime start,
            LocalDateTime end
    ) {
        return repository.findAllByMerchantIdAndTypeAndCreatedAtBetween(
                merchantId,
                NotificationType.SMS,
                start,
                end
        );
    }
}
