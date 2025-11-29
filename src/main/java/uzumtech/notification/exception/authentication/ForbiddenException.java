package uzumtech.notification.exception.authentication;

import uzumtech.notification.exception.NotificationException;
import org.springframework.http.HttpStatus;

//Выбрасывается когда у пользователя нет прав доступа к ресурсу
public class ForbiddenException extends NotificationException {
    public ForbiddenException(String message) {
        super(message, "FORBIDDEN", HttpStatus.FORBIDDEN);
    }
}