package uzumtech.notification.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import uzumtech.notification.dto.NotificationSendRequestDto;
import uzumtech.notification.dto.ResponseDto;
import uzumtech.notification.dto.push.NotificationSendResponseDto;

@Aspect
@Component
@Slf4j
public class ServiceLoggingAspect {

    // Логируем получение сообщения из Kafka до обработки
    @Before(value = "execution(* uzumtech.notification.service.kafka.consumer.*Consumer.consume(..)) && args(notification)", argNames = "notification")
    public void logBeforeKafkaConsume(NotificationSendRequestDto notification) {
        log.info("Consumed message from Kafka: merchantId={}, type={}, receiver={}",
                notification.getMerchantId(), notification.getType(), notification.getReceiver());
    }

    // Логируем результат работы конкретного sender
    @AfterReturning(pointcut = "execution(* uzumtech.notification.service.sender.*NotificationSender.sendNotification(..)) && args(notificationDto)", returning = "result", argNames = "joinPoint,notificationDto,result")
    public void logAfterNotificationSend(JoinPoint joinPoint, NotificationSendRequestDto notificationDto, Object result) {
        if (result instanceof ResponseDto<?> response) {
            if (response.isSuccess() && response.getData() instanceof NotificationSendResponseDto responseDto) {
                if ("PushNotificationSender".equals(joinPoint.getTarget().getClass().getSimpleName())) {
                    log.info("Firebase notification sent to device {}", notificationDto.getReceiver());
                }
                log.info("Notification sent successfully: notificationId={}", responseDto.getNotificationId());
            } else {
                if ("PushNotificationSender".equals(joinPoint.getTarget().getClass().getSimpleName())) {
                    log.error("Firebase notification send error: {}", response.getMessage());
                }
                log.warn("Notification sending finished with status: {}", response.getMessage());
            }
        }
    }

    // Логируем ошибки при обработке сообщений из Kafka
    @AfterThrowing(pointcut = "execution(* uzumtech.notification.service.kafka.consumer.*Consumer.consume(..)) && args(notification)", throwing = "error", argNames = "notification,error")
    public void logKafkaConsumeError(NotificationSendRequestDto notification, Throwable error) {
        log.error("Error while consuming notification: merchantId={}, receiver={}, error={}",
                notification.getMerchantId(), notification.getReceiver(), error.getMessage(), error);
    }
}
