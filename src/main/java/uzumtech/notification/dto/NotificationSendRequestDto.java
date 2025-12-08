package uzumtech.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uzumtech.notification.constant.enums.NotificationType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class NotificationSendRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Body is required")
    private String body;

    @NotNull(message = "Type is required")
    private NotificationType type;

    @NotNull(message = "MerchantId is required")
    private Long merchantId;

    @NotBlank(message = "Receiver is required")
    private String receiver;

    // Idempotency key для предотвращения дублирования
    // Генерируется на стороне клиента или в Producer
    private String idempotencyKey;
}