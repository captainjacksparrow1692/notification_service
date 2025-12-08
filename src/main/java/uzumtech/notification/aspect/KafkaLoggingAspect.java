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

@Aspect
@Component
@Slf4j
public class KafkaLoggingAspect {

    // Логируем перед отправкой сообщения в Kafka
    @Before(value = "execution(* uzumtech.notification.service.kafka.producer.NotificationKafkaProducer.send(..)) && args(message)", argNames = "message")
    public void logBeforeKafkaSend(NotificationSendRequestDto message) {
        log.info("Sending notification to Kafka: merchantId={}, type={}, receiver={}, key={}",
            message.getMerchantId(), message.getType(), message.getReceiver(), message.getIdempotencyKey());
    }

    // Логируем результат отправки через KafkaTemplate
    @AfterReturning(pointcut = "execution(* org.springframework.kafka.core.KafkaTemplate.send(..)) " +
        "&& within(uzumtech.notification.service.kafka.producer.NotificationKafkaProducer)", returning = "result", argNames = "joinPoint,result")
    public void logAfterKafkaSend(JoinPoint joinPoint, Object result) {
        if (result instanceof CompletableFuture<?> future) {
            future.whenComplete((res, ex) -> {
                if (ex != null) {
                    log.error("Kafka send failed: {}", ex.getMessage(), ex);
                } else if (res instanceof SendResult<?, ?> sendResult) {
                    log.info("Kafka send success: topic={}, partition={}, offset={}, key={}",
                        sendResult.getRecordMetadata().topic(),
                        sendResult.getRecordMetadata().partition(),
                        sendResult.getRecordMetadata().offset(),
                        joinPoint.getArgs()[1]);
                }
            });
        }
    }
}
