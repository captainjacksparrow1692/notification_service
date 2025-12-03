package uzumtech.notification.dto.merchant;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// DTO для обновления данных мерчанта
// Все поля опциональные - обновляются только те, что переданы
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MerchantUpdateRequestDto {

    private String name;

    @Email(message = "Неверный формат email")
    private String email;

    private String webhook;

    private String taxNumber;



    public MerchantUpdateRequestDto(String newMerchant, String mail, String newCompany) {
        this.name = newMerchant;
        this.email = mail;
        this.webhook = newCompany;
    }

    // login и password нельзя обновлять через этот DTO
    // Для смены пароля используется отдельный endpoint
}
