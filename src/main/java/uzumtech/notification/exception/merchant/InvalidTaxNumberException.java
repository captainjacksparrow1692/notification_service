package uzumtech.notification.exception.merchant;

import uzumtech.notification.exception.NotificationException;
import org.springframework.http.HttpStatus;

//Выбрасывается когда tax-number мерчанта некорректный или дублируется
public class InvalidTaxNumberException extends NotificationException {
    public InvalidTaxNumberException(String taxNumber) {
        super(String.format("Tax-number '%s' некорректен", taxNumber),
            "INVALID_TAX_NUMBER",
            HttpStatus.BAD_REQUEST);
    }
}