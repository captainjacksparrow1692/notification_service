package uzumtech.notification.exception.notification;

import uzumtech.notification.exception.NotificationException;
import org.springframework.http.HttpStatus;

// Выбрасывается когда уведомление не найдено в БД
public class NotificationNotFoundException extends NotificationException {

    public NotificationNotFoundException(Long notificationId) {
        super(String.format("Notification with id %d not found", notificationId),
            "NOTIFICATION_NOT_FOUND",
            HttpStatus.NOT_FOUND);
    }

    public NotificationNotFoundException(String message) {
        super(message, "NOTIFICATION_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
