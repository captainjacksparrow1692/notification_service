package uzumtech.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import uzumtech.notification.dto.NotificationSendRequestDto;

/**
 * Сервис для отправки уведомлений в Kafka
 */
@Slf4j
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
        log.info("Отправка уведомления в Kafka: merchantId={}, type={}, receiver={}",
                message.getMerchantId(), message.getType(), message.getReceiver());

        // Используем merchantId как ключ для партиционирования
        String key = String.valueOf(message.getMerchantId());

        kafkaTemplate.send(notificationTopic, key, message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Ошибка при отправке в Kafka: {}", ex.getMessage(), ex);
                    } else {
                        log.info("Уведомление успешно отправлено в Kafka: topic={}, partition={}, offset={}, key={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset(),
                                key);
                    }
                });
    }
}
