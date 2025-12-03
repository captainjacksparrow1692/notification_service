package uzumtech.notification.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "prices")
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Активен ли текущий прайс.
     * active = true  → действует сейчас
     * active = false → используется только как историческая запись
     */
    @Column(nullable = false)
    private boolean isActive = true;

    /**
     * Цена за 1 SMS
     */
    @Column(nullable = false)
    private Long price;

    /**
     * Дата начала действия цены.
     * Например: 2025-01-01 00:00:00
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime startDate;

    /**
     * Дата окончания действия цены.
     * Если null — значит цена действует прямо сейчас
     */
    private LocalDateTime endDate;

    /**
     * Когда запись создана в базе.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Автоматическое заполнение createdAt и startDate
     */
    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (startDate == null) {
            startDate = createdAt;
        }
    }
}