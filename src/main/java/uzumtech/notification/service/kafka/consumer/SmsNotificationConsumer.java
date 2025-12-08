package uzumtech.notification.service.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import uzumtech.notification.constant.enums.NotificationType;
import uzumtech.notification.dto.NotificationSendRequestDto;
import uzumtech.notification.service.sender.NotificationGeneratorFactory;

/**
 * Consumer для обработки SMS уведомлений из Kafka
 * Логирование вынесено в ServiceLoggingAspect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SmsNotificationConsumer {

    private final NotificationGeneratorFactory notificationGeneratorFactory;

    // Получение и обработка SMS уведомлений из sms-topic
    // Запускаем 3 параллельных консьюмера для обработки из 3 партиций
    @KafkaListener(
            topics = "${app.kafka.topic.sms}",
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "3"
    )
    public void consume(NotificationSendRequestDto notification) {
        try {
            // Получаем SMS отправитель и отправляем уведомление
            notificationGeneratorFactory
                    .getGenerator(NotificationType.SMS)
                    .sendNotification(notification);
        } catch (Exception e) {
            log.error("Критическая ошибка обработки SMS уведомления: merchantId={}, receiver={}, error={}",
                    notification.getMerchantId(), notification.getReceiver(), e.getMessage(), e);
            // Пробрасываем исключение дальше для DLT обработки
            throw e;
        }
    }
}
