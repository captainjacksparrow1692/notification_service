package uzumtech.notification.service.sender;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uzumtech.notification.constant.enums.NotificationStatus;
import uzumtech.notification.constant.enums.NotificationType;
import uzumtech.notification.dto.push.NotificationSendRequestDto;
import uzumtech.notification.dto.push.NotificationSendResponseDto;
import uzumtech.notification.dto.push.PushResult;
import uzumtech.notification.dto.ResponseDto;
import uzumtech.notification.entity.Notification;
import uzumtech.notification.mapper.NotificationMapper;
import uzumtech.notification.repository.MerchantRepository;
import uzumtech.notification.repository.NotificationRepository;

/**
 * Логирование вынесено в ServiceLoggingAspect
 * Сервис для отправки push уведомлений
 */
@Service
@RequiredArgsConstructor
public class PushNotificationSender implements NotificationSender {

    private final NotificationRepository notificationRepository;
    private final MerchantRepository merchantRepository;
    private final NotificationMapper notificationMapper;
    private final FirebaseMessaging firebaseMessaging;

    @Override
    @Transactional
    public ResponseDto<NotificationSendResponseDto> sendNotification(NotificationSendRequestDto notificationDto) {
        Notification entity = notificationMapper.toEntity(
            notificationDto, merchantRepository);

        //отправка уведомлений и получение результата
        var result = sendPush(notificationDto);

        entity.setStatus(result.getStatus());
        entity.setMessage(result.getMessage());

        var savedNotification = notificationRepository.save(entity);
        if (result.isSuccess()) {
            return ResponseDto.createSuccessResponse(
                new NotificationSendResponseDto(savedNotification.getId())
            );
        }
        return ResponseDto.createErrorResponse(result.getMessage());
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.PUSH;
    }

    // метод для отправки push уведомлений
    private PushResult sendPush(NotificationSendRequestDto notificationDto) {
        try {
            Message message = Message.builder()
                .setNotification(com.google.firebase.messaging.Notification.builder()
                    .setTitle(notificationDto.getTitle())
                    .setBody(notificationDto.getBody())
                    .build())
                .setToken(notificationDto.getReceiver())
                .build();

            var response = firebaseMessaging.send(message);

            return new PushResult(NotificationStatus.SENT, response, true);
        } catch (FirebaseMessagingException e) {
            return new PushResult(NotificationStatus.FAILED, e.getMessage(), false);
        }
    }
}
