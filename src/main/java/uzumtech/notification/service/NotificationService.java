package uzumtech.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uzumtech.notification.constant.enums.NotificationStatus;
import uzumtech.notification.dto.NotificationSendRequestDto;
import uzumtech.notification.entity.Notification;
import uzumtech.notification.entity.Price;
import uzumtech.notification.exception.notification.NotificationNotFoundException;
import uzumtech.notification.repository.NotificationRepository;
import uzumtech.notification.service.kafka.producer.NotificationKafkaProducer;

/**
 * Сервис для обработки уведомлений и отправки событий в Kafka + бизнес-логика цены
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationKafkaProducer kafkaProducer;
    private final PriceService priceService; // добавили бизнес-логику прайса

    /**
     * Отправить уведомление — сохраняет в БД, проставляет цену и публикует событие в Kafka.
     */
    @Transactional
    public Notification send(Notification notification) {

        if (notification == null) {
            throw new IllegalArgumentException("Notification не может быть null");
        }
        if (notification.getRecipient() == null || notification.getRecipient().isBlank()) {
            throw new IllegalArgumentException("Recipient не может быть пустым");
        }

        // ============================
        //    БИЗНЕС-ЛОГИКА PRICE
        // ============================
        Price price = priceService.getActivePrice();   // например 85 сум

        notification.setPrice((price.getPrice()));       // сохраняем цену в уведомление

        // Устанавливаем статус
        notification.setStatus(NotificationStatus.QUEUED);

        // Сохраняем в БД
        Notification saved = repository.save(notification);

        // DTO для Kafka
        NotificationSendRequestDto message = NotificationSendRequestDto.builder()
                .type(saved.getType())
                .title(saved.getTitle())
                .body(saved.getBody())
                .receiver(saved.getRecipient())
                .merchantId(saved.getMerchantId())
                .price(saved.getPrice()) // цена уходит в Kafka-сообщение
                .build();

        // Асинхронная отправка
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
     * Отдельные транзакции для обновления статуса после отправки Kafka
     */
    @Transactional
    public void markAsSent(Long notificationId) {
        updateStatus(notificationId, NotificationStatus.SENT);
    }

    @Transactional
    public void markAsFailed(Long notificationId, Throwable ex) {
        updateStatus(notificationId, NotificationStatus.FAILED);
    }

    /**
     * Обновить статус уведомления
     */
    @Transactional
    public Notification updateStatus(Long id, NotificationStatus status) {
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + id));

        notification.setStatus(status);
        return repository.save(notification);
    }

    /**
     * Получить уведомление по ID
     */
    public Notification findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + id));
    }
}
