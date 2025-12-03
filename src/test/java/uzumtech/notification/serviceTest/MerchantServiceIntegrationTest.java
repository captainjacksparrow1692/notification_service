package uzumtech.notification.serviceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import uzumtech.notification.dto.merchant.MerchantCreateRequestDto;
import uzumtech.notification.dto.merchant.MerchantUpdateRequestDto;
import uzumtech.notification.dto.merchant.MerchantResponseDto;
import uzumtech.notification.entity.Merchant;
import uzumtech.notification.exception.merchant.MerchantNotFoundException;
import uzumtech.notification.repository.MerchantRepository;
import uzumtech.notification.service.MerchantService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Сброс базы после каждого теста
@Transactional
public class MerchantServiceIntegrationTest {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MerchantRepository merchantRepository;

    private MerchantCreateRequestDto createDto;

    @BeforeEach
    void setup() {
        createDto = new MerchantCreateRequestDto();
        createDto.setLogin("merchant1");
        createDto.setEmail("merchant1@mail.com");
        createDto.setTaxNumber("123456789");
        createDto.setName("Merchant One");
        createDto.setPassword("password");
    }

    @Test
    void testCreateMerchant() {
        MerchantResponseDto response = merchantService.create(createDto);

        assertNotNull(response.getId());
        assertEquals(createDto.getLogin(), response.getLogin());
        assertEquals(createDto.getEmail(), response.getEmail());
    }

    @Test
    void testFindById() {
        MerchantResponseDto created = merchantService.create(createDto);
        MerchantResponseDto found = merchantService.findById(created.getId());

        assertEquals(created.getId(), found.getId());
        assertEquals(created.getLogin(), found.getLogin());
    }

    @Test
    void testFindByLogin() {
        merchantService.create(createDto);
        MerchantResponseDto found = merchantService.findByLogin("merchant1");

        assertEquals("merchant1", found.getLogin());
    }

    @Test
    void testFindByEmail() {
        merchantService.create(createDto);
        MerchantResponseDto found = merchantService.findByEmail("merchant1@mail.com");

        assertEquals("merchant1@mail.com", found.getEmail());
    }

    @Test
    void testUpdateMerchant() {
        MerchantResponseDto created = merchantService.create(createDto);

        MerchantUpdateRequestDto updateDto = new MerchantUpdateRequestDto();
        updateDto.setEmail("newemail@mail.com");
        updateDto.setTaxNumber("987654321");

        MerchantResponseDto updated = merchantService.update(created.getId(), updateDto);

        assertEquals("newemail@mail.com", updated.getEmail());
        assertEquals("987654321", updated.getTaxNumber());
    }

    @Test
    void testDeleteMerchant() {
        MerchantResponseDto created = merchantService.create(createDto);

        merchantService.delete(created.getId());

        assertThrows(MerchantNotFoundException.class,
                () -> merchantService.findById(created.getId()));
    }

    @Test
    void testExists() {
        MerchantResponseDto created = merchantService.create(createDto);

        assertTrue(merchantService.exists(created.getId()));
        assertFalse(merchantService.exists(999L));
    }

    @Test
    void testFindAll() {
        merchantService.create(createDto);

        assertFalse(merchantService.findAll().isEmpty());
    }
}

