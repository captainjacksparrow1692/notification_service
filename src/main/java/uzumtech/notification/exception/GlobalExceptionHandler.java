package uzumtech.notification.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

//Глобальный обработчик исключений для всего приложения
//Перехватывает все исключения и возвращает единообразный формат ошибок
@RestControllerAdvice
public class GlobalExceptionHandler {

    //Обработка всех кастомных NotificationException и его наследников
    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<Map<String, Object>> handleNotificationException(NotificationException ex) {
        Map<String, Object> errorResponse = new HashMap<>();

        Map<String, Object> error = new HashMap<>();
        error.put("code", ex.getErrorCode());
        error.put("message", ex.getMessage());

        if (ex.getCause() != null) {
            Map<String, String> details = new HashMap<>();
            details.put("cause", ex.getCause().getMessage());
            error.put("details", details);
        }

        errorResponse.put("error", error);
        errorResponse.put("timestamp", LocalDateTime.now());

        return ResponseEntity
            .status(ex.getHttpStatus())
            .body(errorResponse);
    }

    //Обработка ошибок валидации от @Valid аннотации
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> errorResponse = new HashMap<>();

        Map<String, Object> error = new HashMap<>();
        error.put("code", "VALIDATION_ERROR");
        error.put("message", "Ошибка валидации данных");

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage())
        );
        error.put("details", fieldErrors);

        errorResponse.put("error", error);
        errorResponse.put("timestamp", LocalDateTime.now());

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse);
    }

    //Обработка всех остальных непредвиденных исключений
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> errorResponse = new HashMap<>();

        Map<String, Object> error = new HashMap<>();
        error.put("code", "INTERNAL_SERVER_ERROR");
        error.put("message", "Внутренняя ошибка сервера");

        Map<String, String> details = new HashMap<>();
        details.put("exception", ex.getClass().getSimpleName());
        details.put("cause", ex.getMessage());
        error.put("details", details);

        errorResponse.put("error", error);
        errorResponse.put("timestamp", LocalDateTime.now());

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse);
    }
}
