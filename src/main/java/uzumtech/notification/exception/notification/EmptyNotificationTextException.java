package uzumtech.notification.exception.notification;

import uzumtech.notification.exception.NotificationException;
import org.springframework.http.HttpStatus;

//Выбрасывается когда текст уведомления пустой или null
//Используется при валидации поля "text" в запросе отправки
public class EmptyNotificationTextException extends NotificationException {
    public EmptyNotificationTextException() {
        super("Текст уведомления не может быть пустым",
            "EMPTY_NOTIFICATION_TEXT",
            HttpStatus.BAD_REQUEST);
    }
}
