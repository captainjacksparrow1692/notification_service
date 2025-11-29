package uzumtech.notification.exception.merchant;

import uzumtech.notification.exception.NotificationException;
import org.springframework.http.HttpStatus;

//Выбрасывается когда мерчант с таким ID не найден в БД
public class MerchantNotFoundException extends NotificationException {
    public MerchantNotFoundException(Long merchantId) {
        super(String.format("Merchant с ID %d не найден", merchantId),
            "MERCHANT_NOT_FOUND",
            HttpStatus.NOT_FOUND);
    }
}