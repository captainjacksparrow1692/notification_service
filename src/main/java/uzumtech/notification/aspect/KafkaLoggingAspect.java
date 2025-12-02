package uzumtech.notification.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import uzumtech.notification.dto.NotificationSendRequestDto;

import java.util.concurrent.CompletableFuture;

/**
 * Аспект для логирования Kafka операций
 * Перенес сюда логирование из NotificationKafkaProducer
 */
@Aspect
@Component
@Slf4j
public class KafkaLoggingAspect {

    // Логирование перед отправкой в Kafka
    @Before(value = "execution(* uzumtech.notification.service.kafka.producer.NotificationKafkaProducer.send(..)) && args(message)", argNames = "message")
    public void logBeforeKafkaSend(NotificationSendRequestDto message) {
        log.info("Отправка уведомления в Kafka: merchantId={}, type={}, receiver={}",
                message.getMerchantId(), message.getType(), message.getReceiver());
    }

    // Логирование после отправки в Kafka
    @AfterReturning(pointcut = "execution(* org.springframework.kafka.core.KafkaTemplate.send(..)) && within(uzumtech.notification.service.kafka.producer.NotificationKafkaProducer)", returning = "result", argNames = "joinPoint,result")
    public void logAfterKafkaSend(JoinPoint joinPoint, Object result) {
        if (result instanceof CompletableFuture<?> future) {
            // Добавляем обработчик успеха/ошибки
            future.whenComplete((res, ex) -> {
                if (ex != null) {
                    log.error("Ошибка при отправке в Kafka: {}", ex.getMessage(), ex);
                } else if (res instanceof SendResult<?, ?> sendResult) {
                    log.info("Уведомление успешно отправлено в Kafka: topic={}, partition={}, offset={}, key={}",
                            sendResult.getRecordMetadata().topic(),
                            sendResult.getRecordMetadata().partition(),
                            sendResult.getRecordMetadata().offset(),
                            joinPoint.getArgs()[1]); // key параметр
                }
            });
        }
    }
}
