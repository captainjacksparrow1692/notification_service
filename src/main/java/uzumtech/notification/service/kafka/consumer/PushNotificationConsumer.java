package uzumtech.notification.service.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import uzumtech.notification.constant.enums.NotificationType;
import uzumtech.notification.dto.NotificationSendRequestDto;
import uzumtech.notification.service.sender.NotificationGeneratorFactory;

/**
 * Consumer для обработки PUSH уведомлений из Kafka
 * Логирование вынесено в ServiceLoggingAspect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PushNotificationConsumer {

    private final NotificationGeneratorFactory notificationGeneratorFactory;

    // Получение и обработка PUSH уведомлений из push-topic
    // Запускаем 3 параллельных консьюмера для обработки из 3 партиций
    @KafkaListener(
            topics = "${app.kafka.topic.push}",
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "3"
    )
    public void consume(NotificationSendRequestDto notification) {
        try {
            // Получаем PUSH отправитель и отправляем уведомление
            notificationGeneratorFactory
                    .getGenerator(NotificationType.PUSH)
                    .sendNotification(notification);
        } catch (Exception e) {
            log.error("Критическая ошибка обработки PUSH уведомления: merchantId={}, receiver={}, error={}",
                    notification.getMerchantId(), notification.getReceiver(), e.getMessage(), e);
            // Пробрасываем исключение дальше для DLT обработки
            throw e;
        }
    }
}
