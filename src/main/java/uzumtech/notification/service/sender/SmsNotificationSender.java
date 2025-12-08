package uzumtech.notification.service.sender;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import uzumtech.notification.constant.enums.NotificationStatus;
import uzumtech.notification.constant.enums.NotificationType;
import uzumtech.notification.dto.NotificationSendRequestDto;
import uzumtech.notification.dto.ResponseDto;
import uzumtech.notification.dto.push.NotificationSendResponseDto;
import uzumtech.notification.dto.sms.SendSmsResponse;
import uzumtech.notification.dto.webhook.WebhookPayloadDto;
import uzumtech.notification.entity.Notification;
import uzumtech.notification.mapper.NotificationMapper;
import uzumtech.notification.repository.MerchantRepository;
import uzumtech.notification.repository.NotificationRepository;
import uzumtech.notification.service.webhook.WebhookService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SmsNotificationSender implements NotificationSender {

    private final RestClient eskizRestClient;
    private final NotificationMapper notificationMapper;
    private final MerchantRepository merchantRepository;
    private final NotificationRepository notificationRepository;
    private final WebhookService webhookService;

    @Value("${app.sms.token}")
    private String smsToken;

    @Value("${app.sms.sender-number}")
    private String smsSenderNumber;

    @Override
    public ResponseDto<NotificationSendResponseDto> sendNotification(NotificationSendRequestDto notification) {
        Notification sms = notificationMapper.toEntity(notification, merchantRepository);

        if (notification.getIdempotencyKey() != null) {
            var existing = notificationRepository.findByIdempotencyKey(notification.getIdempotencyKey());
            if (existing.isPresent()) {
                if (existing.get().getStatus() == NotificationStatus.SENT) {
                    return ResponseDto.createSuccessResponse(
                        new NotificationSendResponseDto(existing.get().getId())
                    );
                }
                sms = existing.get();
            }
        }

        sms.setIdempotencyKey(notification.getIdempotencyKey());
        sms.setStatus(NotificationStatus.PENDING);
        notificationRepository.save(sms);

        // Отправляем SMS и фиксируем результат
        var result = sendSms(sms.getRecipient(), sms.getBody());
        try {
            if ("waiting".equals(result.status())) {
                sms.setStatus(NotificationStatus.SENT);
                sms.setMessage(result.message());
                sms.setDeliveredAt(LocalDateTime.now());
            } else {
                sms.setStatus(NotificationStatus.FAILED);
                sms.setMessage(result.message());
            }
            sms = notificationRepository.save(sms);

            sendWebhookNotification(sms);

            return ResponseDto.createSuccessResponse(
                new NotificationSendResponseDto(sms.getId())
            );
        } catch (Exception e) {
            sms.setStatus(NotificationStatus.FAILED);
            sms.setMessage(e.getMessage());
            notificationRepository.save(sms);

            // Отправляем webhook о неуспешной отправке
            sendWebhookNotification(sms);

            throw new RuntimeException("Sms sent failed: ", e);
        }
    }

    // Отправляем webhook с результатом отправки SMS
    private void sendWebhookNotification(Notification notification) {
        String webhookUrl = notification.getMerchant().getWebhook();

        WebhookPayloadDto payload = WebhookPayloadDto.builder()
                .notificationId(notification.getId())
                .type(NotificationType.SMS)
                .status(notification.getStatus())
                .receiver(notification.getRecipient())
                .message(notification.getMessage())
                .sentAt(notification.getCreatedAt())
                .deliveredAt(notification.getDeliveredAt())
                .providerMessageId(notification.getMessage())
                .build();

        webhookService.sendWebhookWithRetry(webhookUrl, payload, 3);
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.SMS;
    }

    // Вызов Eskiz API для отправки SMS
    private SendSmsResponse sendSms(String phone, String text) {
        var body = new LinkedMultiValueMap<String, Object>();
        body.add("mobile_phone", phone);
        body.add("message", text);
        body.add("from", smsSenderNumber);
        body.add("callback_url", "");

        return eskizRestClient.post()
            .uri("message/sms/send")
            .header("Authorization", "Bearer " + smsToken)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(body)
            .retrieve()
            .toEntity(SendSmsResponse.class).getBody();
    }
}
