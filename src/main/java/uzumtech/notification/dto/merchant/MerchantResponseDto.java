package uzumtech.notification.dto.merchant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO для ответа клиенту с информацией о мерчанте
 * НЕ содержит passwordHash - только публичные данные
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MerchantResponseDto {

    private Long id;

    private String name;

    private String email;

    private String webhook;

    private String taxNumber;

    private String login;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // passwordHash НЕ включен - защита от утечки!
}
