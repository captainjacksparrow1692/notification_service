package uzumtech.notification.service.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import uzumtech.notification.constant.enums.NotificationType;
import uzumtech.notification.dto.NotificationSendRequestDto;
import uzumtech.notification.service.sender.NotificationGeneratorFactory;

/**
 * Consumer для обработки EMAIL уведомлений из Kafka
 * Логирование вынесено в ServiceLoggingAspect
 */
@Service
@RequiredArgsConstructor
public class EmailNotificationConsumer {

    private final NotificationGeneratorFactory notificationGeneratorFactory;

    /**
     * Получение и обработка EMAIL уведомлений из email-topic
     * Запускаем 3 параллельных консьюмера для обработки из 3 партиций
     */
    @KafkaListener(
            topics = "${app.kafka.topic.email}",
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "3"
    )
    public void consume(NotificationSendRequestDto notification) {
        // Получаем EMAIL отправитель и отправляем уведомление
        notificationGeneratorFactory
                .getGenerator(NotificationType.EMAIL)
                .sendNotification(notification);
    }
}
