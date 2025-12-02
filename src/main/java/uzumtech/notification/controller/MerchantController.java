package uzumtech.notification.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uzumtech.notification.dto.ResponseDto;
import uzumtech.notification.dto.merchant.MerchantCreateRequestDto;
import uzumtech.notification.dto.merchant.MerchantResponseDto;
import uzumtech.notification.dto.merchant.MerchantUpdateRequestDto;
import uzumtech.notification.service.MerchantService;

import java.util.List;

/**
 * REST контроллер для управления мерчантами
 * Базовые CRUD операции для работы с мерчантами
 */
@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class MerchantController {

    private final MerchantService merchantService;

    /**
     * Создать нового мерчанта
     * POST /api/merchants
     *
     * @param dto - данные мерчанта для создания
     * @return созданный мерчант (статус 201)
     */
    @PostMapping
    public ResponseEntity<ResponseDto<MerchantResponseDto>> createMerchant(@Valid @RequestBody MerchantCreateRequestDto dto) {
        MerchantResponseDto created = merchantService.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDto.createSuccessResponse(created));
    }

    /**
     * Получить информацию о мерчанте по ID
     * GET /api/merchants/{id}
     *
     * @param id - ID мерчанта
     * @return информация о мерчанте (статус 200)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<MerchantResponseDto>> getMerchantById(@PathVariable Long id) {
        MerchantResponseDto merchant = merchantService.findById(id);
        return ResponseEntity.ok(ResponseDto.createSuccessResponse(merchant));
    }

    /**
     * Получить мерчанта по login
     * GET /api/merchants/login/{login}
     *
     * @param login - login мерчанта
     * @return информация о мерчанте (статус 200)
     */
    @GetMapping("/login/{login}")
    public ResponseEntity<ResponseDto<MerchantResponseDto>> getMerchantByLogin(@PathVariable String login) {
        MerchantResponseDto merchant = merchantService.findByLogin(login);
        return ResponseEntity.ok(ResponseDto.createSuccessResponse(merchant));
    }

    /**
     * Получить мерчанта по email
     * GET /api/merchants/email/{email}
     *
     * @param email - email мерчанта
     * @return информация о мерчанте (статус 200)
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ResponseDto<MerchantResponseDto>> getMerchantByEmail(@PathVariable String email) {
        MerchantResponseDto merchant = merchantService.findByEmail(email);
        return ResponseEntity.ok(ResponseDto.createSuccessResponse(merchant));
    }

    /**
     * Получить всех мерчантов
     * GET /api/merchants
     *
     * @return список всех мерчантов (статус 200)
     */
    @GetMapping
    public ResponseEntity<ResponseDto<List<MerchantResponseDto>>> getAllMerchants() {
        List<MerchantResponseDto> merchants = merchantService.findAll();
        return ResponseEntity.ok(ResponseDto.createSuccessResponse(merchants));
    }

    /**
     * Обновить информацию о мерчанте
     * PUT /api/merchants/{id}
     *
     * @param id - ID мерчанта
     * @param dto - новые данные мерчанта
     * @return обновленный мерчант (статус 200)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<MerchantResponseDto>> updateMerchant(
            @PathVariable Long id,
            @Valid @RequestBody MerchantUpdateRequestDto dto) {
        MerchantResponseDto updated = merchantService.update(id, dto);
        return ResponseEntity.ok(ResponseDto.createSuccessResponse(updated));
    }

    /**
     * Удалить мерчанта
     * DELETE /api/merchants/{id}
     *
     * @param id - ID мерчанта для удаления
     * @return пустой ответ (статус 204)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMerchant(@PathVariable Long id) {
        merchantService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Проверить существование мерчанта
     * HEAD /api/merchants/{id}
     *
     * @param id - ID мерчанта
     * @return статус 200 если существует, 404 если нет
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkMerchantExists(@PathVariable Long id) {
        boolean exists = merchantService.exists(id);
        return exists ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
