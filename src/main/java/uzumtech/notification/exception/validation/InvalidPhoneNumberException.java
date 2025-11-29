package uzumtech.notification.exception.validation;

import uzumtech.notification.exception.NotificationException;
import org.springframework.http.HttpStatus;

//Выбрасывается когда формат номера телефона невалиден
//Используется при валидации phone в receiver-info для SMS уведомлений
public class InvalidPhoneNumberException extends NotificationException {
    public InvalidPhoneNumberException(String phone) {
        super(String.format("Невалидный формат номера телефона: %s", phone),
            "INVALID_PHONE_NUMBER",
            HttpStatus.BAD_REQUEST);
    }
}
