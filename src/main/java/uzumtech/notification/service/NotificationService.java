package uzumtech.notification.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uzumtech.notification.constant.enums.NotificationStatus;
import uzumtech.notification.dto.NotificationSendRequestDto;
import uzumtech.notification.entity.Notification;
import uzumtech.notification.exception.notification.NotificationNotFoundException;
import uzumtech.notification.repository.NotificationRepository;

@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationKafkaProducer kafkaProducer;

    public NotificationService(NotificationRepository repository, NotificationKafkaProducer kafkaProducer) {
        this.repository = repository;
        this.kafkaProducer = kafkaProducer;
    }

    @Transactional
    public Notification queue(Notification notification) {
        if (notification == null) {
            throw new IllegalArgumentException("Notification не может быть null");
        }

        // Ставим статус PENDING
        notification.setStatus(NotificationStatus.PENDING);

        // Сохраняем в базе
        Notification saved = repository.save(notification);

        // Формируем DTO для Kafka
        NotificationSendRequestDto message = NotificationSendRequestDto.builder()
                .type(saved.getType())        // если type — enum
                .title(saved.getTitle())
                .body(saved.getBody())
                .receiver(saved.getRecipient())
                .merchantId(saved.getMerchantId())
                .build();

        // Отправляем в Kafka
        kafkaProducer.send(message);

        return saved;
    }

    @Transactional
    public Notification updateStatus(Long id, NotificationStatus status) {
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + id));

        notification.setStatus(status);
        return repository.save(notification);
    }

    public Notification findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + id));
    }
}
