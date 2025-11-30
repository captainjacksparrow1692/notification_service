package uzumtech.notification.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Сообщение для отправки в Kafka очередь
//Содержит всю информацию для отправки уведомления
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private Long notificationId;
    private String type;           // SMS, EMAIL, PUSH
    private String text;
    private String recipient;      // phone/email/firebaseToken
    private Long merchantId;
}
