package uzumtech.notification.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.ExponentialBackOff;
import uzumtech.notification.dto.NotificationSendRequestDto;

import java.util.HashMap;
import java.util.Map;

// Конфигурация продюсеров/консьюмеров Kafka для работы с уведомлениями
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private Boolean enableAutoCommit;

    @Value("${spring.kafka.producer.acks}")
    private String acks;

    @Value("${spring.kafka.producer.retries}")
    private Integer retries;

    // Фабрика продюсеров для отправки NotificationSendRequestDto
    @Bean
    public ProducerFactory<String, NotificationSendRequestDto> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, acks);
        config.put(ProducerConfig.RETRIES_CONFIG, retries);
        return new DefaultKafkaProducerFactory<>(config);
    }

    // KafkaTemplate для SMS отправок
    @Bean("smsKafkaTemplate")
    public KafkaTemplate<String, NotificationSendRequestDto> smsKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // KafkaTemplate для push отправок
    @Bean("pushKafkaTemplate")
    public KafkaTemplate<String, NotificationSendRequestDto> pushKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // KafkaTemplate для email отправок
    @Bean("emailKafkaTemplate")
    public KafkaTemplate<String, NotificationSendRequestDto> emailKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // Фабрика консьюмеров для чтения NotificationSendRequestDto
    @Bean
    public ConsumerFactory<String, NotificationSendRequestDto> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "uzumtech.notification.dto");
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, NotificationSendRequestDto.class);
        return new DefaultKafkaConsumerFactory<>(config);
    }

    // Контейнер слушателей с обработчиком ошибок
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificationSendRequestDto> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, NotificationSendRequestDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    // ErrorHandler с экспоненциальным бэкоффом и повторными попытками
    @Bean
    public DefaultErrorHandler errorHandler() {
        ExponentialBackOff exponentialBackOff = new ExponentialBackOff(1000, 2);
        exponentialBackOff.setMaxElapsedTime(10000);
        return new DefaultErrorHandler(exponentialBackOff);
    }
}
