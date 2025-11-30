package uzumtech.notification.service.sender;

import org.springframework.stereotype.Component;
import uzumtech.notification.constant.enums.NotificationType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class NotificationGeneratorFactory {
    private final Map<NotificationType, NotificationSender> senders;

    public NotificationGeneratorFactory(List<NotificationSender> senders) {
        this.senders = senders.stream()
            .collect(Collectors.toMap(
                NotificationSender::getNotificationType,
                sender -> sender
            ));
    }

    public NotificationSender getGenerator(NotificationType notificationType) {
        var sender = senders.get(notificationType);
        if (sender == null) {
            throw new IllegalArgumentException("No generator found for " + notificationType);
        }
        return sender;
    }
}