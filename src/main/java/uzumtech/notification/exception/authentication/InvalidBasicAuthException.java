package uzumtech.notification.exception.authentication;

import uzumtech.notification.exception.NotificationException;
import org.springframework.http.HttpStatus;

//Выбрасывается когда Basic авторизация некорректна
public class InvalidBasicAuthException extends NotificationException {
    public InvalidBasicAuthException(String message) {
        super(message, "INVALID_BASIC_AUTH", HttpStatus.UNAUTHORIZED);
    }
}