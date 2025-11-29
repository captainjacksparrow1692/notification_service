package uzumtech.notification.exception.validation;

import uzumtech.notification.exception.NotificationException;
import org.springframework.http.HttpStatus;

//Выбрасывается когда обязательное поле отсутствует в запросе
//Используется при валидации request body
public class MissingRequiredFieldException extends NotificationException {
    public MissingRequiredFieldException(String fieldName) {
        super(String.format("Обязательное поле '%s' отсутствует", fieldName),
            "MISSING_REQUIRED_FIELD",
            HttpStatus.BAD_REQUEST);
    }
}
