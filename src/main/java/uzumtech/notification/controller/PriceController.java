package uzumtech.notification.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uzumtech.notification.dto.ResponseDto;
import uzumtech.notification.dto.price.PriceCreateRequestDto;
import uzumtech.notification.dto.price.PriceResponseDto;
import uzumtech.notification.dto.price.PriceUpdateRequestDto;
import uzumtech.notification.service.price.PriceService;

import java.util.List;

/**
 * REST контроллер для управления ценами на SMS
 * CRUD операции + активация/деактивация цен
 */
@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class PriceController {

    private final PriceService priceService;

    /**
     * Создать новую цену
     * POST /api/prices
     * Автоматически деактивирует старую активную цену
     *
     * @param dto - данные новой цены
     * @return созданная цена (статус 201)
     */
    @PostMapping
    public ResponseEntity<ResponseDto<PriceResponseDto>> createPrice(@Valid @RequestBody PriceCreateRequestDto dto) {
        PriceResponseDto created = priceService.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDto.createSuccessResponse(created));
    }

    /**
     * Получить текущую активную цену
     * GET /api/prices/active
     *
     * @return активная цена (статус 200)
     */
    @GetMapping("/active")
    public ResponseEntity<ResponseDto<PriceResponseDto>> getActivePrice() {
        PriceResponseDto activePrice = priceService.getActivePriceDto();
        return ResponseEntity.ok(ResponseDto.createSuccessResponse(activePrice));
    }

    /**
     * Получить информацию о цене по ID
     * GET /api/prices/{id}
     *
     * @param id - ID цены
     * @return информация о цене (статус 200)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<PriceResponseDto>> getPriceById(@PathVariable Long id) {
        PriceResponseDto price = priceService.findById(id);
        return ResponseEntity.ok(ResponseDto.createSuccessResponse(price));
    }

    /**
     * Получить все цены
     * GET /api/prices
     *
     * @return список всех цен (статус 200)
     */
    @GetMapping
    public ResponseEntity<ResponseDto<List<PriceResponseDto>>> getAllPrices() {
        List<PriceResponseDto> prices = priceService.findAll();
        return ResponseEntity.ok(ResponseDto.createSuccessResponse(prices));
    }

    /**
     * Обновить цену
     * PUT /api/prices/{id}
     *
     * @param id - ID цены
     * @param dto - новые данные цены
     * @return обновленная цена (статус 200)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<PriceResponseDto>> updatePrice(
            @PathVariable Long id,
            @Valid @RequestBody PriceUpdateRequestDto dto) {
        PriceResponseDto updated = priceService.update(id, dto);
        return ResponseEntity.ok(ResponseDto.createSuccessResponse(updated));
    }

    /**
     * Активировать цену (деактивирует все остальные)
     * POST /api/prices/{id}/activate
     *
     * @param id - ID цены для активации
     * @return активированная цена (статус 200)
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<ResponseDto<PriceResponseDto>> activatePrice(@PathVariable Long id) {
        PriceResponseDto activated = priceService.activate(id);
        return ResponseEntity.ok(ResponseDto.createSuccessResponse(activated));
    }

    /**
     * Деактивировать цену
     * POST /api/prices/{id}/deactivate
     *
     * @param id - ID цены для деактивации
     * @return деактивированная цена (статус 200)
     */
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<ResponseDto<PriceResponseDto>> deactivatePrice(@PathVariable Long id) {
        PriceResponseDto deactivated = priceService.deactivate(id);
        return ResponseEntity.ok(ResponseDto.createSuccessResponse(deactivated));
    }

    /**
     * Удалить цену
     * DELETE /api/prices/{id}
     *
     * @param id - ID цены для удаления
     * @return пустой ответ (статус 204)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrice(@PathVariable Long id) {
        priceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
