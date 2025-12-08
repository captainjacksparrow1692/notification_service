package uzumtech.notification.dto.merchant;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for creating a merchant account.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MerchantCreateRequestDto {

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email format is invalid")
    private String email;

    @NotBlank(message = "Webhook URL must not be blank")
    private String webhook;

    @NotBlank(message = "Tax number must not be blank")
    private String taxNumber;

    @NotBlank(message = "Login must not be blank")
    @Size(min = 3, max = 50, message = "Login length must be between 3 and 50 characters")
    private String login;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 6, message = "Password length must be at least 6 characters")
    private String password;  // plain text password - will be encoded later

    public MerchantCreateRequestDto(String merchant1, String mail, String merchantCompany) {
        this.name = merchant1;
        this.email = mail;
        this.webhook = merchantCompany;
    }
}
