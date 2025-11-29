package uzumtech.notification.exception.notification;

import uzumtech.notification.exception.NotificationException;
import org.springframework.http.HttpStatus;

//Выбрасывается когда получатель (phone/email/firebaseToken) невалиден
//Используется при валидации receiver-info в запросе отправки уведомления
public class InvalidRecipientException extends NotificationException {
    public InvalidRecipientException(String message) {
        super(message,
            "INVALID_RECIPIENT",
            HttpStatus.BAD_REQUEST);
    }

    public InvalidRecipientException(String fieldName, String value) {
        super(String.format("Невалидное значение для %s: %s", fieldName, value),
            "INVALID_RECIPIENT",
            HttpStatus.BAD_REQUEST);
    }
}
