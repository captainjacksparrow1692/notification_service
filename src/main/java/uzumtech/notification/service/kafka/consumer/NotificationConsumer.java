package uzumtech.notification.service.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import uzumtech.notification.dto.NotificationSendRequestDto;
import uzumtech.notification.service.sender.NotificationGeneratorFactory;

/**
 * Consumer для обработки уведомлений из Kafka
 * Логирование вынесено в ServiceLoggingAspect
 */
@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationGeneratorFactory notificationGeneratorFactory;

    /**
     * Получение и обработка уведомлений из Kafka топика
     * Запускаем 3 параллельных консьюмера для обработки из 3 партиций
     */
    @KafkaListener(
            topics = "${app.kafka.topic.notification}",
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "3"
    )
    public void consume(NotificationSendRequestDto notification) {
        // Получаем нужный отправитель (SMS/Email/Push) и отправляем уведомление
        notificationGeneratorFactory
                .getGenerator(notification.getType())
                .sendNotification(notification);
    }
}
