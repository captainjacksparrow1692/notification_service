package uzumtech.notification.dto;

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
    private String title;
    private String body;
    private NotificationType type;
    private Long merchantId;
    private String receiver;
}