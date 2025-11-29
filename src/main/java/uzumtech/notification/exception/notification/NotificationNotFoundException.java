package uzumtech.notification.exception.notification;

import uzumtech.notification.exception.NotificationException;
import org.springframework.http.HttpStatus;

//Выбрасывается когда уведомление с указанным ID не найдено в БД
//Используется в методах получения/обновления уведомления по ID
public class NotificationNotFoundException extends NotificationException {
    public NotificationNotFoundException(Long notificationId) {
        super(String.format("Уведомление с ID %d не найдено", notificationId),
            "NOTIFICATION_NOT_FOUND",
            HttpStatus.NOT_FOUND);
    }
}
