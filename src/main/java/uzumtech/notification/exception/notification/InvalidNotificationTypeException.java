package uzumtech.notification.exception.notification;

import uzumtech.notification.exception.NotificationException;
import org.springframework.http.HttpStatus;

//Выбрасывается когда тип уведомления не поддерживается
//Ожидаемые типы: SMS, EMAIL, PUSH
public class InvalidNotificationTypeException extends NotificationException {
    public InvalidNotificationTypeException(String type) {
        super(String.format("Неподдерживаемый тип уведомления: %s. Доступны: SMS, EMAIL, PUSH", type),
            "INVALID_NOTIFICATION_TYPE",
            HttpStatus.BAD_REQUEST);
    }
}
