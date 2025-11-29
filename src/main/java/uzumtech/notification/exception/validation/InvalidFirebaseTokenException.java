package uzumtech.notification.exception.validation;

import uzumtech.notification.exception.NotificationException;
import org.springframework.http.HttpStatus;

//Выбрасывается когда Firebase токен невалиден или истек
//Используется при валидации firebaseToken для PUSH уведомлений
public class InvalidFirebaseTokenException extends NotificationException {
    public InvalidFirebaseTokenException(String message) {
        super(message,
            "INVALID_FIREBASE_TOKEN",
            HttpStatus.BAD_REQUEST);
    }

    public InvalidFirebaseTokenException() {
        super("Firebase токен невалиден или отсутствует",
            "INVALID_FIREBASE_TOKEN",
            HttpStatus.BAD_REQUEST);
    }
}
