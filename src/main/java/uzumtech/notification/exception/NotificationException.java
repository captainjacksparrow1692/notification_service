package uzumtech.notification.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

//Базовый класс для всех исключений Notification Service
//Содержит код ошибки, HTTP статус и детальное сообщение
@Getter
public class NotificationException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus httpStatus;

    public NotificationException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public NotificationException(String message, String errorCode, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}