package uzumtech.notification.exception.notification;

import uzumtech.notification.exception.NotificationException;
import org.springframework.http.HttpStatus;

//Выбрасывается когда не удалось отправить уведомление
//Может быть вызвано проблемами с внешними сервисами (email/sms провайдер, Firebase)
public class NotificationSendingFailedException extends NotificationException {
    public NotificationSendingFailedException(String message) {
        super(message,
            "NOTIFICATION_SENDING_FAILED",
            HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public NotificationSendingFailedException(String type, String recipient, Throwable cause) {
        super(String.format("Не удалось отправить %s уведомление на %s", type, recipient),
            "NOTIFICATION_SENDING_FAILED",
            HttpStatus.INTERNAL_SERVER_ERROR,
            cause);
    }
}
