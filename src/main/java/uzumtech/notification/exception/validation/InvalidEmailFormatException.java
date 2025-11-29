package uzumtech.notification.exception.validation;

import uzumtech.notification.exception.NotificationException;
import org.springframework.http.HttpStatus;

//Выбрасывается когда формат email адреса невалиден
//Используется при валидации email в различных запросах
public class InvalidEmailFormatException extends NotificationException {
    public InvalidEmailFormatException(String email) {
        super(String.format("Невалидный формат email: %s", email),
            "INVALID_EMAIL_FORMAT",
            HttpStatus.BAD_REQUEST);
    }
}
