package uzumtech.notification.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import uzumtech.notification.dto.kafka.NotificationMessage;

//Получает сообщения из Kafka и обрабатывает их
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    @KafkaListener(topics = "${spring.kafka.topic.notification}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(NotificationMessage message) {
        log.info("Получено сообщение из Kafka: notificationId={}, type={}, recipient={}",
                message.getNotificationId(), message.getType(), message.getRecipient());

        try {
            //TODO: Здесь будет вызов сервиса отправки (SMS/Email/Push)
            //notificationSenderService.send(message);

            log.info("Уведомление успешно обработано: notificationId={}", message.getNotificationId());

        } catch (Exception e) {
            log.error("Ошибка при обработке уведомления: notificationId={}, error={}",
                    message.getNotificationId(), e.getMessage());
        }
    }
}
