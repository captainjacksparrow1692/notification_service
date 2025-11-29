package uzumtech.notification.exception.authentication;

import uzumtech.notification.exception.NotificationException;
import org.springframework.http.HttpStatus;

//Выбрасывается когда токен авторизации невалидный, истёк или отсутствует
public class UnauthorizedException extends NotificationException {
    public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }
}