package uzumtech.notification.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import uzumtech.notification.dto.NotificationSendRequestDto;
import uzumtech.notification.dto.ResponseDto;

@Aspect
@Component
@Slf4j
public class ControllerLoggingAspect {

    // Логируем вход в контроллер перед отправкой уведомления
    @Before(value = "execution(* uzumtech.notification.controller.NotificationController.sendNotification(..)) && args(request)", argNames = "request")
    public void logBeforeSendNotification(NotificationSendRequestDto request) {
        log.info("Received send notification request: merchantId={}, type={}, receiver={}",
                request.getMerchantId(), request.getType(), request.getReceiver());
    }

    // Логируем успешное завершение отправки (постановки в очередь)
    @AfterReturning(pointcut = "execution(* uzumtech.notification.controller.NotificationController.sendNotification(..))",
        returning = "result", argNames = "result")
    public void logAfterSendNotification(Object result) {
        if (result instanceof ResponseDto<?> responseDto && responseDto.getData() instanceof Long id) {
            log.info("Notification queued: id={}, status={}", id, "QUEUED");
        }
    }

    // Логируем запрос на получение уведомления по ID
    @Before(value = "execution(* uzumtech.notification.controller.NotificationController.getNotification(..)) && args(id)", argNames = "id")
    public void logBeforeGetNotification(Long id) {
        log.info("Fetching notification with id={}", id);
    }
}
