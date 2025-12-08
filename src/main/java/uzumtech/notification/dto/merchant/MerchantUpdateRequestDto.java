package uzumtech.notification.dto.merchant;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for updating merchant information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MerchantUpdateRequestDto {

    private String name;

    @Email(message = "Email format is invalid")
    private String email;

    private String webhook;

    private String taxNumber;

    public MerchantUpdateRequestDto(String newMerchant, String mail, String newCompany) {
        this.name = newMerchant;
        this.email = mail;
        this.webhook = newCompany;
    }
}
