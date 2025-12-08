package uzumtech.notification.dto.webhook;

import lombok.Builder;
import lombok.Getter;
import uzumtech.notification.constant.enums.NotificationStatus;
import uzumtech.notification.constant.enums.NotificationType;

import java.time.LocalDateTime;

// DTO для отправки webhook о статусе уведомления
@Getter
@Builder
public class WebhookPayloadDto {

    // ID уведомления в системе
    private Long notificationId;

    // Тип уведомления (SMS, PUSH, EMAIL)
    private NotificationType type;

    // Текущий статус (SENT, FAILED, DELIVERED и т.п.)
    private NotificationStatus status;

    // Получатель (телефон, email, firebase token)
    private String receiver;

    // Текст сообщения/описание ошибки
    private String message;

    // Когда отправлено
    private LocalDateTime sentAt;

    // Когда доставлено (если применимо)
    private LocalDateTime deliveredAt;

    // Идентификатор сообщения у провайдера
    private String providerMessageId;
}
