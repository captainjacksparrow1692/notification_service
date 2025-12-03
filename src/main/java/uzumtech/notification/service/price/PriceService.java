package uzumtech.notification.service.price;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uzumtech.notification.entity.Price;
import uzumtech.notification.exception.notification.PriceNotFoundException;
import uzumtech.notification.repository.PriceRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final PriceRepository priceRepository;

    /**
     * Получить активный прайс (действующий в данный момент)
     */
    public Price getActivePrice() {
        return priceRepository.findByIsActiveTrue()
                .orElseThrow(() -> new PriceNotFoundException("Активный прайс не найден"));
    }

    /**
     * Цена за одно SMS на текущий момент
     */
    public Long getPricePerSms() {
        return getActivePrice().getPrice();
    }

    /**
     * Найти прайс, действовавший в момент отправки SMS (исторический поиск)
     */
    public Long getPriceAt(LocalDateTime dateTime) {
        return priceRepository.findPriceAt(dateTime)
                .orElseThrow(() -> new PriceNotFoundException(
                        "Не найден прайс на дату: " + dateTime
                ))
                .getPrice();
    }

    /**
     * Обновить цену: закрывает старую запись и создаёт новую.
     * Например, было 85 → станет 100.
     */
    @Transactional
    public Price updatePrice(Long newPriceValue) {

        // 1) Закрываем старую цену, если она существует
        priceRepository.findByIsActiveTrue().ifPresent(oldPrice -> {
            oldPrice.setActive(false);
            oldPrice.setEndDate(LocalDateTime.now());
            priceRepository.save(oldPrice);
        });

        // 2) Создаём новую цену
        Price newPrice = new Price();
        newPrice.setActive(true);
        newPrice.setPrice(newPriceValue);
        newPrice.setStartDate(LocalDateTime.now());
        newPrice.setEndDate(null); // действует сейчас

        return priceRepository.save(newPrice);
    }
}
