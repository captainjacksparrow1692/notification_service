package uzumtech.notification.service.webhook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import uzumtech.notification.dto.webhook.WebhookPayloadDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {

    private final RestClient.Builder restClientBuilder;

    // Отправка webhook без повторов
    @Async
    public void sendWebhook(String webhookUrl, WebhookPayloadDto payload) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.warn("Webhook URL is empty for notification {}, skipping webhook", payload.getNotificationId());
            return;
        }

        try {
            log.info("Sending webhook to {}: notificationId={}, status={}",
                    webhookUrl, payload.getNotificationId(), payload.getStatus());

            RestClient restClient = restClientBuilder.build();

            String response = restClient.post()
                    .uri(webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(String.class);

            log.info("Webhook delivered successfully to {}: notificationId={}, response={}",
                    webhookUrl, payload.getNotificationId(), response);

        } catch (Exception e) {
            log.error("Failed to send webhook to {}: notificationId={}, error={}",
                    webhookUrl, payload.getNotificationId(), e.getMessage(), e);
        }
    }

    /**
     * Send webhook with retries.
     */
    // Отправка webhook с указанным числом повторов
    @Async
    public void sendWebhookWithRetry(String webhookUrl, WebhookPayloadDto payload, int maxRetries) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.warn("Webhook URL is empty for notification {}, skipping webhook", payload.getNotificationId());
            return;
        }

        int attempt = 0;
        Exception lastException = null;

        while (attempt < maxRetries) {
            attempt++;
            try {
                log.info("Attempt {} to send webhook to {}: notificationId={}",
                        attempt, webhookUrl, payload.getNotificationId());

                RestClient restClient = restClientBuilder.build();

                restClient.post()
                        .uri(webhookUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(payload)
                        .retrieve()
                        .body(String.class);

                log.info("Webhook delivered successfully on attempt {}: notificationId={}",
                        attempt, payload.getNotificationId());
                return;

            } catch (Exception e) {
                lastException = e;
                log.warn("Attempt {} failed: notificationId={}, error={}",
                        attempt, payload.getNotificationId(), e.getMessage());

                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Retry interrupted: notificationId={}", payload.getNotificationId());
                        return;
                    }
                }
            }
        }

        log.error("All {} attempts to send webhook failed: notificationId={}, lastError={}",
                maxRetries, payload.getNotificationId(),
                lastException != null ? lastException.getMessage() : "unknown");
    }
}
