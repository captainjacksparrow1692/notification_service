package uzumtech.notification.exception.merchant;

import uzumtech.notification.exception.NotificationException;
import org.springframework.http.HttpStatus;

//Выбрасывается когда регистрируется мерчант с уже существующим email
public class MerchantAlreadyExistsException extends NotificationException {
    public MerchantAlreadyExistsException(String email) {
        super(String.format("Merchant с email '%s' уже зарегистрирован", email),
            "MERCHANT_ALREADY_EXISTS",
            HttpStatus.CONFLICT);
    }
}