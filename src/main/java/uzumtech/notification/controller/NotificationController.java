package uzumtech.notification.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uzumtech.notification.dto.NotificationResponseDto;
import uzumtech.notification.dto.NotificationSendRequestDto;
import uzumtech.notification.dto.ResponseDto;
import uzumtech.notification.entity.Notification;
import uzumtech.notification.mapper.NotificationMapper;
import uzumtech.notification.service.NotificationService;

// REST-эндпоинты для работы с уведомлениями (логируются аспектом ControllerLoggingAspect)
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    // Принимаем запрос на отправку уведомления (сохранение + публикация в Kafka)
    @PostMapping("/send")
    public ResponseEntity<ResponseDto<Long>> sendNotification(@Valid @RequestBody NotificationSendRequestDto request) {
        Notification saved = notificationService.sendFromDto(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDto.createSuccessResponse(saved.getId()));
    }

    // Возвращаем уведомление по ID
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<NotificationResponseDto>> getNotification(@PathVariable Long id) {
        Notification notification = notificationService.findById(id);
        NotificationResponseDto responseDto = notificationMapper.toResponseDto(notification);

        return ResponseEntity.ok(ResponseDto.createSuccessResponse(responseDto));
    }
}
