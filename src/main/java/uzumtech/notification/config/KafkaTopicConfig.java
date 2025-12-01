package uzumtech.notification.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Конфигурация топиков Kafka
 * Автоматически создает топик с нужным количеством партиций
 */
@Configuration
public class KafkaTopicConfig {

    @Value("${app.kafka.topic.notification}")
    private String notificationTopic;

    @Value("${app.kafka.topic.partitions}")
    private Integer partitions;

    @Value("${app.kafka.topic.replication-factor}")
    private Integer replicationFactor;

    /**
     * Создание топика для уведомлений с 3 партициями
     * Это гарантирует что 3 консьюмера смогут обрабатывать сообщения параллельно
     */
    @Bean
    public NewTopic notificationTopic() {
        return TopicBuilder
                .name(notificationTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .build();
    }
}
