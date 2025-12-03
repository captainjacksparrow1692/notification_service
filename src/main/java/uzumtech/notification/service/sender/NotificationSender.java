package uzumtech.notification.service.sender;

import uzumtech.notification.constant.enums.NotificationType;
import uzumtech.notification.dto.NotificationSendRequestDto;
import uzumtech.notification.dto.push.NotificationSendResponseDto;
import uzumtech.notification.dto.ResponseDto;

public interface NotificationSender {
    ResponseDto<NotificationSendResponseDto> sendNotification(NotificationSendRequestDto notification);
    NotificationType getNotificationType();
}