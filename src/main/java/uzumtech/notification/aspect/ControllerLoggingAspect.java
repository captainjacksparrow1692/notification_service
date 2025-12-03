package uzumtech.notification.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import uzumtech.notification.dto.push.NotificationSendRequestDto;
import uzumtech.notification.dto.ResponseDto;

// Аспект для логирования всех запросов к контроллерам
// Перенес логирование из контроллеров сюда для чистоты кода
@Aspect
@Component
@Slf4j
public class ControllerLoggingAspect {

    // Логирование перед вызовом метода sendNotification
    @Before(value = "execution(* uzumtech.notification.controller.NotificationController.sendNotification(..)) && args(request)", argNames = "request")
    public void logBeforeSendNotification(NotificationSendRequestDto request) {
        log.info("Получен запрос на отправку уведомления: merchantId={}, type={}, receiver={}",
                request.getMerchantId(), request.getType(), request.getReceiver());
    }

    // Логирование после успешного выполнения sendNotification
    @AfterReturning(pointcut = "execution(* uzumtech.notification.controller.NotificationController.sendNotification(..))", returning = "result", argNames = "result")
    public void logAfterSendNotification(Object result) {
        if (result instanceof ResponseDto<?> responseDto && responseDto.getData() instanceof Long id) {
            log.info("Уведомление добавлено в очередь: id={}, status={}", id, "QUEUED");
        }
    }

    // Логирование перед вызовом метода getNotification
    @Before(value = "execution(* uzumtech.notification.controller.NotificationController.getNotification(..)) && args(id)", argNames = "id")
    public void logBeforeGetNotification(Long id) {
        log.info("Получен запрос на получение уведомления: id={}", id);
    }
}
