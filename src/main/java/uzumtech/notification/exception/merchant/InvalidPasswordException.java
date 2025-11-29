package uzumtech.notification.exception.merchant;

import uzumtech.notification.exception.NotificationException;
import org.springframework.http.HttpStatus;

//Выбрасывается когда password мерчанта не прошёл валидацию
public class InvalidPasswordException extends NotificationException {
    public InvalidPasswordException() {
        super("Password не соответствует требованиям безопасности",
            "INVALID_PASSWORD",
            HttpStatus.BAD_REQUEST);
    }
}