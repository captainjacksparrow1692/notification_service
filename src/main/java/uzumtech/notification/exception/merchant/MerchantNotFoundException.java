package uzumtech.notification.exception.merchant;

import uzumtech.notification.exception.NotificationException;
import org.springframework.http.HttpStatus;

// Выбрасывается когда мерчант не найден в БД
public class MerchantNotFoundException extends NotificationException {

    public MerchantNotFoundException(Long merchantId) {
        super(String.format("Merchant with id %d not found", merchantId),
            "MERCHANT_NOT_FOUND",
            HttpStatus.NOT_FOUND);
    }

    public MerchantNotFoundException(String message) {
        super(message, "MERCHANT_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}