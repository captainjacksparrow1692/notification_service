package uzumtech.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uzumtech.notification.entity.Price;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PriceRepository extends JpaRepository<Price, Long> {

    /**
     * Получить текущую активную цену (end_date = null, is_active = true)
     */
    Optional<Price> findByIsActiveTrue();

    /**
     * Найти цену, которая действовала на момент отправки SMS.
     *
     * startDate <= datetime
     * и (endDate == null или endDate > datetime)
     */
    @Query("""
        SELECT p FROM Price p
        WHERE p.startDate <= :dateTime
          AND (p.endDate IS NULL OR p.endDate > :dateTime)
        ORDER BY p.startDate DESC
    """)
    Optional<Price> findPriceAt(LocalDateTime dateTime);
}
