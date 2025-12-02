package uzumtech.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uzumtech.notification.dto.merchant.MerchantCreateRequestDto;
import uzumtech.notification.dto.merchant.MerchantResponseDto;
import uzumtech.notification.dto.merchant.MerchantUpdateRequestDto;
import uzumtech.notification.entity.Merchant;
import uzumtech.notification.exception.merchant.MerchantNotFoundException;
import uzumtech.notification.repository.MerchantRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с мерчантами
 * Работает с DTO для безопасности данных
 */
@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepository merchantRepository;

    /**
     * Создать нового мерчанта из DTO
     */
    @Transactional
    public MerchantResponseDto create(MerchantCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("MerchantCreateRequestDto не может быть null");
        }

        // Проверка уникальности login
        if (merchantRepository.existsByLogin(dto.getLogin())) {
            throw new IllegalArgumentException("Merchant с таким login уже существует: " + dto.getLogin());
        }

        // Проверка уникальности email
        if (merchantRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Merchant с таким email уже существует: " + dto.getEmail());
        }

        // Проверка уникальности taxNumber
        if (merchantRepository.existsByTaxNumber(dto.getTaxNumber())) {
            throw new IllegalArgumentException("Merchant с таким taxNumber уже существует: " + dto.getTaxNumber());
        }

        // Создаем entity из DTO
        Merchant merchant = new Merchant();
        merchant.setName(dto.getName());
        merchant.setEmail(dto.getEmail());
        merchant.setWebhook(dto.getWebhook());
        merchant.setTaxNumber(dto.getTaxNumber());
        merchant.setLogin(dto.getLogin());

        // Хэшируем пароль (пока просто сохраняем как есть - потом добавите BCrypt)
        merchant.setPasswordHash(dto.getPassword());  // TODO: добавить BCryptPasswordEncoder

        Merchant saved = merchantRepository.save(merchant);
        return toResponseDto(saved);
    }

    /**
     * Получить мерчанта по ID
     */
    public MerchantResponseDto findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID не может быть null");
        }

        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new MerchantNotFoundException(id));

        return toResponseDto(merchant);
    }

    /**
     * Получить мерчанта по login
     */
    public MerchantResponseDto findByLogin(String login) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Login не может быть пустым");
        }

        Merchant merchant = merchantRepository.findByLogin(login)
                .orElseThrow(() -> new MerchantNotFoundException("Merchant not found with login: " + login));

        return toResponseDto(merchant);
    }

    /**
     * Получить мерчанта по email
     */
    public MerchantResponseDto findByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }

        Merchant merchant = merchantRepository.findByEmail(email)
                .orElseThrow(() -> new MerchantNotFoundException("Merchant not found with email: " + email));

        return toResponseDto(merchant);
    }

    /**
     * Получить всех мерчантов
     */
    public List<MerchantResponseDto> findAll() {
        return merchantRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Обновить мерчанта из DTO
     */
    @Transactional
    public MerchantResponseDto update(Long id, MerchantUpdateRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("ID не может быть null");
        }

        if (dto == null) {
            throw new IllegalArgumentException("MerchantUpdateRequestDto не может быть null");
        }

        // Находим существующего мерчанта
        Merchant existing = merchantRepository.findById(id)
                .orElseThrow(() -> new MerchantNotFoundException(id));

        // Обновляем только переданные поля
        if (dto.getName() != null && !dto.getName().isBlank()) {
            existing.setName(dto.getName());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            // Проверяем, не занят ли новый email другим мерчантом
            if (!existing.getEmail().equals(dto.getEmail()) &&
                merchantRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Email уже используется: " + dto.getEmail());
            }
            existing.setEmail(dto.getEmail());
        }

        if (dto.getWebhook() != null && !dto.getWebhook().isBlank()) {
            existing.setWebhook(dto.getWebhook());
        }

        if (dto.getTaxNumber() != null && !dto.getTaxNumber().isBlank()) {
            // Проверяем, не занят ли новый taxNumber другим мерчантом
            if (!existing.getTaxNumber().equals(dto.getTaxNumber()) &&
                merchantRepository.existsByTaxNumber(dto.getTaxNumber())) {
                throw new IllegalArgumentException("TaxNumber уже используется: " + dto.getTaxNumber());
            }
            existing.setTaxNumber(dto.getTaxNumber());
        }

        Merchant updated = merchantRepository.save(existing);
        return toResponseDto(updated);
    }

    /**
     * Удалить мерчанта по ID
     */
    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID не может быть null");
        }

        // Проверяем, существует ли мерчант
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new MerchantNotFoundException(id));

        merchantRepository.delete(merchant);
    }

    /**
     * Проверить существование мерчанта по ID
     */
    public boolean exists(Long id) {
        if (id == null) {
            return false;
        }
        return merchantRepository.existsById(id);
    }

    /**
     * Преобразовать Entity в ResponseDto
     * Скрывает passwordHash от клиента!
     */
    private MerchantResponseDto toResponseDto(Merchant merchant) {
        MerchantResponseDto dto = new MerchantResponseDto();
        dto.setId(merchant.getId());
        dto.setName(merchant.getName());
        dto.setEmail(merchant.getEmail());
        dto.setWebhook(merchant.getWebhook());
        dto.setTaxNumber(merchant.getTaxNumber());
        dto.setLogin(merchant.getLogin());
        dto.setCreatedAt(merchant.getCreatedAt());
        dto.setUpdatedAt(merchant.getUpdatedAt());
        // passwordHash НЕ копируем!
        return dto;
    }
}
