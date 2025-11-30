package uzumtech.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseDto<T> {
    private String message;
    private boolean success;
    private T data;

    public static <T> ResponseDto<T> createSuccessResponse(T data) {
        return new ResponseDto<>("", true, data);
    }

    public static <T> ResponseDto<T> createErrorResponse(String message) {
        return new ResponseDto<>(message, false, null);
    }
}
