package uzumtech.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uzumtech.notification.constant.enums.NotificationStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PushResult {
    private NotificationStatus status;
    private String message;
    boolean success;
}
