package uzumtech.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uzumtech.notification.constant.enums.NotificationStatus;
import uzumtech.notification.constant.enums.NotificationType;
import uzumtech.notification.dto.NotificationSendRequestDto;
import uzumtech.notification.entity.Notification;
import uzumtech.notification.entity.Price;
import uzumtech.notification.exception.notification.NotificationNotFoundException;
import uzumtech.notification.mapper.NotificationMapper;
import uzumtech.notification.repository.MerchantRepository;
import uzumtech.notification.repository.NotificationRepository;
import uzumtech.notification.service.kafka.producer.NotificationKafkaProducer;
import uzumtech.notification.service.price.PriceService;

import java.util.UUID;

/**
 * Handles notification creation, pricing, status management and Kafka dispatch.
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationKafkaProducer kafkaProducer;
    private final PriceService priceService;
    private final NotificationMapper notificationMapper;
    private final MerchantRepository merchantRepository;

    /**
     * Build Notification from DTO and queue it.
     */
    // Формируем сущность из DTO и передаём на отправку
    @Transactional
    public Notification sendFromDto(NotificationSendRequestDto dto) {
        Notification notification = notificationMapper.toEntity(dto, merchantRepository);
        return send(notification);
    }

    /**
     * Persist notification, calculate price, and send to Kafka.
     */
    // Сохраняем уведомление, считаем цену и отправляем в Kafka
    @Transactional
    public Notification send(Notification notification) {
        if (notification == null) {
            throw new IllegalArgumentException("Notification must not be null");
        }
        if (notification.getRecipient() == null || notification.getRecipient().isBlank()) {
            throw new IllegalArgumentException("Recipient must not be blank");
        }

        if (notification.getIdempotencyKey() != null && !notification.getIdempotencyKey().isBlank()) {
            return repository.findByIdempotencyKey(notification.getIdempotencyKey())
                .orElseGet(() -> processAndSend(notification));
        }

        notification.setIdempotencyKey(UUID.randomUUID().toString());
        return processAndSend(notification);
    }

    // Общая логика подготовки, сохранения и публикации
    private Notification processAndSend(Notification notification) {
        Long priceValue;

        if (notification.getType() == NotificationType.SMS) {
            Price price = priceService.getActivePrice();
            priceValue = price.getPrice();
        } else {
            priceValue = 0L; // EMAIL and PUSH are free here
        }

        notification.setPrice(priceValue);
        notification.setStatus(NotificationStatus.QUEUED);

        Notification saved = repository.save(notification);

        NotificationSendRequestDto message = NotificationSendRequestDto.builder()
                .type(saved.getType())
                .title(saved.getTitle())
                .body(saved.getBody())
                .receiver(saved.getRecipient())
                .merchantId(saved.getMerchantId())
                .idempotencyKey(saved.getIdempotencyKey())
                .build();

        kafkaProducer.send(message)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        markAsSent(saved.getId());
                    } else {
                        markAsFailed(saved.getId(), ex);
                    }
                });

        return saved;
    }

    /**
     * Mark notification as sent after successful Kafka dispatch.
     */
    // Отмечаем как отправленное после удачной публикации
    @Transactional
    public void markAsSent(Long notificationId) {
        updateStatus(notificationId, NotificationStatus.SENT);
    }

    // Отмечаем как неуспешное при ошибке публикации
    @Transactional
    public void markAsFailed(Long notificationId, Throwable ex) {
        updateStatus(notificationId, NotificationStatus.FAILED);
    }

    /**
     * Update notification status.
     */
    // Обновляем статус уведомления
    @Transactional
    public Notification updateStatus(Long id, NotificationStatus status) {
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + id));

        notification.setStatus(status);
        return repository.save(notification);
    }

    /**
     * Find notification by id.
     */
    // Находим уведомление по id или бросаем исключение
    public Notification findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + id));
    }
}
