package uzumtech.notification.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uzumtech.notification.dto.NotificationSendRequestDto;
import uzumtech.notification.dto.ResponseDto;
import uzumtech.notification.entity.Notification;
import uzumtech.notification.mapper.NotificationMapper;
import uzumtech.notification.repository.MerchantRepository;
import uzumtech.notification.service.NotificationService;

/**
 * Контроллер для отправки уведомлений
 */
@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;
    private final MerchantRepository merchantRepository;

    /**
     * Отправить уведомление (добавляет в очередь Kafka)
     */
    @PostMapping("/send")
    public ResponseEntity<ResponseDto<Long>> sendNotification(@Valid @RequestBody NotificationSendRequestDto request) {
        log.info("Получен запрос на отправку уведомления: merchantId={}, type={}, receiver={}",
                request.getMerchantId(), request.getType(), request.getReceiver());

        // Преобразуем DTO в entity
        Notification notification = notificationMapper.toEntity(request, merchantRepository);

        // Сохраняем в БД и отправляем в Kafka
        Notification saved = notificationService.queue(notification);

        log.info("Уведомление добавлено в очередь: id={}, status={}", saved.getId(), saved.getStatus());

        // Возвращаем ID созданного уведомления
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDto.createSuccessResponse(saved.getId()));
    }

    // Получить уведомление по ID
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<Notification>> getNotification(@PathVariable Long id) {
        log.info("Получен запрос на получение уведомления: id={}", id);

        Notification notification = notificationService.findById(id);

        return ResponseEntity.ok(ResponseDto.createSuccessResponse(notification));
    }
}
