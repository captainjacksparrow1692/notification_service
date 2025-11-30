package uzumtech.notification.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import uzumtech.notification.dto.NotificationSendRequestDto;

//Отправляет сообщения в Kafka топик
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.notification}")
    private String notificationTopic;

    public void sendNotification(NotificationSendRequestDto notification) {
        log.info("Отправка сообщения в Kafka: merchantId={}, type={}",
                notification.getMerchantId(), notification.getType());

        kafkaTemplate.send(notificationTopic, notification.getMerchantId().toString(), notification);

        log.info("Сообщение успешно отправлено в топик: {}", notificationTopic);
    }
}
