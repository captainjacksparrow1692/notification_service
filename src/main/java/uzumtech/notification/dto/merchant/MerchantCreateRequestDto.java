package uzumtech.notification.dto.merchant;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// DTO для создания нового мерчанта
// Клиент отправляет эти данные при регистрации
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MerchantCreateRequestDto {

    @NotBlank(message = "Name не может быть пустым")
    private String name;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Неверный формат email")
    private String email;

    @NotBlank(message = "Webhook не может быть пустым")
    private String webhook;

    @NotBlank(message = "TaxNumber не может быть пустым")
    private String taxNumber;

    @NotBlank(message = "Login не может быть пустым")
    @Size(min = 3, max = 50, message = "Login должен быть от 3 до 50 символов")
    private String login;

    @NotBlank(message = "Password не может быть пустым")
    @Size(min = 6, message = "Password должен быть минимум 6 символов")
    private String password;  // plain text пароль - будет хэшироваться в сервисе

    public MerchantCreateRequestDto(String merchant1, String mail, String merchantCompany) {
        this.name = merchant1;
        this.email = mail;
        this.webhook = merchantCompany;
    }
}
