package uzumtech.notification.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import uzumtech.notification.dto.push.NotificationSendRequestDto;
import uzumtech.notification.dto.push.NotificationSendResponseDto;
import uzumtech.notification.dto.ResponseDto;

// Аспект для логирования методов сервисов
// Перенес сюда логирование из PushNotificationSender и NotificationConsumer
@Aspect
@Component
@Slf4j
public class ServiceLoggingAspect {

    // Логирование получения сообщений из Kafka в NotificationConsumer
    @Before(value = "execution(* uzumtech.notification.service.kafka.consumer.NotificationConsumer.consume(..)) && args(notification)", argNames = "notification")
    public void logBeforeKafkaConsume(NotificationSendRequestDto notification) {
        log.info("Получено сообщение из Kafka: merchantId={}, type={}, receiver={}",
                notification.getMerchantId(), notification.getType(), notification.getReceiver());
    }

    // Логирование после отправки уведомлений любым сендером
    @AfterReturning(pointcut = "execution(* uzumtech.notification.service.sender.*NotificationSender.sendNotification(..)) && args(notificationDto)", returning = "result", argNames = "joinPoint,notificationDto,result")
    public void logAfterNotificationSend(JoinPoint joinPoint, NotificationSendRequestDto notificationDto, Object result) {
        if (result instanceof ResponseDto<?> response) {
            if (response.isSuccess() && response.getData() instanceof NotificationSendResponseDto responseDto) {
                // Специфичное логирование для Firebase
                if (joinPoint.getTarget().getClass().getSimpleName().equals("PushNotificationSender")) {
                    log.info("Firebase notification sent to {} device", notificationDto.getReceiver());
                }
                log.info("Уведомление успешно обработано: notificationId={}", responseDto.getNotificationId());
            } else {
                // Логирование ошибок
                if (joinPoint.getTarget().getClass().getSimpleName().equals("PushNotificationSender")) {
                    log.error("Firebase notification multicast sending error: {}", response.getMessage());
                }
                log.warn("Уведомление обработано с ошибкой: {}", response.getMessage());
            }
        }
    }

    // Логирование ошибок при обработке уведомления в NotificationConsumer
    @AfterThrowing(pointcut = "execution(* uzumtech.notification.service.kafka.consumer.NotificationConsumer.consume(..)) && args(notification)", throwing = "error", argNames = "notification,error")
    public void logKafkaConsumeError(NotificationSendRequestDto notification, Throwable error) {
        log.error("Ошибка при обработке уведомления: merchantId={}, receiver={}, error={}",
                notification.getMerchantId(), notification.getReceiver(), error.getMessage(), error);
    }
}
