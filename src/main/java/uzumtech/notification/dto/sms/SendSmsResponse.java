package uzumtech.notification.dto.sms;


public record SendSmsResponse(
        String id,
        String message,
        String status
) {

}
