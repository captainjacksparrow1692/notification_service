package uzumtech.notification.service.sender;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uzumtech.notification.constant.enums.NotificationStatus;
import uzumtech.notification.constant.enums.NotificationType;
import uzumtech.notification.dto.NotificationSendRequestDto;
import uzumtech.notification.dto.ResponseDto;
import uzumtech.notification.dto.push.NotificationSendResponseDto;
import uzumtech.notification.dto.webhook.WebhookPayloadDto;
import uzumtech.notification.entity.Notification;
import uzumtech.notification.mapper.NotificationMapper;
import uzumtech.notification.repository.MerchantRepository;
import uzumtech.notification.repository.NotificationRepository;
import uzumtech.notification.service.webhook.WebhookService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationSender implements NotificationSender {

    private final JavaMailSender mailSender;
    private final NotificationMapper notificationMapper;
    private final MerchantRepository merchantRepository;
    private final NotificationRepository notificationRepository;
    private final WebhookService webhookService;

    @Override
    @Transactional
    public ResponseDto<NotificationSendResponseDto> sendNotification(NotificationSendRequestDto notificationDto) {
        Notification email = notificationMapper.toEntity(notificationDto, merchantRepository);

        if (notificationDto.getIdempotencyKey() != null) {
            var existing = notificationRepository.findByIdempotencyKey(notificationDto.getIdempotencyKey());
            if (existing.isPresent()) {
                if (existing.get().getStatus() == NotificationStatus.SENT) {
                    return ResponseDto.createSuccessResponse(
                        new NotificationSendResponseDto(existing.get().getId())
                    );
                }
                email = existing.get();
            }
        }

        email.setIdempotencyKey(notificationDto.getIdempotencyKey());
        email.setStatus(NotificationStatus.PENDING);
        notificationRepository.save(email);

        try {
            // Отправляем письмо через SMTP
            sendEmail(email.getRecipient(), email.getTitle(), email.getBody());

            email.setStatus(NotificationStatus.SENT);
            email.setMessage("Email sent successfully");
            email.setDeliveredAt(LocalDateTime.now());
            email = notificationRepository.save(email);

            // Уведомляем мерчанта через webhook
            sendWebhookNotification(email);

            return ResponseDto.createSuccessResponse(
                    new NotificationSendResponseDto(email.getId())
            );

        } catch (Exception e) {
            email.setStatus(NotificationStatus.FAILED);
            email.setMessage(e.getMessage());
            notificationRepository.save(email);

            // Отправляем webhook о неуспешной попытке
            sendWebhookNotification(email);

            throw new RuntimeException("Email sending failed: ", e);
        }
    }

    private void sendEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true); // true = HTML content

        mailSender.send(message);
    }

    // Отправляем webhook с результатом
    private void sendWebhookNotification(Notification notification) {
        String webhookUrl = notification.getMerchant().getWebhook();

        WebhookPayloadDto payload = WebhookPayloadDto.builder()
                .notificationId(notification.getId())
                .type(NotificationType.EMAIL)
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
        return NotificationType.EMAIL;
    }
}
