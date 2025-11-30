package uzumtech.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import uzumtech.notification.dto.NotificationSendRequestDto;

@Component
@RequiredArgsConstructor
public class NotificationKafkaProducer {

    private final KafkaTemplate<String, NotificationSendRequestDto> kafkaTemplate;
    private static final String TOPIC = "notifications";

    public void send(NotificationSendRequestDto message) {
        kafkaTemplate.send(TOPIC, message)
                .thenAccept(result -> System.out.printf("✅ Notification sent to Kafka: topic=%s offset=%d%n",
                        result.getRecordMetadata().topic(), result.getRecordMetadata().offset()))
                .exceptionally(ex -> {
                    System.err.println("❌ Error sending to Kafka: " + ex.getMessage());
                    return null;
                });
    }
}
