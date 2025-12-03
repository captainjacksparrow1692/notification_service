package uzumtech.notification.service.price;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uzumtech.notification.dto.price.PriceCreateRequestDto;
import uzumtech.notification.dto.price.PriceResponseDto;
import uzumtech.notification.dto.price.PriceUpdateRequestDto;
import uzumtech.notification.entity.Price;
import uzumtech.notification.exception.notification.PriceNotFoundException;
import uzumtech.notification.mapper.PriceMapper;
import uzumtech.notification.repository.PriceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final PriceRepository priceRepository;
    private final PriceMapper priceMapper;

    // Получить активный прайс как Entity (например, 85 сум за 1 SMS)
    @Transactional(readOnly = true)
    public Price getActivePrice() {
        return priceRepository.findByIsActiveTrue()
                .orElseThrow(() -> new PriceNotFoundException("Активный прайс не найден"));
    }

    // Получить активный прайс как DTO
    @Transactional(readOnly = true)
    public PriceResponseDto getActivePriceDto() {
        Price activePrice = getActivePrice();
        return priceMapper.toResponseDto(activePrice);
    }

    // Создать новую цену (автоматически деактивирует старую активную цену)
    @Transactional
    public PriceResponseDto create(PriceCreateRequestDto dto) {
        // Деактивируем текущую активную цену, если есть
        priceRepository.findByIsActiveTrue().ifPresent(activePrice -> {
            activePrice.setActive(false);
            priceRepository.save(activePrice);
        });

        // Создаем новую активную цену
        Price price = priceMapper.toEntity(dto);
        Price saved = priceRepository.save(price);

        return priceMapper.toResponseDto(saved);
    }

    // Получить цену по ID
    @Transactional(readOnly = true)
    public PriceResponseDto findById(Long id) {
        Price price = priceRepository.findById(id)
                .orElseThrow(() -> new PriceNotFoundException("Price not found with id: " + id));
        return priceMapper.toResponseDto(price);
    }

    // Получить все цены
    @Transactional(readOnly = true)
    public List<PriceResponseDto> findAll() {
        return priceRepository.findAll().stream()
                .map(priceMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    // Обновить цену
    @Transactional
    public PriceResponseDto update(Long id, PriceUpdateRequestDto dto) {
        Price price = priceRepository.findById(id)
                .orElseThrow(() -> new PriceNotFoundException("Price not found with id: " + id));

        priceMapper.updateEntityFromDto(dto, price);
        Price updated = priceRepository.save(price);

        return priceMapper.toResponseDto(updated);
    }

    // Активировать цену (деактивирует все остальные)
    @Transactional
    public PriceResponseDto activate(Long id) {
        // Деактивируем все активные цены
        priceRepository.findByIsActiveTrue().ifPresent(activePrice -> {
            activePrice.setActive(false);
            priceRepository.save(activePrice);
        });

        // Активируем выбранную
        Price price = priceRepository.findById(id)
                .orElseThrow(() -> new PriceNotFoundException("Price not found with id: " + id));
        price.setActive(true);
        Price activated = priceRepository.save(price);

        return priceMapper.toResponseDto(activated);
    }

    // Деактивировать цену
    @Transactional
    public PriceResponseDto deactivate(Long id) {
        Price price = priceRepository.findById(id)
                .orElseThrow(() -> new PriceNotFoundException("Price not found with id: " + id));
        price.setActive(false);
        Price deactivated = priceRepository.save(price);

        return priceMapper.toResponseDto(deactivated);
    }

    // Удалить цену
    @Transactional
    public void delete(Long id) {
        if (!priceRepository.existsById(id)) {
            throw new PriceNotFoundException("Price not found with id: " + id);
        }
        priceRepository.deleteById(id);
    }
}
