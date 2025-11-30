package uzumtech.notification.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import uzumtech.notification.dto.kafka.NotificationMessage;

//Отправляет сообщения в Kafka топик
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.notification}")
    private String notificationTopic;

    public void sendNotification(NotificationMessage message) {
        log.info("Отправка сообщения в Kafka: notificationId={}, type={}",
                message.getNotificationId(), message.getType());

        kafkaTemplate.send(notificationTopic, message.getNotificationId().toString(), message);

        log.info("Сообщение успешно отправлено в топик: {}", notificationTopic);
    }
}
