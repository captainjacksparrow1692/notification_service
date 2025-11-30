package uzumtech.notification.service.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import uzumtech.notification.dto.NotificationSendRequestDto;
import uzumtech.notification.service.sender.NotificationGeneratorFactory;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationGeneratorFactory notificationGeneratorFactory;

    @KafkaListener(topics = "${spring.kafka.topic.notification}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(NotificationSendRequestDto notification) {
        log.info("Получено сообщение из Kafka: merchantId={}, type={}, recipient={}",
            notification.getMerchantId(), notification.getType().name(), notification.getReceiver());

        try {
            //TODO: Здесь будет вызов сервиса отправки (SMS/Email/Push)
            var response = notificationGeneratorFactory.getGenerator(notification.getType()).
                sendNotification(notification);

            log.info("Уведомление успешно обработано: notificationId={}", response.getData().getNotificationId());

        } catch (Exception e) {
            log.error("Ошибка при обработке уведомления: merchantId={}, receiver={} error={}",
               notification.getMerchantId(), notification.getReceiver(), e.getMessage());
        }
    }
}
//Получает сообщения из Kafka и обрабатывает их