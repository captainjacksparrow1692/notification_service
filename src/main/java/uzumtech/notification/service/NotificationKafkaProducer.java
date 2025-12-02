package uzumtech.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import uzumtech.notification.dto.NotificationSendRequestDto;

/**
 * Сервис для отправки уведомлений в Kafka
 * Логирование вынесено в KafkaLoggingAspect
 */
@Component
@RequiredArgsConstructor
public class NotificationKafkaProducer {

    private final KafkaTemplate<String, NotificationSendRequestDto> kafkaTemplate;

    @Value("${app.kafka.topic.notification}")
    private String notificationTopic;

    /**
     * Отправить уведомление в Kafka топик
     * Используем merchantId как ключ для гарантии порядка обработки
     */
    public void send(NotificationSendRequestDto message) {
        // Используем merchantId как ключ для партиционирования
        String key = String.valueOf(message.getMerchantId());

        kafkaTemplate.send(notificationTopic, key, message);
    }
}
