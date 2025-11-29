package uzumtech.notification.exception.merchant;

import uzumtech.notification.exception.NotificationException;
import org.springframework.http.HttpStatus;

//Выбрасывается когда webhook мерчанта некорректный или недоступный
public class InvalidWebhookException extends NotificationException {
    public InvalidWebhookException(String webhook) {
        super(String.format("Webhook '%s' некорректен или недоступен", webhook),
            "INVALID_WEBHOOK",
            HttpStatus.BAD_REQUEST);
    }
}