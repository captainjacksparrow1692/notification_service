package uzumtech.notification.service.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import uzumtech.notification.dto.NotificationSendRequestDto;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class NotificationKafkaProducer {

    @Qualifier("smsKafkaTemplate")
    private final KafkaTemplate<String, NotificationSendRequestDto> smsKafkaTemplate;

    @Qualifier("pushKafkaTemplate")
    private final KafkaTemplate<String, NotificationSendRequestDto> pushKafkaTemplate;

    @Qualifier("emailKafkaTemplate")
    private final KafkaTemplate<String, NotificationSendRequestDto> emailKafkaTemplate;

    @Value("${app.kafka.topic.sms}")
    private String smsTopic;

    @Value("${app.kafka.topic.push}")
    private String pushTopic;

    @Value("${app.kafka.topic.email}")
    private String emailTopic;

    /**
     * Возвращает CompletableFuture, чтобы можно было обработать успех/ошибку отправки
     * Выбирает топик и KafkaTemplate в зависимости от типа уведомления
     * Автоматически генерирует idempotencyKey если его нет
     */
    public CompletableFuture<SendResult<String, NotificationSendRequestDto>> send(
            NotificationSendRequestDto message) {

        // Генерируем idempotency key если его нет (для обеспечения идемпотентности)
        if (message.getIdempotencyKey() == null || message.getIdempotencyKey().isBlank()) {
            message.setIdempotencyKey(UUID.randomUUID().toString());
        }

        String key = String.valueOf(message.getMerchantId());

        // Выбираем топик и template в зависимости от типа уведомления
        return switch (message.getType()) {
            case SMS -> smsKafkaTemplate.send(smsTopic, key, message);
            case PUSH -> pushKafkaTemplate.send(pushTopic, key, message);
            case EMAIL -> emailKafkaTemplate.send(emailTopic, key, message);
        };
    }
}