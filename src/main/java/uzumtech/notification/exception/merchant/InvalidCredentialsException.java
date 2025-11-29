package uzumtech.notification.exception.merchant;

import uzumtech.notification.exception.NotificationException;
import org.springframework.http.HttpStatus;

//Выбрасывается когда login или password неверные при авторизации
public class InvalidCredentialsException extends NotificationException {
    public InvalidCredentialsException() {
        super("Login или password неверные",
            "INVALID_CREDENTIALS",
            HttpStatus.UNAUTHORIZED);
    }
}