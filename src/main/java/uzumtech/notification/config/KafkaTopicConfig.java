package uzumtech.notification.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Конфигурация топиков Kafka
 * Автоматически создает 3 отдельных топика для SMS, Push и Email уведомлений
 * Каждый топик имеет 3 партиции для параллельной обработки
 */
@Configuration
public class KafkaTopicConfig {

    @Value("${app.kafka.topic.sms}")
    private String smsTopic;

    @Value("${app.kafka.topic.push}")
    private String pushTopic;

    @Value("${app.kafka.topic.email}")
    private String emailTopic;

    @Value("${app.kafka.topic.partitions}")
    private Integer partitions;

    @Value("${app.kafka.topic.replication-factor}")
    private Integer replicationFactor;

    /**
     * Создание топика для SMS уведомлений
     * 3 партиции для параллельной обработки по merchantId
     */
    @Bean
    public NewTopic smsTopic() {
        return TopicBuilder
                .name(smsTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .build();
    }

    /**
     * Создание топика для Push уведомлений
     * 3 партиции для параллельной обработки по merchantId
     */
    @Bean
    public NewTopic pushTopic() {
        return TopicBuilder
                .name(pushTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .build();
    }

    /**
     * Создание топика для Email уведомлений
     * 3 партиции для параллельной обработки по merchantId
     */
    @Bean
    public NewTopic emailTopic() {
        return TopicBuilder
                .name(emailTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .build();
    }
}
