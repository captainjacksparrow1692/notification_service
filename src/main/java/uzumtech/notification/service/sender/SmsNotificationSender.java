package uzumtech.notification.service.sender;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import uzumtech.notification.constant.enums.NotificationStatus;
import uzumtech.notification.constant.enums.NotificationType;
import uzumtech.notification.dto.ResponseDto;
import uzumtech.notification.dto.push.NotificationSendRequestDto;
import uzumtech.notification.dto.push.NotificationSendResponseDto;
import uzumtech.notification.dto.sms.SendSmsResponse;
import uzumtech.notification.entity.Notification;
import uzumtech.notification.mapper.NotificationMapper;
import uzumtech.notification.repository.MerchantRepository;
import uzumtech.notification.repository.NotificationRepository;

import java.time.LocalDateTime;

// Сервис для отправки смс уведомлений
@Service
@RequiredArgsConstructor
public class SmsNotificationSender implements NotificationSender {

    private final RestClient eskizRestClient;
    private final NotificationMapper notificationMapper;
    private final MerchantRepository merchantRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public ResponseDto<NotificationSendResponseDto> sendNotification(NotificationSendRequestDto notification) {
        Notification sms = notificationMapper.toEntity(
            notification, merchantRepository);
        // сохранение в статусе 'ожидание'
        sms.setStatus(NotificationStatus.PENDING);
        notificationRepository.save(sms);
        // отправка смс
        var result = sendSms(sms.getRecipient(), sms.getBody());
        try {
            if ("waiting".equals(result.status())) {
                sms.setStatus(NotificationStatus.SENT);
                sms.setMessage(result.message());
                sms.setDeliveredAt(LocalDateTime.now());
            } else {
                sms.setStatus(NotificationStatus.FAILED);
            }
            sms = notificationRepository.save(sms);
            return ResponseDto.createSuccessResponse(
                new NotificationSendResponseDto(sms.getId())
            );
        } catch (Exception e) {
            // сохранение ошибки для этого смс уведомления
            sms.setStatus(NotificationStatus.FAILED);
            sms.setMessage(e.getMessage());
            throw new RuntimeException("Sms sent failed: ", e);
        }
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.SMS;
    }
    /**
     * Отправка смс уведомления на определенный номер с текстом
     */
    private SendSmsResponse sendSms(String phone, String text) {
        var body = new LinkedMultiValueMap<String, Object>();
        body.add("mobile_phone", phone);
        body.add("message", text);
        body.add("from", "`number`");
        body.add("callback_url", "");

        return eskizRestClient.post()
            .uri("message/sms/send")
            .header("Authorization", "Bearer `token`")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(body)
            .retrieve()
            .toEntity(SendSmsResponse.class).getBody();
    }
}
