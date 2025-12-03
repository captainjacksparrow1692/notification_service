package uzumtech.notification.serviceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import uzumtech.notification.constant.enums.NotificationStatus;
import uzumtech.notification.constant.enums.NotificationType;
import uzumtech.notification.dto.NotificationSendRequestDto;
import uzumtech.notification.entity.Notification;
import uzumtech.notification.entity.Price;
import uzumtech.notification.exception.notification.NotificationNotFoundException;
import uzumtech.notification.repository.NotificationRepository;
import uzumtech.notification.service.NotificationService;
import uzumtech.notification.service.kafka.producer.NotificationKafkaProducer;
import uzumtech.notification.service.price.PriceService;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class NotificationServiceIntegrationTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    private NotificationKafkaProducer kafkaProducer;

    private PriceService priceService;

    private Notification testNotification;

    @BeforeEach
    void setup() {
        testNotification = new Notification();
        testNotification.setRecipient("merchant1");
        testNotification.setBody("Test message");
        testNotification.setTitle("Test Title");
        testNotification.setType(NotificationType.EMAIL);
    }

    @Test
    void testSendNotification_Email() {
        when(kafkaProducer.send(any(NotificationSendRequestDto.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        Notification saved = notificationService.send(testNotification);

        assertNotNull(saved.getId());
        assertEquals(NotificationStatus.QUEUED, saved.getStatus());
        assertEquals(0L, saved.getPrice()); // EMAIL бесплатный

        // Проверяем, что Kafka send вызван
        verify(kafkaProducer, times(1)).send(any(NotificationSendRequestDto.class));
    }

    @Test
    void testSendNotification_SMS() {
        testNotification.setType(NotificationType.SMS);
        Price price = new Price();
        price.setPrice(500L);
        when(priceService.getActivePrice()).thenReturn(price);
        when(kafkaProducer.send(any(NotificationSendRequestDto.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        Notification saved = notificationService.send(testNotification);

        assertNotNull(saved.getId());
        assertEquals(NotificationStatus.QUEUED, saved.getStatus());
        assertEquals(500L, saved.getPrice());

        verify(kafkaProducer, times(1)).send(any(NotificationSendRequestDto.class));
    }

    @Test
    void testMarkAsSentAndFailed() {
        when(kafkaProducer.send(any(NotificationSendRequestDto.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        Notification saved = notificationService.send(testNotification);

        notificationService.markAsSent(saved.getId());
        Notification updated = notificationService.findById(saved.getId());
        assertEquals(NotificationStatus.SENT, updated.getStatus());

        notificationService.markAsFailed(saved.getId(), new RuntimeException("Test"));
        updated = notificationService.findById(saved.getId());
        assertEquals(NotificationStatus.FAILED, updated.getStatus());
    }

    @Test
    void testFindById_NotFound() {
        assertThrows(NotificationNotFoundException.class, () ->
                notificationService.findById(999L)
        );
    }

    @Test
    void testSendNotification_NullRecipient() {
        testNotification.setRecipient(null);
        assertThrows(IllegalArgumentException.class, () ->
                notificationService.send(testNotification)
        );
    }
}

