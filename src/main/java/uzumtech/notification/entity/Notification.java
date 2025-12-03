package uzumtech.notification.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import uzumtech.notification.constant.enums.NotificationStatus;
import uzumtech.notification.constant.enums.NotificationType;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    @Column(name = "image_url")
    private String imageUrl;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="merchant_id", nullable = false)
    private Merchant merchant;

    @Column(nullable = false)
    private String receiverInfo;

    private String message;

    // Добавляем цену за сообщение
    @Column(nullable = false)
    private Long price;

    private LocalDateTime deliveredAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Геттер для recipient (чтобы использовать в PriceService)
    public String getRecipient() {
        return receiverInfo;
    }

    // Сеттер для recipient (чтобы можно было вызвать setRecipient)
    public void setRecipient(String recipient) {
        this.receiverInfo = recipient;
    }

    // Геттер для merchantId
    public Long getMerchantId() {
        return merchant != null ? merchant.getId() : null;
    }

    // Сеттер для merchant (если нужно создавать уведомления вручную)
    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }
}
