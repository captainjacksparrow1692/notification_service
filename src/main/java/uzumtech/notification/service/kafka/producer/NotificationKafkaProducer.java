package uzumtech.notification.service.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import uzumtech.notification.dto.push.NotificationSendRequestDto;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class NotificationKafkaProducer {

    private final KafkaTemplate<String, NotificationSendRequestDto> kafkaTemplate;

    @Value("${app.kafka.topic.notification}")
    private String notificationTopic;

    /**
     * Возвращает CompletableFuture, чтобы можно было обработать успех/ошибку отправки
     */
    public CompletableFuture<SendResult<String, NotificationSendRequestDto>> send(
            NotificationSendRequestDto message) {

        String key = String.valueOf(message.getMerchantId());

        return kafkaTemplate.send(notificationTopic, key, message);
    }
}