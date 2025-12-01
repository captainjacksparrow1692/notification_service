package uzumtech.notification.service.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import uzumtech.notification.dto.NotificationSendRequestDto;
import uzumtech.notification.service.sender.NotificationGeneratorFactory;

/**
 * Consumer для обработки уведомлений из Kafka
 */
@Slf4j
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
        log.info("Получено сообщение из Kafka: merchantId={}, type={}, receiver={}",
                notification.getMerchantId(), notification.getType(), notification.getReceiver());

        try {
            // Получаем нужный отправитель (SMS/Email/Push) и отправляем уведомление
            var response = notificationGeneratorFactory
                    .getGenerator(notification.getType())
                    .sendNotification(notification);

            if (response.isSuccess()) {
                log.info("Уведомление успешно обработано: notificationId={}",
                        response.getData().getNotificationId());
            } else {
                log.warn("Уведомление обработано с ошибкой: {}", response.getMessage());
            }

        } catch (Exception e) {
            log.error("Ошибка при обработке уведомления: merchantId={}, receiver={}, error={}",
                    notification.getMerchantId(), notification.getReceiver(), e.getMessage(), e);
        }
    }
}
